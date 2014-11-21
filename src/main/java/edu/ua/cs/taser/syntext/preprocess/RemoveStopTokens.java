package edu.ua.cs.taser.syntext.preprocess;

import edu.ua.cs.taser.io.LineReader;
import edu.ua.cs.taser.syntext.SyntaxAnnotatedCorpus;
import edu.ua.cs.taser.syntext.SyntaxAnnotatedDocument;
import edu.ua.cs.taser.syntext.SyntaxAnnotatedToken;
import edu.ua.cs.taser.token.TokenStream;
import static edu.ua.cs.taser.io.Filenames.makeFilename;
import static edu.ua.cs.taser.lang.Strings.join;

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
