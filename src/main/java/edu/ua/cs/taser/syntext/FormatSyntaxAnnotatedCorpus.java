package edu.ua.cs.taser.syntext;

import edu.ua.cs.taser.io.Filenames;

import rx.util.functions.Action1;

import java.nio.file.Path;

public class FormatSyntaxAnnotatedCorpus implements Action1<SyntaxAnnotatedCorpus> {

    private final Path outputDir;

    public FormatSyntaxAnnotatedCorpus(final Path outputDir) {
        this.outputDir = outputDir;
    }

    @Override
    public void call(final SyntaxAnnotatedCorpus corpus) {
        // TODO(nkraft): Avoid hard-coding Mallet file ext and writer
        final String filename = Filenames.makeFilename(outputDir.toString(), corpus.getName(), "ser");
        new MalletFormatWriter(corpus).write(Filenames.normalize(filename));
    }
}
