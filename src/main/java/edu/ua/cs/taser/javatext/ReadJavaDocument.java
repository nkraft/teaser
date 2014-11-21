package edu.ua.cs.taser.javatext;

import rx.util.functions.Func1;

import java.nio.file.Path;

public class ReadJavaDocument implements Func1<Path, JavaDocument> {

    public JavaDocument call(final Path path) {
        return new JavaDocumentReader(path).read();
    }
}
