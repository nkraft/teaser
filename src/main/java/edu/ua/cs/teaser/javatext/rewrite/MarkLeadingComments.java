package edu.ua.cs.teaser.javatext.rewrite;

import edu.ua.cs.teaser.document.EntityType;
import edu.ua.cs.teaser.document.JavaEntity;
import edu.ua.cs.teaser.javatext.JavaDocument;
import edu.ua.cs.teaser.token.JavaToken;
import edu.ua.cs.teaser.token.TokenStream;
import edu.ua.cs.teaser.token.TokenType;

import rx.util.functions.Func1;

/**
 * Must be applied AFTER MergeComments but BEFORE FoldComments
 */
public class MarkLeadingComments implements Func1<JavaDocument, JavaDocument> {

    public JavaDocument call(JavaDocument jdoc) {
        jdoc.addRewrite("MarkLeadingComments");
        final TokenStream<JavaToken> stream = new TokenStream<JavaToken>(jdoc.getTokens());
        while (stream.hasNext()) {
            final JavaToken t1 = stream.getNext();
            if ((!t1.getType().isComment()) || (!stream.hasNext())) continue;
            final JavaToken t2 = stream.getNext();
            final JavaEntity e2 = t2.getEntity();
            if ((t1.getEntity() != t2.getEntity()) && // haveDifferentJavaEntity
                    ((e2.getPosition().getStart().getLine() - t1.getPosition().getEnd().getLine()) == 1)) { // areConsecutive
                // Mark t1 as leading t2's entity
                final EntityType e2type = e2.getType();
                if (e2type.isClassType()) {
                    t1.setType(TokenType.CLASS_COMMENT);
                } else if (e2type.isMethod()) {
                    t1.setType(TokenType.METHOD_COMMENT);
                }
            }
            stream.getPrevious();
        }
        return jdoc;
    }
}
