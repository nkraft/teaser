package edu.ua.cs.teaser.syntext;

import edu.ua.cs.teaser.io.Filenames;

import rx.util.functions.Action1;

import java.nio.file.Path;

public class WriteSyntaxAnnotatedCorpus implements Action1<SyntaxAnnotatedCorpus> {

    private final Path outputDir;

    public WriteSyntaxAnnotatedCorpus(final Path outputDir) {
        this.outputDir = outputDir;
    }

    @Override
    public void call(final SyntaxAnnotatedCorpus corpus) {
        final String filename = Filenames.makeFilename(outputDir.toString(), corpus.getName(), "sacorpus");
        new SyntaxAnnotatedCorpusWriter(corpus).write(Filenames.normalize(filename));
    }
}
