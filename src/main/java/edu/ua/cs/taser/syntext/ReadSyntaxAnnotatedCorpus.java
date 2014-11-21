package edu.ua.cs.taser.syntext;

import rx.util.functions.Func1;

import java.nio.file.Path;

public class ReadSyntaxAnnotatedCorpus implements Func1<Path, SyntaxAnnotatedCorpus> {

    public SyntaxAnnotatedCorpus call(final Path path) {
        return new SyntaxAnnotatedCorpusReader(path).read();
    }
}
