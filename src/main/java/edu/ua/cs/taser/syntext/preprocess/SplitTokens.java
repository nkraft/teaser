package edu.ua.cs.taser.syntext.preprocess;

import edu.ua.cs.taser.syntext.SyntaxAnnotatedCorpus;
import edu.ua.cs.taser.syntext.SyntaxAnnotatedDocument;
import edu.ua.cs.taser.syntext.SyntaxAnnotatedToken;
import edu.ua.cs.taser.token.TokenStream;
import static edu.ua.cs.taser.lang.Numbers.isNumeric;

import rx.util.functions.Func1;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SplitTokens implements Func1<SyntaxAnnotatedDocument, SyntaxAnnotatedDocument> {

    private static final Pattern SPLITTER = Pattern.compile("([A-Z]+(?=$|[0-9_]|[A-Z][a-z])|[A-Za-z][a-z]*)");

    private final boolean retainOriginal;

    public SplitTokens(final SyntaxAnnotatedCorpus syncorp, boolean retainOriginal) {
        syncorp.addTransformation("SplitTokens(retainOriginal=" + retainOriginal + ")");
        this.retainOriginal = retainOriginal;
    }

    public SyntaxAnnotatedDocument call(SyntaxAnnotatedDocument syndoc) {
        final TokenStream<SyntaxAnnotatedToken> stream = new TokenStream<SyntaxAnnotatedToken>(syndoc.getTokens());
        while (stream.hasNext()) {
            final SyntaxAnnotatedToken token = stream.getNext();
            final List<SyntaxAnnotatedToken> parts = new LinkedList<SyntaxAnnotatedToken>();
            final Matcher matcher = SPLITTER.matcher(token.getText());
            while (matcher.find()) {
                final String part = matcher.group(1);
                if (!part.isEmpty() && !isNumeric(part)) {
                    parts.add(new SyntaxAnnotatedToken(token.getType(), part));
                }
            }
            if (parts.size() > 1 && retainOriginal) {
                parts.add(0, token);
            }
            if (parts.size() > 0) {
                stream.replace(parts);
            } else {
                stream.remove();
            }
        }
        return syndoc;
    }
}
