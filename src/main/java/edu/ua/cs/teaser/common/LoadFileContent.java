package edu.ua.cs.teaser.common;

import rx.util.functions.Func1;

import java.nio.file.Path;

public final class LoadFileContent implements Func1<Path, FileContent> {

    public FileContent call(final Path path) {
        char[] content = null;
        try {
            content = new FileContentLoader(path).call();
        } catch (Exception e) {
            content = new char[0];
        }
        return new FileContent(path, content);
    }
}
