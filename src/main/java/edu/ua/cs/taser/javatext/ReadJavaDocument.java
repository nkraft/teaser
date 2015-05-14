package edu.ua.cs.teaser.javatext;

import rx.util.functions.Func1;

import java.nio.file.Path;

public class ReadJavaDocument implements Func1<Path, JavaDocument> {

    public JavaDocument call(final Path path) {
        return new JavaDocumentReader(path).read();
    }
}
