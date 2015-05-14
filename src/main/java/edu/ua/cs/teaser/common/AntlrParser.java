package edu.ua.cs.teaser.common;

import edu.ua.cs.teaser.util.position.Position;
import edu.ua.cs.teaser.util.position.Positions;

import org.antlr.runtime.BitSet;
import org.antlr.runtime.IntStream;
import org.antlr.runtime.MismatchedTokenException;
import org.antlr.runtime.Parser;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;

public abstract class AntlrParser extends Parser {

    protected AntlrParser(TokenStream input) {
        super(input);
    }

    protected AntlrParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }

    @Override
    protected Object recoverFromMismatchedToken(IntStream input, int ttype, BitSet follow) throws RecognitionException {
        throw new MismatchedTokenException(ttype, input);
    }

    protected static Position positionOf(Token token) {
        return Positions.position(token.getLine(), token.getCharPositionInLine() + 1);
    }

    protected static Position rangePositionOf(Token start, Token end) {
        return Positions.rangePosition(positionOf(start), positionOf(end));
    }
}
