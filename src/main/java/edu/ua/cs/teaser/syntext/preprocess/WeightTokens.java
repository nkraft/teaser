package edu.ua.cs.teaser.syntext.preprocess;

import edu.ua.cs.teaser.syntext.SyntaxAnnotatedCorpus;
import edu.ua.cs.teaser.syntext.SyntaxAnnotatedDocument;
import edu.ua.cs.teaser.syntext.SyntaxAnnotatedToken;
import edu.ua.cs.teaser.token.TokenStream;
import edu.ua.cs.teaser.token.TokenType;

import rx.util.functions.Func1;

public class WeightTokens implements Func1<SyntaxAnnotatedDocument, SyntaxAnnotatedDocument> {

    public WeightTokens(final SyntaxAnnotatedCorpus syncorp) {
        syncorp.addTransformation("WeightTokens");
    }

    public SyntaxAnnotatedDocument call(SyntaxAnnotatedDocument syndoc) {
        final TokenStream<SyntaxAnnotatedToken> stream = new TokenStream<SyntaxAnnotatedToken>(syndoc.getTokens());
        while (stream.hasNext()) {
            final SyntaxAnnotatedToken token = stream.getNext();
            final TokenType ttype = token.getType();
            Number weight = token.getWeight();
            if (ttype.isA(TokenType.METHOD_NAME)) {
                weight = 1;
            } else if (ttype.isA(TokenType.PARAMETER_NAME)) {
                weight = 1.0;
            }
            token.setWeight(weight);
        }
        return syndoc;
    }
}
