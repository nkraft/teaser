package edu.ua.cs.teaser.javatext.rewrite;

import edu.ua.cs.teaser.javatext.JavaDocument;
import edu.ua.cs.teaser.token.JavaToken;
import edu.ua.cs.teaser.token.TokenStream;
import edu.ua.cs.teaser.token.TokenType;
import edu.ua.cs.teaser.token.Tokens;
import static edu.ua.cs.teaser.lang.Numbers.isNumeric;

import rx.util.functions.Func1;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SplitNonidentifiers implements Func1<JavaDocument, JavaDocument> {

    private static final Pattern SPLITTER = Pattern.compile("\\W+");

    public JavaDocument call(JavaDocument jdoc) {
        jdoc.addRewrite("SplitNonidentifiers");
        final TokenStream<JavaToken> stream = new TokenStream<JavaToken>(jdoc.getTokens());
        while (stream.hasNext()) {
            final JavaToken token = stream.getNext();
            final TokenType type = token.getType();
            if (!type.isIdentifier()) {
                String text = token.getText();
                if (text.charAt(0) == '"') {
                    text = text.substring(1, text.length() - 1);
                }
                text = text.replace("'", "");
                final List<JavaToken> parts = new ArrayList<JavaToken>();
                for (final String part : SPLITTER.split(text)) {
                    if (!part.isEmpty() && !isNumeric(part)) {
                        parts.add(Tokens.newJavaToken(part, type, token.getPosition(), token.getEntity()));
                    }
                }
                if (parts.size() > 0) {
                    stream.replace(parts);
                } else {
                    stream.remove();
                }
            }
        }
        return jdoc;
    }
}
