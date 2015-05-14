package edu.ua.cs.teaser.javatext;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.RecognizerSharedState;

/**
 * Extends the class generated from the JavaLexer.g file.
 */
public class Java5Lexer extends JavaLexer {

    public Java5Lexer() {
        super();
    }

    public Java5Lexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }

    public Java5Lexer(CharStream input, RecognizerSharedState state) {
        super(input, state);
    }

    protected boolean assertIsKeyword() {
        return true;
    }

    protected boolean enumIsKeyword() {
        return true;
    }

    // @Override
    // public String getErrorMessage(org.antlr.runtime.RecognitionException e, String[] tokenNames) {
    //     String msg = super.getErrorMessage(e, tokenNames);
    //     return "filename '" + fn + "': " + msg;
    // }
}
