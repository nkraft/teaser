package edu.ua.cs.taser.javatext;

import edu.ua.cs.taser.io.Filenames;

import rx.util.functions.Action1;

import java.nio.file.Path;

public final class WriteJavaDocument implements Action1<JavaDocument> {

    private final Path outputDir;

    public WriteJavaDocument(final Path outputDir) {
        this.outputDir = outputDir;
    }

    @Override
    public void call(final JavaDocument jdoc) {
        final String filename = Filenames.makeFilename(outputDir.toString(), jdoc.getCompilationUnit(), "jdoc");
        new JavaDocumentWriter(jdoc).write(Filenames.normalize(filename));
    }
}
