package edu.ua.cs.taser.javatext;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.RecognizerSharedState;

/**
 * Extended by the class generated from the JavaLexer.g file.
 */
public abstract class BaseJavaLexer extends Lexer {

    public static final int COMMENT_CHANNEL = HIDDEN + 1;

    public BaseJavaLexer() {
        super();
    }

    public BaseJavaLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }

    public BaseJavaLexer(CharStream input, RecognizerSharedState state) {
        super(input, state);
    }

    protected boolean assertIsKeyword() {
        return false;
    }

    protected boolean enumIsKeyword() {
        return false;
    }
}
