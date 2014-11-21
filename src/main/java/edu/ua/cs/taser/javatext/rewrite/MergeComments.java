package edu.ua.cs.taser.javatext.rewrite;

import edu.ua.cs.taser.javatext.JavaDocument;
import edu.ua.cs.taser.token.JavaToken;
import edu.ua.cs.taser.token.TokenStream;
import edu.ua.cs.taser.token.TokenType;
import edu.ua.cs.taser.token.Tokens;
import static edu.ua.cs.taser.util.position.Positions.rangePosition;

import rx.util.functions.Func1;

public class MergeComments implements Func1<JavaDocument, JavaDocument> {

    public JavaDocument call(JavaDocument jdoc) {
        jdoc.addRewrite("MergeComments");
        final TokenStream<JavaToken> stream = new TokenStream<JavaToken>(jdoc.getTokens());
        while (stream.hasNext()) {
            final JavaToken t1 = stream.getNext();
            if ((!t1.getType().isComment()) || (!stream.hasNext())) continue;
            boolean mergingFailed = false;
            while (stream.hasNext() && !mergingFailed) {
                mergingFailed = true;
                final JavaToken t2 = stream.getNext();
                if (t2.getType().isComment() &&
                        (t1.getEntity() == t2.getEntity()) && // haveSameJavaEntity
                        ((t1.getType().isChildOf(TokenType.LINE_COMMENT)) && (t2.getType() == TokenType.LINE_COMMENT)) && // areMergeable
                        ((t2.getPosition().getStart().getLine() - t1.getPosition().getEnd().getLine()) == 1) && // areConsecutive
                        (t1.getPosition().getStart().getColumn() == t2.getPosition().getStart().getColumn())) { // areLeftAligned
                    mergingFailed = false;
                    // Merge t1 and t2 as nt
                    final JavaToken nt = Tokens.newJavaToken(
                            t1.getText() + "\n" + t2.getText(),
                            TokenType.MERGED_COMMENT,
                            rangePosition(t1.getPosition().getStart(), t2.getPosition().getEnd()),
                            t1.getEntity());
                    // Remove t2
                    stream.remove();
                    stream.getPrevious();
                    // Replace t1 with nt
                    stream.replace(nt);
                }
                else {
                    stream.getPrevious();
                }
            }
        }
        return jdoc;
    }
}
