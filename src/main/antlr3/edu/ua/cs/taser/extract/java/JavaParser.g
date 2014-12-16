/*
 [The "BSD licence"]
 Copyright (c) 2007-2008 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
/** A Java 1.5 grammar for ANTLR v3 derived from the spec
 *
 *  This is a very close representation of the spec; the changes
 *  are comestic (remove left recursion) and also fixes (the spec
 *  isn't exactly perfect).  I have run this on the 1.4.2 source
 *  and some nasty looking enums from 1.5, but have not really
 *  tested for 1.5 compatibility.
 *
 *  I built this with: java -Xmx100M org.antlr.Tool java.g 
 *  and got two errors that are ok (for now):
 *  java.g:691:9: Decision can match input such as
 *    "'0'..'9'{'E', 'e'}{'+', '-'}'0'..'9'{'D', 'F', 'd', 'f'}"
 *    using multiple alternatives: 3, 4
 *  As a result, alternative(s) 4 were disabled for that input
 *  java.g:734:35: Decision can match input such as "{'$', 'A'..'Z',
 *    '_', 'a'..'z', '\u00C0'..'\u00D6', '\u00D8'..'\u00F6',
 *    '\u00F8'..'\u1FFF', '\u3040'..'\u318F', '\u3300'..'\u337F',
 *    '\u3400'..'\u3D2D', '\u4E00'..'\u9FFF', '\uF900'..'\uFAFF'}"
 *    using multiple alternatives: 1, 2
 *  As a result, alternative(s) 2 were disabled for that input
 *
 *  You can turn enum on/off as a keyword :)
 *
 *  Version 1.0 -- initial release July 5, 2006 (requires 3.0b2 or higher)
 *
 *  Primary author: Terence Parr, July 2006
 *
 *  Version 1.0.1 -- corrections by Koen Vanderkimpen & Marko van Dooren,
 *      October 25, 2006;
 *      fixed normalInterfaceDeclaration: now uses typeParameters instead
 *          of typeParameter (according to JLS, 3rd edition)
 *      fixed castExpression: no longer allows expression next to type
 *          (according to semantics in JLS, in contrast with syntax in JLS)
 *
 *  Version 1.0.2 -- Terence Parr, Nov 27, 2006
 *      java spec I built this from had some bizarre for-loop control.
 *          Looked weird and so I looked elsewhere...Yep, it's messed up.
 *          simplified.
 *
 *  Version 1.0.3 -- Chris Hogue, Feb 26, 2007
 *      Factored out an annotationName rule and used it in the annotation rule.
 *          Not sure why, but typeName wasn't recognizing references to inner
 *          annotations (e.g. @InterfaceName.InnerAnnotation())
 *      Factored out the elementValue section of an annotation reference.  Created 
 *          elementValuePair and elementValuePairs rules, then used them in the 
 *          annotation rule.  Allows it to recognize annotation references with 
 *          multiple, comma separated attributes.
 *      Updated elementValueArrayInitializer so that it allows multiple elements.
 *          (It was only allowing 0 or 1 element).
 *      Updated localVariableDeclaration to allow annotations.  Interestingly the JLS
 *          doesn't appear to indicate this is legal, but it does work as of at least
 *          JDK 1.5.0_06.
 *      Moved the Identifier portion of annotationTypeElementRest to annotationMethodRest.
 *          Because annotationConstantRest already references variableDeclarator which 
 *          has the Identifier portion in it, the parser would fail on constants in 
 *          annotation definitions because it expected two identifiers.  
 *      Added optional trailing ';' to the alternatives in annotationTypeElementRest.
 *          Wouldn't handle an inner interface that has a trailing ';'.
 *      Swapped the expression and type rule reference order in castExpression to 
 *          make it check for genericized casts first.  It was failing to recognize a
 *          statement like  "Class<Byte> TYPE = (Class<Byte>)...;" because it was seeing
 *          'Class<Byte' in the cast expression as a less than expression, then failing 
 *          on the '>'.
 *      Changed createdName to use typeArguments instead of nonWildcardTypeArguments.
 *          Again, JLS doesn't seem to allow this, but java.lang.Class has an example of
 *          of this construct.
 *      Changed the 'this' alternative in primary to allow 'identifierSuffix' rather than
 *          just 'arguments'.  The case it couldn't handle was a call to an explicit
 *          generic method invocation (e.g. this.<E>doSomething()).  Using identifierSuffix
 *          may be overly aggressive--perhaps should create a more constrained thisSuffix rule?
 *      
 *  Version 1.0.4 -- Hiroaki Nakamura, May 3, 2007
 *
 *  Fixed formalParameterDecls, localVariableDeclaration, forInit,
 *  and forVarControl to use variableModifier* not 'final'? (annotation)?
 *
 *  Version 1.0.5 -- Terence, June 21, 2007
 *  --a[i].foo didn't work. Fixed unaryExpression
 *
 *  Version 1.0.6 -- John Ridgway, March 17, 2008
 *      Made "assert" a switchable keyword like "enum".
 *      Fixed compilationUnit to disallow "annotation importDeclaration ...".
 *      Changed "Identifier ('.' Identifier)*" to "qualifiedName" in more 
 *          places.
 *      Changed modifier* and/or variableModifier* to classOrInterfaceModifiers,
 *          modifiers or variableModifiers, as appropriate.
 *      Renamed "bound" to "typeBound" to better match language in the JLS.
 *      Added "memberDeclaration" which rewrites to methodDeclaration or 
 *      fieldDeclaration and pulled type into memberDeclaration.  So we parse 
 *          type and then move on to decide whether we're dealing with a field
 *          or a method.
 *      Modified "constructorDeclaration" to use "constructorBody" instead of
 *          "methodBody".  constructorBody starts with explicitConstructorInvocation,
 *          then goes on to blockStatement*.  Pulling explicitConstructorInvocation
 *          out of expressions allowed me to simplify "primary".
 *      Changed variableDeclarator to simplify it.
 *      Changed type to use classOrInterfaceType, thus simplifying it; of course
 *          I then had to add classOrInterfaceType, but it is used in several 
 *          places.
 *      Fixed annotations, old version allowed "@X(y,z)", which is illegal.
 *      Added optional comma to end of "elementValueArrayInitializer"; as per JLS.
 *      Changed annotationTypeElementRest to use normalClassDeclaration and 
 *          normalInterfaceDeclaration rather than classDeclaration and 
 *          interfaceDeclaration, thus getting rid of a couple of grammar ambiguities.
 *      Split localVariableDeclaration into localVariableDeclarationStatement
 *          (includes the terminating semi-colon) and localVariableDeclaration.  
 *          This allowed me to use localVariableDeclaration in "forInit" clauses,
 *           simplifying them.
 *      Changed switchBlockStatementGroup to use multiple labels.  This adds an
 *          ambiguity, but if one uses appropriately greedy parsing it yields the
 *           parse that is closest to the meaning of the switch statement.
 *      Renamed "forVarControl" to "enhancedForControl" -- JLS language.
 *      Added semantic predicates to test for shift operations rather than other
 *          things.  Thus, for instance, the string "< <" will never be treated
 *          as a left-shift operator.
 *      In "creator" we rule out "nonWildcardTypeArguments" on arrayCreation, 
 *          which are illegal.
 *      Moved "nonWildcardTypeArguments into innerCreator.
 *      Removed 'super' superSuffix from explicitGenericInvocation, since that
 *          is only used in explicitConstructorInvocation at the beginning of a
 *           constructorBody.  (This is part of the simplification of expressions
 *           mentioned earlier.)
 *      Simplified primary (got rid of those things that are only used in
 *          explicitConstructorInvocation).
 *      Lexer -- removed "Exponent?" from FloatingPointLiteral choice 4, since it
 *          led to an ambiguity.
 *
 *      This grammar successfully parses every .java file in the JDK 1.5 source 
 *          tree (excluding those whose file names include '-', which are not
 *          valid Java compilation units).
 *
 *  Known remaining problems:
 *      "Letter" and "JavaIDDigit" are wrong.  The actual specification of
 *      "Letter" should be "a character for which the method
 *      Character.isJavaIdentifierStart(int) returns true."  A "Java 
 *      letter-or-digit is a character for which the method 
 *      Character.isJavaIdentifierPart(int) returns true."
 */
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
