lexer grammar JavaLexer;

options {
    language = Java;
    superClass = BaseJavaLexer;
}

@header {
    package edu.ua.cs.taser.javatext;
}

ABSTRACT : 'abstract' ;
ASSERT : 'assert' { if (!assertIsKeyword()) $type = Identifier; } ;
BOOLEAN : 'boolean' ;
BREAK : 'break' ;
BYTE : 'byte' ;
CASE : 'case' ;
CATCH : 'catch' ;
CHAR : 'char' ;
CLASS : 'class' ;
CONTINUE : 'continue' ;
DEFAULT : 'default' ;
DO : 'do' ;
DOUBLE : 'double' ;
ELSE : 'else' ;
ENUM : 'enum' { if (!enumIsKeyword()) $type = Identifier; } ;
EXTENDS : 'extends' ;
FINAL : 'final' ;
FINALLY : 'finally' ;
FLOAT : 'float' ;
FOR : 'for' ;
IF : 'if' ;
IMPLEMENTS : 'implements' ;
IMPORT : 'import' ;
INSTANCEOF : 'instanceof' ;
INT : 'int' ;
INTERFACE : 'interface' ;
LONG : 'long' ;
NATIVE : 'native' ;
NEW : 'new' ;
PACKAGE : 'package' ;
PRIVATE : 'private' ;
PROTECTED : 'protected' ;
PUBLIC : 'public' ;
RETURN : 'return' ;
SHORT : 'short' ;
STATIC : 'static' ;
STRICTFP : 'strictfp' ;
SUPER : 'super' ;
SWITCH : 'switch' ;
SYNCHRONIZED : 'synchronized' ;
THIS : 'this' ;
THROW : 'throw' ;
THROWS : 'throws' ;
TRANSIENT : 'transient' ;
TRY : 'try' ;
VOID : 'void' ;
VOLATILE : 'volatile' ;
WHILE : 'while' ;

FALSE : 'false' ;
NULL : 'null' ;
TRUE : 'true' ;

LPAREN : '(' ;
RPAREN : ')' ;
LBRACE : '{' ;
RBRACE : '}' ;
LBRACKET : '[' ;
RBRACKET : ']' ;
SEMICOLON : ';' ;
COMMA : ',' ;
DOT : '.' ;

ASG : '=' ;
LT : '<' ;
GT : '>' ;
NOT : '!' ;
BITNOT : '~' ;
QUESTIONMARK : '?' ;
COLON : ':' ;

EQ : '==' ;
NE : '!=' ;
AND : '&&' ;
OR : '||' ;
INC : '++' ;
DEC : '--' ;

ADD : '+' ;
SUB : '-' ;
MUL : '*' ;
DIV : '/' ;
BITAND : '&' ;
BITOR : '|' ;
BITXOR : '^' ;
REM : '%' ;

ASGADD : '+=' ;
ASGSUB : '-=' ;
ASGMUL : '*=' ;
ASGDIV : '/=' ;
ASGBITAND : '&=' ;
ASGBITOR : '|=' ;
ASGBITXOR : '^=' ;
ASGREM : '%=' ;

ELLIPSIS : '...' ;
AT : '@' ;

fragment HexDigit
    :   ('0'..'9'|'a'..'f'|'A'..'F')
    ;

fragment IntegerTypeSuffix
    :   ('l'|'L')
    ;

HexLiteral
    :   '0' ('x'|'X') HexDigit+ IntegerTypeSuffix?
    ;

DecimalLiteral
    :   ('0' | '1'..'9' '0'..'9'*) IntegerTypeSuffix?
    ;

OctalLiteral
    :   '0' ('0'..'7')+ IntegerTypeSuffix?
    ;

fragment Exponent
    :   ('e'|'E') ('+'|'-')? ('0'..'9')+
    ;

fragment FloatTypeSuffix
    :   ('f'|'F'|'d'|'D')
    ;

FloatingPointLiteral
    :   ('0'..'9')+ '.' ('0'..'9')* Exponent? FloatTypeSuffix?
    |   '.' ('0'..'9')+ Exponent? FloatTypeSuffix?
    |   ('0'..'9')+ Exponent FloatTypeSuffix?
    |   ('0'..'9')+ FloatTypeSuffix
    ;

fragment OctalEscape
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

fragment UnicodeEscape
    :   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;

fragment EscapeSequence
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |   UnicodeEscape
    |   OctalEscape
    ;

CharacterLiteral
    :   '\'' ( EscapeSequence | ~('\''|'\\') ) '\''
    ;

StringLiteral
    :   '"' ( EscapeSequence | ~('\\'|'"') )* '"'
    ;

fragment Letter
    :   '\u0024' |
        '\u0041'..'\u005a' |
        '\u005f' |
        '\u0061'..'\u007a' |
        '\u00c0'..'\u00d6' |
        '\u00d8'..'\u00f6' |
        '\u00f8'..'\u00ff' |
        '\u0100'..'\u1fff' |
        '\u3040'..'\u318f' |
        '\u3300'..'\u337f' |
        '\u3400'..'\u3d2d' |
        '\u4e00'..'\u9fff' |
        '\uf900'..'\ufaff'
    ;

fragment JavaIDDigit
    :   '\u0030'..'\u0039' |
        '\u0660'..'\u0669' |
        '\u06f0'..'\u06f9' |
        '\u0966'..'\u096f' |
        '\u09e6'..'\u09ef' |
        '\u0a66'..'\u0a6f' |
        '\u0ae6'..'\u0aef' |
        '\u0b66'..'\u0b6f' |
        '\u0be7'..'\u0bef' |
        '\u0c66'..'\u0c6f' |
        '\u0ce6'..'\u0cef' |
        '\u0d66'..'\u0d6f' |
        '\u0e50'..'\u0e59' |
        '\u0ed0'..'\u0ed9' |
        '\u1040'..'\u1049'
    ;

Identifier
    :   Letter (Letter|JavaIDDigit)*
    ;

WS : (' '|'\r'|'\t'|'\u000C'|'\n') { $channel = HIDDEN; } ;

BLOCK_COMMENT : '/*' ( options {greedy=false;} : . )* '*/' { $channel = COMMENT_CHANNEL; } ;

LINE_COMMENT : '//' ~('\n'|'\r')* '\r'? ('\n'|EOF) { $channel = COMMENT_CHANNEL; } ;
