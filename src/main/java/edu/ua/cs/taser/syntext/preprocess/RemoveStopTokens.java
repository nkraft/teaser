package edu.ua.cs.teaser.syntext.preprocess;

import edu.ua.cs.teaser.io.LineReader;
import edu.ua.cs.teaser.syntext.SyntaxAnnotatedCorpus;
import edu.ua.cs.teaser.syntext.SyntaxAnnotatedDocument;
import edu.ua.cs.teaser.syntext.SyntaxAnnotatedToken;
import edu.ua.cs.teaser.token.TokenStream;
import static edu.ua.cs.teaser.io.Filenames.makeFilename;
import static edu.ua.cs.teaser.lang.Strings.join;

import rx.util.functions.Func1;

import java.util.HashSet;
import java.util.Set;

public class RemoveStopTokens implements Func1<SyntaxAnnotatedDocument, SyntaxAnnotatedDocument> {

    private final Set<String> stopTokens = new HashSet<String>();

    public RemoveStopTokens(final SyntaxAnnotatedCorpus syncorp, final String... stopLists) {
        syncorp.addTransformation("RemoveStopTokens(stopLists={" + join(stopLists, ',') + "})");
        for (String stopList : stopLists) {
            loadStopList(stopList);
        }
    }

    public SyntaxAnnotatedDocument call(SyntaxAnnotatedDocument syndoc) {
        final TokenStream<SyntaxAnnotatedToken> stream = new TokenStream<SyntaxAnnotatedToken>(syndoc.getTokens());
        while (stream.hasNext()) {
            final SyntaxAnnotatedToken token = stream.getNext();
            if (stopTokens.contains(token.getText())) {
                stream.remove();
            }
        }
        return syndoc;
    }

    private void loadStopList(final String stopList) {
        final String res = makeFilename("stoplists", stopList, "txt");
        for (String line : LineReader.forResource(res)) {
            if (!line.startsWith("#")) {
                stopTokens.add(line);
            }
        }
    }
}
