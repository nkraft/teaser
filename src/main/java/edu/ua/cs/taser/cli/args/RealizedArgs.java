package edu.ua.cs.taser.cli.args;

import edu.ua.cs.taser.javatext.JavaLexerType;
import edu.ua.cs.taser.javatext.JavaDocument;
import edu.ua.cs.taser.syntext.SyntaxAnnotatedDocument;

import java.nio.file.Path;
import java.util.List;
import rx.util.functions.Func1;

public interface RealizedArgs {
    public List<Path> getInputFiles();
    public Path getOutputDir();
    public String getProject();
    public JavaLexerType getJavaLexerType();
    public Func1<JavaDocument, List<SyntaxAnnotatedDocument>> getCorpusType();
}
