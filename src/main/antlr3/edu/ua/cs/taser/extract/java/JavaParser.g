parser grammar JavaParser;

options {
    backtrack = true;
    language = Java;
    memoize = true;
    superClass = BaseJavaParser;
    tokenVocab = JavaLexer;
}

scope RefTypeBody {
    Position start;
}

@header {
package edu.ua.cs.taser.javatext;

import edu.ua.cs.taser.document.EntityType;
import edu.ua.cs.taser.document.Entities;
import edu.ua.cs.taser.document.JavaEntity;
import edu.ua.cs.taser.io.Filenames;
import edu.ua.cs.taser.token.JavaToken;
import edu.ua.cs.taser.token.TokenType;
import edu.ua.cs.taser.util.Stacks;
import edu.ua.cs.taser.util.position.Position;
import edu.ua.cs.taser.util.position.Positions;

import java.io.File;
}

@rulecatch {
    catch (RecognitionException e) {
        //System.err.print(getRuleInvocationStack());
        throw e;
    }
}

compilationUnit[final String filename, final EntityType type] returns [List<JavaToken> lstok]
@init {
    JavaEntity cue = Entities.newJavaEntity(type, null, filename, Positions.position(0, 0));
    Stacks.push(entityStack, cue);
}
@after {
    cUnit.append(".").append(Filenames.getBasename(filename, "java"));

    Stacks.pop(entityStack);
    Token firstInputToken = input.get(0);
    int lastIndex = input.size() - 2;
    while (lastIndex < 0) ++lastIndex;
    Token lastInputToken = input.get(lastIndex);
    String lastInputTokenText = lastInputToken.getText();
    int end = lastInputTokenText.length();
    while (end != 0 && Character.isWhitespace(lastInputTokenText.charAt(end - 1))) {
        end--;
    }
    lastInputTokenText = lastInputTokenText.substring(0, end);
    Position cueEnd = Positions.position(
        lastInputToken.getLine(),
        lastInputToken.getCharPositionInLine() + lastInputTokenText.length()
        );
    cue.setPosition(cue.getPosition().withEnd(cueEnd));
    addEntity(cue);
    connectCommentsToEntities();
    $lstok = tokens;
}
    :   annotations
        (   packageDeclaration importDeclaration* typeDeclaration*
        |   classOrInterfaceDeclaration typeDeclaration*
        )
    |   packageDeclaration? importDeclaration* typeDeclaration*
    ;

packageDeclaration
@init {
    StringBuilder pkgName = new StringBuilder();
}
    :   PACKAGE pkgTop=Identifier
            {
            pkgName.append($pkgTop.getText());
            cUnit.append($pkgTop.getText());
            }
        (DOT pkgSub=Identifier
            {
            pkgName.append(".").append($pkgSub.getText());
            cUnit.append(".").append($pkgSub.getText());
            }
        )*
            {
            Stacks.push(qualifiedNameStack, pkgName.toString());
            }
        SEMICOLON
    ;

importDeclaration
    :   IMPORT STATIC? Identifier (DOT Identifier)* (DOT MUL)? SEMICOLON
    ;

typeDeclaration
    :   classOrInterfaceDeclaration
    |   SEMICOLON
    ;

classOrInterfaceDeclaration
    :
            {
            JavaEntity top = Stacks.top(entityStack);
            Stacks.push(entityStack, Entities.newJavaEntity(EntityType.CLASS, Stacks.top(entityStack)));
            }
        classOrInterfaceModifiers (classDeclaration | interfaceDeclaration)
            {
            addEntity(Stacks.pop(entityStack));
            Stacks.pop(qualifiedNameStack);
            }
    ;

classOrInterfaceModifiers
    :   classOrInterfaceModifier*
    ;

classOrInterfaceModifier
@after {
    if ($mod != null) {
        // Found modifier
        Position modPos = positionOf($mod);
        if ($RefTypeBody.size() == 0) {
            // Not nested in a reference type body
            JavaEntity top = Stacks.top(entityStack);
            Position topPos = top.getPosition();
            if (modPos.precedes(topPos)) {
                // First modifier
                top.setPosition(topPos.withStart(modPos));
            }
        } else {
            // Nested in a reference type body
            if (modPos.precedes($RefTypeBody::start)) {
                // First modifier
                $RefTypeBody::start = modPos;
            }
        }
    }
}
    :   annotation
    |   mod=PUBLIC
    |   mod=PROTECTED
    |   mod=PRIVATE
    |   mod=ABSTRACT
    |   mod=STATIC
    |   mod=FINAL
    |   mod=STRICTFP
    ;

modifiers
    :   modifier*
    ;

classDeclaration
    :   normalClassDeclaration
    |   enumDeclaration
    ;

normalClassDeclaration
    :   k=CLASS i=Identifier
        {
            Position kwPos = positionOf($k);
            JavaEntity top = Stacks.top(entityStack);
            Position topPos = top.getPosition();
            if (kwPos.precedes(topPos)) {
                top.setPosition(topPos.withStart(kwPos));
            }
            String text = $i.getText();
            Stacks.push(qualifiedNameStack, text);
            top.setName(createQualifiedName());
            addToken(makeToken($i, TokenType.CLASS_NAME, top, text));
        }
        (typeParameters
            {
            appendString(createTypeParametersString());
            }
        )?  (EXTENDS type)?  (IMPLEMENTS typeList)?  classBody
    ;

typeParameters
    :   o=LT typeParameter (COMMA typeParameter)* GT
            {
                if ($RefTypeBody.size() > 0) {
                    Position ltPos = positionOf($o);
                    if (ltPos.precedes($RefTypeBody::start)) {
                        $RefTypeBody::start = ltPos;
                    }
                }
            }
    ;

typeParameter
    :   i=Identifier
            {
            String text = $i.getText();
            addToken(makeToken($i, TokenType.TYPE_PARAMETER_NAME, Stacks.top(entityStack), text));
            typeParameters.add(text);
            }
        (EXTENDS typeBound)?
    ;

typeBound
    :   type (BITAND type)*
    ;

enumDeclaration
    :   ENUM i=Identifier
            {
            String text = $i.getText();
            Stacks.push(qualifiedNameStack, text);
            JavaEntity top = Stacks.top(entityStack);
            top.setType(EntityType.ENUM);
            top.setName(createQualifiedName());
            addToken(makeToken($i, TokenType.ENUM_NAME, top, text));
            }
        (IMPLEMENTS typeList)? enumBody
    ;

enumBody
    :   o=LBRACE enumConstants? COMMA? enumBodyDeclarations? c=RBRACE
            {
            Stacks.top(entityStack).setPosition(rangePositionOf($o, $c));
            }
    ;

enumConstants
    :   enumConstant (COMMA enumConstant)*
    ;

enumConstant
    :   annotations? i=Identifier
            {
            addToken(makeToken($i, TokenType.ENUM_CONSTANT_NAME, Stacks.top(entityStack)));
            }
        arguments? classBody?
    ;

enumBodyDeclarations
    :   SEMICOLON (classBodyDeclaration)*
    ;

interfaceDeclaration
@init {
    JavaEntity top = Stacks.top(entityStack);
    top.setType(EntityType.INTERFACE);
}
    :   normalInterfaceDeclaration
    |   annotationTypeDeclaration
    ;

normalInterfaceDeclaration
    :   n=INTERFACE i=Identifier
            {
            Position interfacePos = positionOf($n);
            JavaEntity top = Stacks.top(entityStack);
            Position topPos = top.getPosition();
            if (interfacePos.precedes(topPos)) {
                top.setPosition(topPos.withStart(interfacePos));
            }
            String text = $i.getText();
            Stacks.push(qualifiedNameStack, text);
            top.setName(createQualifiedName());
            addToken(makeToken($i, TokenType.INTERFACE_NAME, top, text));
            }
        (typeParameters
            {
            appendString(createTypeParametersString());
            }
        )? (EXTENDS typeList)? interfaceBody
    ;

typeList
    :   type (COMMA type)*
    ;

classBody returns [Position pos]
    :
            {
            Stacks.push(anonStack, 1);
            }
        o=LBRACE
            {
                JavaEntity top = Stacks.top(entityStack);
                Position topPos = top.getPosition();
                top.setPosition(topPos.withStart(
                    findFirstPosition(
                        ($RefTypeBody.size() > 0) ? $RefTypeBody::start : Positions.tailPosition(),
                        (topPos.isDefined() ? topPos.getStart() : topPos),
                        positionOf($o)
                    )
                ));
            }
            classBodyDeclaration* c=RBRACE
            {
            Stacks.pop(anonStack);
            JavaEntity top = Stacks.top(entityStack);
            top.setPosition(top.getPosition().withEnd(positionOf($c)));

            $pos = rangePositionOf($o, $c);
            }
    ;

interfaceBody
    :
            {
            Stacks.push(anonStack, 1);
            }
        o=LBRACE
            {
                JavaEntity top = Stacks.top(entityStack);
                Position topPos = top.getPosition();
                top.setPosition(topPos.withStart(
                    findFirstPosition(
                        ($RefTypeBody.size() > 0) ? $RefTypeBody::start : Positions.tailPosition(),
                        (topPos.isDefined() ? topPos.getStart() : topPos),
                        positionOf($o)
                    )
                ));
            }
        interfaceBodyDeclaration* c=RBRACE
            {
            Stacks.pop(anonStack);
            JavaEntity top = Stacks.top(entityStack);
            top.setPosition(top.getPosition().withEnd(positionOf($c)));
            }
    ;

classBodyDeclaration
scope RefTypeBody;
@init {
    $RefTypeBody::start = Positions.tailPosition();
}
    :   SEMICOLON
    |   STATIC? block
    |   modifiers memberDecl
    ;

memberDecl
    :   genericMethodOrConstructorDecl
    |   memberDeclaration
    |   v=VOID i=Identifier
            {
                Position voidPos = positionOf($v);
                if (voidPos.precedes($RefTypeBody::start)) {
                    // No modifier
                    $RefTypeBody::start = voidPos;
                }
                String text = $i.getText();
                Stacks.push(qualifiedNameStack, text);
                JavaEntity entity = Entities.newJavaEntity(EntityType.METHOD, Stacks.top(entityStack), createQualifiedName());
                Stacks.push(entityStack, entity);
                addToken(makeToken($i, TokenType.METHOD_NAME, entity, text));
            }
        voidMethodDeclaratorRest
            {
                addEntity(Stacks.pop(entityStack));
                Stacks.pop(qualifiedNameStack);
            }
    |   i=Identifier
        {
            Position iPos = positionOf($i);
            if (iPos.precedes($RefTypeBody::start)) {
                // No modifier
                $RefTypeBody::start = iPos;
            }
            String text = $i.getText();
            Stacks.push(qualifiedNameStack, text);
            JavaEntity entity = Entities.newJavaEntity(EntityType.METHOD, Stacks.top(entityStack), createQualifiedName());
            Stacks.push(entityStack, entity);
            addToken(makeToken($i, TokenType.METHOD_NAME, entity, text));
        }
        constructorDeclaratorRest
        {
            addEntity(Stacks.pop(entityStack));
            Stacks.pop(qualifiedNameStack);
        }
    |   {
            Stacks.push(entityStack, Entities.newJavaEntity(EntityType.INTERFACE, Stacks.top(entityStack)));

        }
        interfaceDeclaration
        {
            addEntity(Stacks.pop(entityStack));
            Stacks.pop(qualifiedNameStack);
        }
    |   {
            Stacks.push(entityStack, Entities.newJavaEntity(EntityType.CLASS, Stacks.top(entityStack)));
        }
        classDeclaration
        {
            addEntity(Stacks.pop(entityStack));
            Stacks.pop(qualifiedNameStack);
        }
    ;

memberDeclaration
    :
            {
            Stacks.push(entityStack, Entities.newJavaEntity(EntityType.METHOD, Stacks.top(entityStack)));
            }
        type methodDeclaration
    |   type fieldDeclaration
    ;

genericMethodOrConstructorDecl
    :
            {
            Stacks.push(entityStack, Entities.newJavaEntity(EntityType.METHOD, Stacks.top(entityStack)));
            }
        typeParameters genericMethodOrConstructorRest
            {
            addEntity(Stacks.pop(entityStack));
            Stacks.pop(qualifiedNameStack);
            }
    ;

genericMethodOrConstructorRest
    :   (type | v=VOID
            {
                Position voidPos = positionOf($v);
                if (voidPos.precedes($RefTypeBody::start)) {
                    // No modifier
                    $RefTypeBody::start = voidPos;
                }
            }
        ) i=Identifier
            {
            String text = $i.getText();
            Stacks.push(qualifiedNameStack, text);
            JavaEntity top = Stacks.top(entityStack);
            top.setName(createQualifiedName());
            addToken(makeToken($i, TokenType.METHOD_NAME, top, text));
            }
        methodDeclaratorRest
    |   i=Identifier
            {
                Position iPos = positionOf($i);
                if (iPos.precedes($RefTypeBody::start)) {
                    // No modifier
                    $RefTypeBody::start = iPos;
                }
                String text = $i.getText();
                Stacks.push(qualifiedNameStack, text);
                JavaEntity top = Stacks.top(entityStack);
                top.setName(createQualifiedName());
                addToken(makeToken($i, TokenType.METHOD_NAME, top, text));
            }
        constructorDeclaratorRest
    ;

methodDeclaration
    :   i=Identifier
            {
            String text = $i.getText();
            Stacks.push(qualifiedNameStack, text);
            JavaEntity top = Stacks.top(entityStack);
            top.setName(createQualifiedName());
            addToken(makeToken($i, TokenType.METHOD_NAME, top, text));
            }
        methodDeclaratorRest
            {
            addEntity(Stacks.pop(entityStack));
            Stacks.pop(qualifiedNameStack);
            }
    ;

fieldDeclaration
    :   variableDeclarators SEMICOLON
    ;

interfaceBodyDeclaration
scope RefTypeBody;
@init {
    $RefTypeBody::start = Positions.tailPosition();
}
    :   modifiers interfaceMemberDecl
    |   SEMICOLON
    ;

interfaceMemberDecl
    :   interfaceMethodOrFieldDecl
    |   interfaceGenericMethodDecl
    |
            {
            Stacks.push(entityStack, Entities.newJavaEntity(EntityType.METHOD, Stacks.top(entityStack)));
            }
        v=VOID i=Identifier
            {
                Position voidPos = positionOf($v);
                if (voidPos.precedes($RefTypeBody::start)) {
                    // No modifier
                    $RefTypeBody::start = voidPos;
                }
                String text = $i.getText();
                Stacks.push(qualifiedNameStack, text);
                JavaEntity top = Stacks.top(entityStack);
                top.setName(createQualifiedName());
                addToken(makeToken($i, TokenType.METHOD_NAME, top, text));
            }
        voidInterfaceMethodDeclaratorRest
            {
            addEntity(Stacks.pop(entityStack));
            Stacks.pop(qualifiedNameStack);
            }
    |
            {
            Stacks.push(entityStack, Entities.newJavaEntity(EntityType.INTERFACE, Stacks.top(entityStack)));
            }
        interfaceDeclaration
            {
            addEntity(Stacks.pop(entityStack));
            Stacks.pop(qualifiedNameStack);
            }
    |
            {
            Stacks.push(entityStack, Entities.newJavaEntity(EntityType.CLASS, Stacks.top(entityStack)));
            }
        classDeclaration
            {
            addEntity(Stacks.pop(entityStack));
            Stacks.pop(qualifiedNameStack);
            }
    ;

interfaceMethodOrFieldDecl
    :   type i=Identifier
            {
                addToken(makeToken($i, TokenType.FIELD_NAME, Stacks.top(entityStack)));
            }
        constantDeclaratorsRest SEMICOLON
            {
                $RefTypeBody::start = Positions.tailPosition();
            }
    |
            {
            Stacks.push(entityStack, Entities.newJavaEntity(EntityType.METHOD, Stacks.top(entityStack)));
            }
        type i=Identifier
            {
            String text = $i.getText();
            Stacks.push(qualifiedNameStack, text);
            JavaEntity top = Stacks.top(entityStack);
            top.setName(createQualifiedName());
            addToken(makeToken($i, TokenType.METHOD_NAME, top, text));
            }
        interfaceMethodDeclaratorRest
            {
            addEntity(Stacks.pop(entityStack));
            Stacks.pop(qualifiedNameStack);
            }
    ;

methodDeclaratorRest
    :   formalParameters (LBRACKET RBRACKET)*
            {
                appendString(createFormalsString());
            }
        (THROWS qualifiedNameList)?
        (   methodBody
        |   c=SEMICOLON
                {
                    JavaEntity top = Stacks.top(entityStack);
                    top.setPosition($RefTypeBody::start.withEnd(positionOf($c)));
                    $RefTypeBody::start = Positions.tailPosition();
                }
        )
    ;

voidMethodDeclaratorRest
    :   formalParameters
            {
                appendString(createFormalsString());
            }
        (THROWS qualifiedNameList)?
        (   methodBody
        |   c=SEMICOLON
                {
                    JavaEntity top = Stacks.top(entityStack);
                    top.setPosition($RefTypeBody::start.withEnd(positionOf($c)));
                    $RefTypeBody::start = Positions.tailPosition();
                }
        )
    ;

interfaceMethodDeclaratorRest
    :   formalParameters
            {
                appendString(createFormalsString());
            }
        (LBRACKET RBRACKET)* (THROWS qualifiedNameList)? c=SEMICOLON
            {
                JavaEntity top = Stacks.top(entityStack);
                top.setPosition($RefTypeBody::start.withEnd(positionOf($c)));
                $RefTypeBody::start = Positions.tailPosition();
            }
    ;

interfaceGenericMethodDecl
    :
            {
            Stacks.push(entityStack, Entities.newJavaEntity(EntityType.METHOD, Stacks.top(entityStack)));
            }
        typeParameters (type | VOID) i=Identifier
            {
            String text = $i.getText();
            Stacks.push(qualifiedNameStack, text);
            JavaEntity top = Stacks.top(entityStack);
            top.setName(createQualifiedName());
            addToken(makeToken($i, TokenType.METHOD_NAME, top, text));
            }
        interfaceMethodDeclaratorRest
            {
            addEntity(Stacks.pop(entityStack));
            Stacks.pop(qualifiedNameStack);
            }
    ;

voidInterfaceMethodDeclaratorRest
    :   formalParameters
            {
            appendString(createFormalsString());
            }
        (THROWS qualifiedNameList)? c=SEMICOLON
            {
                JavaEntity top = Stacks.top(entityStack);
                top.setPosition($RefTypeBody::start.withEnd(positionOf($c)));
                $RefTypeBody::start = Positions.tailPosition();
            }
    ;

constructorDeclaratorRest
    :   formalParameters
            {
            appendString(createFormalsString());
            }
        (THROWS qualifiedNameList)? constructorBody
    ;

constantDeclarator
    :   i=Identifier
            {
            addToken(makeToken($i, TokenType.FIELD_NAME, Stacks.top(entityStack)));
            }
        constantDeclaratorRest
    ;

variableDeclarators
    :   variableDeclarator (COMMA variableDeclarator)*
    ;

variableDeclarator
    :   variableDeclaratorId
            {
            lastVariableDeclaratorId = null;
            }
        (ASG variableInitializer)?
    ;

constantDeclaratorsRest
    :   constantDeclaratorRest (COMMA constantDeclarator)*
    ;

constantDeclaratorRest
    :   (LBRACKET RBRACKET)* ASG variableInitializer
    ;

variableDeclaratorId
    :   i=Identifier
            {
            JavaEntity entity = Stacks.top(entityStack);
            TokenType type = (entity.getType().isClassType()) ? TokenType.FIELD_NAME : TokenType.LOCAL_VARIABLE_NAME;
            lastVariableDeclaratorId = makeToken($i, type, entity);
            addToken(lastVariableDeclaratorId);
            }
        (LBRACKET RBRACKET)*
    ;

variableInitializer
    :   arrayInitializer
    |   expression
    ;

arrayInitializer
    :   o=LBRACE (variableInitializer (COMMA variableInitializer)* (COMMA)? )? c=RBRACE
    ;

modifier
@after {
    if ($mod != null) {
        // Found modifier
        Position modPos = positionOf($mod);
        if ($RefTypeBody.size() == 0) {
            // Not nested in a reference type body
            JavaEntity top = Stacks.top(entityStack);
            Position topPos = top.getPosition();
            if (modPos.precedes(topPos)) {
                // First modifier
                top.setPosition(topPos.withStart(modPos));
            }
        } else {
            // Nested in a reference type body
            if (modPos.precedes($RefTypeBody::start)) {
                // First modifier
                $RefTypeBody::start = modPos;
            }
        }
    }
}
    :   annotation
    |   mod=PUBLIC
    |   mod=PROTECTED
    |   mod=PRIVATE
    |   mod=STATIC
    |   mod=ABSTRACT
    |   mod=FINAL
    |   mod=NATIVE
    |   mod=SYNCHRONIZED
    |   mod=TRANSIENT
    |   mod=VOLATILE
    |   mod=STRICTFP
    ;

enumConstantName
    :   i=Identifier
            {
            addToken(makeToken($i, TokenType.ENUM_CONSTANT_REF, Stacks.top(entityStack)));
            }
    ;

type returns [String name]
    :   t=classOrInterfaceType
            { $name = $t.name; }
        (LBRACKET
            { $name += "["; }
        RBRACKET
            { $name += "]"; }
        )*
    |   t=primitiveType
            { $name = $t.name; }
        (LBRACKET
            { $name += "["; }
        RBRACKET
            { $name += "]"; }
        )*
    ;

classOrInterfaceType returns [String name]
    :   i=Identifier
            {
                if ($RefTypeBody.size() > 0) {
                    // Nested in a reference type body
                    Position iPos = positionOf($i);
                    if (iPos.precedes($RefTypeBody::start)) {
                        // No modifier
                        $RefTypeBody::start = iPos;
                    }
                }
                addToken(makeToken($i, TokenType.CLASS_OR_INTERFACE_REF, Stacks.top(entityStack)));
                $name = $i.getText();
            }
        (typeArguments
            {
            String gs = createTypeArgumentsString();
            $name += gs;
            }
        )? (DOT j=Identifier
            {
            String text = $j.getText();
            addToken(makeToken($j, TokenType.CLASS_OR_INTERFACE_REF, Stacks.top(entityStack), text));
            $name += ("." + text);
            }
        (typeArguments
            {
            String gs = createTypeArgumentsString();
            $name += gs;
            }
        )? )*
    ;

primitiveType returns [String name]
@after {
    if ($RefTypeBody.size() > 0) {
        Position typePos = positionOf($t);
        if (typePos.precedes($RefTypeBody::start)) {
            // No modifier
            $RefTypeBody::start = typePos;
        }
    }
}
    :   t=BOOLEAN
        { $name = "boolean"; }
    |   t=CHAR
        { $name = "char"; }
    |   t=BYTE
        { $name = "byte"; }
    |   t=SHORT
        { $name = "short"; }
    |   t=INT
        { $name = "int"; }
    |   t=LONG
        { $name = "long"; }
    |   t=FLOAT
        { $name = "float"; }
    |   t=DOUBLE
        { $name = "double"; }
    ;

variableModifier
    :   FINAL
    |   annotation
    ;

typeArguments
    :   LT typeArgument (COMMA typeArgument)* GT
    ;

typeArgument
    :   t=type
            {
            typeArguments.add($t.name);
            }
    |   QUESTIONMARK ((EXTENDS | SUPER) type)?
    ;

qualifiedNameList
    :   qualifiedName (COMMA qualifiedName)*
    ;

formalParameters
    :   o=LPAREN formalParameterDecls? c=RPAREN
    ;

formalParameterDecls
    :   variableModifiers
            {
            isCollectingTypeParts = true;
            }
        t=type
            {
            Stacks.push(formalsStack, $t.name);
            isCollectingTypeParts = false;
            for (JavaToken token : typeParts) {
                token.setType(TokenType.PARAMETER_TYPE);
            }
            typeParts.clear();
            }
        formalParameterDeclsRest
    ;

formalParameterDeclsRest
    :   variableDeclaratorId
            {
            if (lastVariableDeclaratorId != null) {
                lastVariableDeclaratorId.setType(TokenType.PARAMETER_NAME);
                lastVariableDeclaratorId = null;
            }
            }
        (COMMA formalParameterDecls)?
    |   ELLIPSIS variableDeclaratorId
            {
            if (lastVariableDeclaratorId != null) {
                lastVariableDeclaratorId.setType(TokenType.PARAMETER_NAME);
                lastVariableDeclaratorId = null;
            }
            }
    ;

methodBody
    :   block
    ;

constructorBody
    :   o=LBRACE
            {
                JavaEntity top = Stacks.top(entityStack);
                Position topPos = top.getPosition();
                top.setPosition(topPos.withStart(
                    findFirstPosition(
                        $RefTypeBody::start,
                        (topPos.isDefined() ? topPos.getStart() : topPos),
                        positionOf($o)
                    )
                ));
                $RefTypeBody::start = Positions.tailPosition();
            }
        explicitConstructorInvocation? blockStatement* c=RBRACE
            {
            JavaEntity top = Stacks.top(entityStack);
            top.setPosition(top.getPosition().withEnd(positionOf($c)));
            }
    ;

explicitConstructorInvocation
    :   nonWildcardTypeArguments? (THIS | SUPER) arguments SEMICOLON
    |   primary DOT nonWildcardTypeArguments? SUPER arguments SEMICOLON
    ;


qualifiedName
    :   i=Identifier
            {
            addToken(makeToken($i, TokenType.THROWS, Stacks.top(entityStack)));
            }
        (DOT j=Identifier
            {
            addToken(makeToken($j, TokenType.THROWS, Stacks.top(entityStack)));
            }
        )*
    ;

literal 
    :   integerLiteral
    |   FloatingPointLiteral
    |   CharacterLiteral
    |   s=StringLiteral
            {
            addToken(makeToken($s, TokenType.STRING_LITERAL, Stacks.top(entityStack)));
            }
    |   booleanLiteral
    |   NULL
    ;

integerLiteral
    :   HexLiteral
    |   OctalLiteral
    |   DecimalLiteral
    ;

booleanLiteral
    :   TRUE
    |   FALSE
    ;

// ANNOTATIONS

annotations
    :   annotation+
    ;

annotation
    :   AT annotationName ( LPAREN ( elementValuePairs | elementValue )? RPAREN )?
    ;

annotationName
    :   i=Identifier
            {
            addToken(makeToken($i, TokenType.ANNOTATION_REF, Stacks.top(entityStack)));
            }
        (DOT j=Identifier
            {
            addToken(makeToken($j, TokenType.ANNOTATION_REF, Stacks.top(entityStack)));
            }
        )*
    ;

elementValuePairs
    :   elementValuePair (COMMA elementValuePair)*
    ;

elementValuePair
    :   i=Identifier
            {
            addToken(makeToken($i, TokenType.ANNOTATION_ELEMENT_REF, Stacks.top(entityStack)));
            }
        ASG elementValue
    ;

elementValue
    :   conditionalExpression
    |   annotation
    |   elementValueArrayInitializer
    ;

elementValueArrayInitializer
    :   o=LBRACE (elementValue (COMMA elementValue)*)? (COMMA)? c=RBRACE
    ;

annotationTypeDeclaration
    :   AT INTERFACE i=Identifier
            {
            String text = $i.getText();
            Stacks.push(qualifiedNameStack, text);
            JavaEntity top = Stacks.top(entityStack);
            top.setName(createQualifiedName());
            addToken(makeToken($i, TokenType.ANNOTATION_NAME, top, text));
            }
        annotationTypeBody
    ;

annotationTypeBody
    :   o=LBRACE (annotationTypeElementDeclaration)* c=RBRACE
            {
            Stacks.top(entityStack).setPosition(rangePositionOf($o, $c));
            }
    ;

annotationTypeElementDeclaration
    :   modifiers annotationTypeElementRest
    ;

annotationTypeElementRest
    :   type annotationMethodOrConstantRest SEMICOLON
    |   normalClassDeclaration SEMICOLON?
    |   normalInterfaceDeclaration SEMICOLON?
    |   enumDeclaration SEMICOLON?
    |   annotationTypeDeclaration SEMICOLON?
    ;

annotationMethodOrConstantRest
    :   annotationMethodRest
    |   annotationConstantRest
    ;

annotationMethodRest
    :   i=Identifier
            {
            addToken(makeToken($i, TokenType.ANNOTATION_ELEMENT_NAME, Stacks.top(entityStack)));
            }
        LPAREN RPAREN defaultValue?
    ;

annotationConstantRest
    :   variableDeclarators
    ;

defaultValue
    :   DEFAULT elementValue
    ;

// STATEMENTS / BLOCKS

block
    :   o=LBRACE
            {
                JavaEntity top = Stacks.top(entityStack);
                Position topPos = top.getPosition();
                top.setPosition(topPos.withStart(
                    findFirstPosition(
                        $RefTypeBody::start,
                        (topPos.isDefined() ? topPos.getStart() : topPos),
                        positionOf($o)
                    )
                ));
                $RefTypeBody::start = Positions.tailPosition();
            }
        blockStatement* c=RBRACE
            {
            JavaEntity top = Stacks.top(entityStack);
            top.setPosition(top.getPosition().withEnd(positionOf($c)));
            }
    ;

blockStatement
    :   localVariableDeclarationStatement
    |   classOrInterfaceDeclaration
    |   statement
    ;

localVariableDeclarationStatement
    :    localVariableDeclaration SEMICOLON
    ;

localVariableDeclaration
    :   variableModifiers type variableDeclarators
    ;

variableModifiers
    :   variableModifier*
    ;

statement
    : block
    |   ASSERT expression (COLON expression)? SEMICOLON
    |   IF parExpression statement (options {k=1;}:ELSE statement)?
    |   FOR o=LPAREN forControl c=RPAREN
        statement
    |   WHILE parExpression statement
    |   DO statement WHILE parExpression SEMICOLON
    |   TRY block
        ( catches FINALLY block
        | catches
        |   FINALLY block
        )
    |   SWITCH parExpression o=LBRACE switchBlockStatementGroups c=RBRACE
    |   SYNCHRONIZED parExpression block
    |   RETURN expression? SEMICOLON
    |   THROW expression SEMICOLON
    |   BREAK   ( i=Identifier
            {
            addToken(makeToken($i, TokenType.LABEL_REF, Stacks.top(entityStack)));
            }
        )? SEMICOLON
    |   CONTINUE ( i=Identifier
            {
            addToken(makeToken($i, TokenType.LABEL_REF, Stacks.top(entityStack)));
            }
        )? SEMICOLON
    |   SEMICOLON
    |   statementExpression SEMICOLON
    |   i=Identifier
            {
            addToken(makeToken($i, TokenType.LABEL_NAME, Stacks.top(entityStack)));
            }
        COLON statement
    ;

catches
    :   catchClause (catchClause)*
    ;

catchClause
    :   CATCH o=LPAREN formalCatchParameter c=RPAREN
        block
    ;

formalCatchParameter
    :   variableModifiers type variableDeclaratorId
            {
            lastVariableDeclaratorId = null;
            }
    ;

switchBlockStatementGroups
    :   (switchBlockStatementGroup)*
    ;

switchBlockStatementGroup
    :   switchLabel+ blockStatement*
    ;

switchLabel
    :   CASE constantExpression COLON
    |   CASE enumConstantName COLON
    |   DEFAULT COLON
    ;

forControl
options {k=3;}
    :   enhancedForControl
    |   forInit? SEMICOLON expression? SEMICOLON forUpdate?
    ;

forInit
    :   localVariableDeclaration
    |   expressionList
    ;

enhancedForControl
    :   variableModifiers type i=Identifier
            {
            addToken(makeToken($i, TokenType.LOCAL_VARIABLE_NAME, Stacks.top(entityStack)));
            }
        COLON expression
    ;

forUpdate
    :   expressionList
    ;

// EXPRESSIONS

parExpression
    :   o=LPAREN expression c=RPAREN
    ;

expressionList
    :   expression (COMMA expression)*
    ;

statementExpression
    :   expression
    ;

constantExpression
    :   expression
    ;

expression
    :   conditionalExpression (assignmentOperator expression)?
    ;

assignmentOperator
    :   ASG
    |   ASGADD
    |   ASGSUB
    |   ASGMUL
    |   ASGDIV
    |   ASGBITAND
    |   ASGBITOR
    |   ASGBITXOR
    |   ASGREM
    |   (LT LT ASG)=> t1=LT t2=LT t3=ASG
        { $t1.getLine() == $t2.getLine() &&
          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() && 
          $t2.getLine() == $t3.getLine() && 
          $t2.getCharPositionInLine() + 1 == $t3.getCharPositionInLine() }?
    |   (GT GT GT ASG)=> t1=GT t2=GT t3=GT t4=ASG
        { $t1.getLine() == $t2.getLine() && 
          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() &&
          $t2.getLine() == $t3.getLine() && 
          $t2.getCharPositionInLine() + 1 == $t3.getCharPositionInLine() &&
          $t3.getLine() == $t4.getLine() && 
          $t3.getCharPositionInLine() + 1 == $t4.getCharPositionInLine() }?
    |   (GT GT ASG)=> t1=GT t2=GT t3=ASG
        { $t1.getLine() == $t2.getLine() && 
          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() && 
          $t2.getLine() == $t3.getLine() && 
          $t2.getCharPositionInLine() + 1 == $t3.getCharPositionInLine() }?
    ;

conditionalExpression
    :   conditionalOrExpression ( QUESTIONMARK expression COLON expression )?
    ;

conditionalOrExpression
    :   conditionalAndExpression ( OR conditionalAndExpression )*
    ;

conditionalAndExpression
    :   inclusiveOrExpression ( AND inclusiveOrExpression )*
    ;

inclusiveOrExpression
    :   exclusiveOrExpression ( BITOR exclusiveOrExpression )*
    ;

exclusiveOrExpression
    :   andExpression ( BITXOR andExpression )*
    ;

andExpression
    :   equalityExpression ( BITAND equalityExpression )*
    ;

equalityExpression
    :   instanceOfExpression ( (EQ | NE) instanceOfExpression )*
    ;

instanceOfExpression
    :   relationalExpression (INSTANCEOF type)?
    ;

relationalExpression
    :   shiftExpression ( relationalOp shiftExpression )*
    ;

relationalOp
    :   (LT ASG)=> t1=LT t2=ASG
        { $t1.getLine() == $t2.getLine() && 
          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() }?
    |   (GT ASG)=> t1=GT t2=ASG
        { $t1.getLine() == $t2.getLine() && 
          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() }?
    |   LT
    |   GT
    ;

shiftExpression
    :   additiveExpression ( shiftOp additiveExpression )*
    ;

shiftOp
    :   (LT LT)=> t1=LT t2=LT
        { $t1.getLine() == $t2.getLine() && 
          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() }?
    |   (GT GT GT)=> t1=GT t2=GT t3=GT
        { $t1.getLine() == $t2.getLine() && 
          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() &&
          $t2.getLine() == $t3.getLine() && 
          $t2.getCharPositionInLine() + 1 == $t3.getCharPositionInLine() }?
    |   (GT GT)=> t1=GT t2=GT
        { $t1.getLine() == $t2.getLine() && 
          $t1.getCharPositionInLine() + 1 == $t2.getCharPositionInLine() }?
    ;


additiveExpression
    :   multiplicativeExpression ( (ADD | SUB) multiplicativeExpression )*
    ;

multiplicativeExpression
    :   unaryExpression ( ( MUL | DIV | REM ) unaryExpression )*
    ;

unaryExpression
    :   ADD unaryExpression
    |   SUB unaryExpression
    |   INC unaryExpression
    |   DEC unaryExpression
    |   unaryExpressionNotPlusMinus
    ;

unaryExpressionNotPlusMinus
    :   BITNOT unaryExpression
    |   NOT unaryExpression
    |   castExpression
    |   primary selector* (INC|DEC)?
    ;

castExpression
    :   o=LPAREN primitiveType c=RPAREN unaryExpression
    |   o=LPAREN (type | expression) c=RPAREN unaryExpressionNotPlusMinus
    ;

primary
    :   parExpression
    |   THIS (DOT i=Identifier
            {
            JavaToken token = makeToken($i, TokenType.QUALIFIED_NAME_REF, Stacks.top(entityStack));
            addToken(token);
            }
        )* identifierSuffix?
    |   SUPER superSuffix
    |   literal
    |   NEW creator
    |   i=Identifier
            {
            addToken(makeToken($i, TokenType.PRIMARY_NAME_REF, Stacks.top(entityStack)));
            }
        (DOT j=Identifier
            {
            JavaToken token = makeToken($j, TokenType.QUALIFIED_NAME_REF, Stacks.top(entityStack));
            addToken(token);
            }
        )* identifierSuffix?
    |   primitiveType (LBRACKET RBRACKET)* DOT CLASS
    |   VOID DOT CLASS
    ;

identifierSuffix
    :   (LBRACKET RBRACKET)+ DOT CLASS
    |   (LBRACKET expression RBRACKET)+
    |   arguments
    |   DOT CLASS
    |   DOT explicitGenericInvocation
    |   DOT THIS
    |   DOT SUPER arguments
    |   DOT NEW innerCreator
    ;

creator
    :   nonWildcardTypeArguments createdName classCreatorRest
    |   createdName (arrayCreatorRest | classCreatorRest)
    ;

createdName
    :
            {
            isCollectingCreatedNameParts = true;
            }
        classOrInterfaceType
            {
            isCollectingCreatedNameParts = false;
            for (JavaToken token : createdNameParts) {
                token.setType(TokenType.CONSTRUCTOR_CALL);
            }
            createdNameParts.clear();
            }
    |   primitiveType
    ;

innerCreator
    :   nonWildcardTypeArguments? i=Identifier
            {
            addToken(makeToken($i, TokenType.CONSTRUCTOR_CALL, Stacks.top(entityStack)));
            }
        classCreatorRest
    ;

arrayCreatorRest
    :   LBRACKET
        (   RBRACKET (LBRACKET RBRACKET)* arrayInitializer
        |   expression RBRACKET (LBRACKET expression RBRACKET)* (LBRACKET RBRACKET)*
        )
    ;

classCreatorRest
    :   arguments (
            {
            StringBuilder name = new StringBuilder();
            name.append("$");
            name.append(Stacks.top(anonStack));
            Stacks.push(qualifiedNameStack, name.toString());
            Stacks.push(entityStack, Entities.newJavaEntity(EntityType.CLASS, Stacks.top(entityStack), createQualifiedName()));
            Integer anon = Stacks.pop(anonStack);
            Stacks.push(anonStack, anon + 1);
            }
        pos=classBody
            {
            Stacks.top(entityStack).setPosition(pos);
            addEntity(Stacks.pop(entityStack));
            Stacks.pop(qualifiedNameStack);
            }
        )?
    ;

explicitGenericInvocation
    :   nonWildcardTypeArguments i=Identifier
            {
            addToken(makeToken($i, TokenType.METHOD_CALL, Stacks.top(entityStack)));
            }
        arguments
    ;

nonWildcardTypeArguments
    :   LT typeList GT
    ;

selector
@init {
    JavaToken token = null;
}
    :   DOT i=Identifier
            {
            token = makeToken($i, TokenType.QUALIFIED_NAME_REF, Stacks.top(entityStack));
            addToken(token);
            }
        (
            {
            token.setType(TokenType.METHOD_CALL);
            }
        arguments)?
    |   DOT THIS
    |   DOT SUPER superSuffix
    |   DOT NEW innerCreator
    |   LBRACKET expression RBRACKET
    ;

superSuffix
@init {
    JavaToken token = null;
}
    :   arguments
    |   DOT i=Identifier
            {
            token = makeToken($i, TokenType.QUALIFIED_NAME_REF, Stacks.top(entityStack));
            addToken(token);
            }
        (
            {
            token.setType(TokenType.METHOD_CALL);
            }
        arguments)?
    ;

arguments
    :
            {
            TokenType lastType = lastTokenAdded.getType();
            if (lastType.isChildOf(TokenType.NAME_USE) &&
                    ((lastType == TokenType.QUALIFIED_NAME_REF) ||
                    (lastType != TokenType.CONSTRUCTOR_CALL))) {
                lastTokenAdded.setType(TokenType.METHOD_CALL);
            }
            }
        o=LPAREN expressionList? c=RPAREN
    ;
