package edu.ua.cs.teaser.syntext.preprocess;

import edu.ua.cs.teaser.syntext.SyntaxAnnotatedCorpus;
import edu.ua.cs.teaser.syntext.SyntaxAnnotatedDocument;
import edu.ua.cs.teaser.syntext.SyntaxAnnotatedToken;
import edu.ua.cs.teaser.token.TokenStream;

import rx.util.functions.Func1;

import java.util.Locale;

public class LowercaseTokens implements Func1<SyntaxAnnotatedDocument, SyntaxAnnotatedDocument> {

    public LowercaseTokens(final SyntaxAnnotatedCorpus syncorp) {
        syncorp.addTransformation("LowercaseTokens");
    }

    public SyntaxAnnotatedDocument call(SyntaxAnnotatedDocument syndoc) {
        final TokenStream<SyntaxAnnotatedToken> stream = new TokenStream<SyntaxAnnotatedToken>(syndoc.getTokens());
        while (stream.hasNext()) {
            final SyntaxAnnotatedToken token = stream.getNext();
            token.setText(token.getText().toLowerCase(Locale.US));
        }
        return syndoc;
    }
}
