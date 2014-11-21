package edu.ua.cs.taser.javatext.rewrite;

import edu.ua.cs.taser.javatext.JavaDocument;
import edu.ua.cs.taser.token.JavaToken;
import edu.ua.cs.taser.token.TokenStream;
import edu.ua.cs.taser.util.position.Position;
import static edu.ua.cs.taser.util.position.Positions.rangePosition;

import rx.util.functions.Func1;

public class FoldComments implements Func1<JavaDocument, JavaDocument> {

    public JavaDocument call(JavaDocument jdoc) {
        jdoc.addRewrite("FoldComments");
        final TokenStream<JavaToken> stream = new TokenStream<JavaToken>(jdoc.getTokens());
        while (stream.hasNext()) {
            final JavaToken t1 = stream.getNext();
            if ((!t1.getType().isComment()) || (!stream.hasNext())) continue;
            final JavaToken t2 = stream.getNext();
            if ((t1.getEntity() != t2.getEntity()) && // haveDifferentJavaEntity
                    ((t2.getEntity().getPosition().getStart().getLine() - t1.getPosition().getEnd().getLine()) == 1) && // areConsecutive
                    (t1.getPosition().getStart().getColumn() == t2.getEntity().getPosition().getStart().getColumn())) { // areLeftAligned
                // Fold t1 into t2's entity
                t1.setEntity(t2.getEntity());
                // Update span of t2.entity (/t1.entity) to include t1
                t1.getEntity().setPosition(rangePosition(t1.getPosition().getStart(), t1.getEntity().getPosition().getEnd()));
            }
            stream.getPrevious();
        }
        return jdoc;
    }
}
