package edu.ua.cs.taser.syntext.preprocess;

import edu.ua.cs.taser.common.PorterStemmer;
import edu.ua.cs.taser.common.Stemmer;
import edu.ua.cs.taser.syntext.SyntaxAnnotatedCorpus;
import edu.ua.cs.taser.syntext.SyntaxAnnotatedDocument;
import edu.ua.cs.taser.syntext.SyntaxAnnotatedToken;
import edu.ua.cs.taser.token.TokenStream;

import rx.util.functions.Func1;

public class StemTokens implements Func1<SyntaxAnnotatedDocument, SyntaxAnnotatedDocument> {

    private final Stemmer stemmer = new PorterStemmer();

    public StemTokens(final SyntaxAnnotatedCorpus syncorp) {
        syncorp.addTransformation("StemTokens");
    }

    public SyntaxAnnotatedDocument call(SyntaxAnnotatedDocument syndoc) {
        final TokenStream<SyntaxAnnotatedToken> stream = new TokenStream<SyntaxAnnotatedToken>(syndoc.getTokens());
        while (stream.hasNext()) {
            final SyntaxAnnotatedToken token = stream.getNext();
            token.setText(stemmer.stem(token.getText()));
        }
        return syndoc;
    }
}
