package edu.ua.cs.teaser.javatext;

public final class JavaLexerFactory {

    public static JavaLexer createJavaLexer(final JavaLexerType type) {
        JavaLexer lexer = null;
        switch(type) {
        case JAVA:
            lexer = new JavaLexer();
            break;
        case JAVA5:
            lexer = new Java5Lexer();
            break;
        }
        return lexer;
    }
}
