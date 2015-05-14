package edu.ua.cs.teaser.util.function;

import edu.ua.cs.teaser.common.FileContent;
import edu.ua.cs.teaser.javatext.JavaDocument;

import rx.util.functions.Func1;

public final class Predicates {

    public static final Func1<FileContent, Boolean> fileContentNotEmpty() {
        return FileContentPredicate.NOT_EMPTY;
    }

    private enum FileContentPredicate implements Func1<FileContent, Boolean> {
        NOT_EMPTY {
            @Override
            public Boolean call(final FileContent c) {
                return c.getCharacters().length > 0;
            }
        };
    }

    public static final Func1<JavaDocument, Boolean> javaDocumentHasTokens() {
        return JavaDocumentPredicate.HAS_TOKENS;
    }

    private enum JavaDocumentPredicate implements Func1<JavaDocument, Boolean> {
        HAS_TOKENS {
            @Override
            public Boolean call(final JavaDocument jdoc) {
                return !jdoc.getTokens().isEmpty();
            }
        };
    }

    private Predicates() {}
}
