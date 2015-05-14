package edu.ua.cs.teaser;

import edu.ua.cs.teaser.cli.BuildCorpus;
import edu.ua.cs.teaser.cli.Extract;
import edu.ua.cs.teaser.cli.FormatCorpus;
import edu.ua.cs.teaser.cli.PreprocessCorpus;
import edu.ua.cs.teaser.cli.Rewrite;
import edu.ua.cs.teaser.cli.args.RawArgs;
import edu.ua.cs.teaser.cli.args.RealizedArgs;
import edu.ua.cs.teaser.io.Files;
import edu.ua.cs.teaser.javatext.JavaLexerType;
import edu.ua.cs.teaser.syntext.BuildMethodDocuments;
import edu.ua.cs.teaser.syntext.BuildClassTypeDocuments;
import edu.ua.cs.teaser.javatext.JavaDocument;
import edu.ua.cs.teaser.syntext.SyntaxAnnotatedDocument;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import rx.util.functions.Func1;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class App {

    public static final String EXTRACT_JDOC      = "ex";
    public static final String REWRITE_JDOC      = "rw";
    public static final String BUILD_CORPUS      = "bc";
    public static final String PREPROCESS_CORPUS = "pp";
    public static final String FORMAT_CORPUS     = "fc";

    public static final String[] MODES = new String[] {
        EXTRACT_JDOC, REWRITE_JDOC,
        BUILD_CORPUS, PREPROCESS_CORPUS, FORMAT_CORPUS
    };

    public static void main(String[] args) {
        // AppDebug.launch(args);
        System.exit(launch(args));
    }

    public static int launch(String[] args) {
        final RawArgs rawArgs = new RawArgs();
        final JCommander cli = new JCommander();
        cli.addCommand(EXTRACT_JDOC, rawArgs);
        cli.addCommand(REWRITE_JDOC, rawArgs);
        cli.addCommand(BUILD_CORPUS, rawArgs);
        cli.addCommand(PREPROCESS_CORPUS, rawArgs);
        cli.addCommand(FORMAT_CORPUS, rawArgs);
        try {
            cli.parse(args);
            final String cmd = cli.getParsedCommand();
            if (Arrays.asList(MODES).contains(cmd)) {
                switch (cmd) {
                case EXTRACT_JDOC:
                    Extract.execute(realizeArgs(rawArgs, "java"));
                    break;
                case REWRITE_JDOC:
                    Rewrite.execute(realizeArgs(rawArgs, "jdoc"));
                    break;
                case BUILD_CORPUS:
                    BuildCorpus.execute(realizeArgs(rawArgs, "jdoc"));
                    break;
                case PREPROCESS_CORPUS:
                    PreprocessCorpus.execute(realizeArgs(rawArgs, "sacorpus"));
                    break;
                case FORMAT_CORPUS:
                    FormatCorpus.execute(realizeArgs(rawArgs, "sacorpus"));
                    break;
                }
            } else {
                cli.usage();
            }
        } catch (ParameterException e) {
            System.err.println(e.getMessage());
            cli.usage();
            return 1;
        }
        return 0;
    }

    private static RealizedArgs realizeArgs(final RawArgs args, final String extension) throws ParameterException {
        final List<Path> inputFiles = realizeInputFiles(args.getInputFiles(), extension);
        final Path outputDir = realizeOutputDir(args.getOutputDir());
        final Func1<JavaDocument, List<SyntaxAnnotatedDocument>> corpusType = realizeCorpusType(args.getCorpusType());
        final String project = args.getProject();
        final JavaLexerType javaLexerType = realizeJavaLexerType(args.getJavaLanguageSpec());
        return new RealizedArgs() {
            @Override public List<Path> getInputFiles() { return inputFiles; }
            @Override public Path getOutputDir() { return outputDir; }
            @Override public String getProject() { return project; }
            @Override public JavaLexerType getJavaLexerType() { return javaLexerType; }
            @Override public Func1<JavaDocument, List<SyntaxAnnotatedDocument>> getCorpusType() { return corpusType; }
        };
    }

    private static List<Path> realizeInputFiles(final List<String> filenames, final String extension) throws ParameterException {
        final List<Path> inputFiles = Files.findFilesWithExtension(filenames, extension);
        if (inputFiles.isEmpty()) {
            throw new ParameterException("Failed to find input files with extension '." + extension + "'");
        }
        return inputFiles;
    }

    private static Path realizeOutputDir(final String outputDir) throws ParameterException {
        try {
            if (!Files.createDirectories(outputDir)) {
                throw new ParameterException("Failed to create output directory '" + outputDir + "'");
            }
        } catch (IOException e) {
            throw new ParameterException(e.getMessage());
        }
        return Paths.get(outputDir);
    }

    private static JavaLexerType realizeJavaLexerType(final String jls) throws ParameterException {
        try {
            return JavaLexerType.valueOf(jls.toUpperCase(Locale.US));
        } catch (IllegalArgumentException e) {
            throw new ParameterException("Illegal java language specification value '" + jls + "'");
        }
    }

    private static Func1<JavaDocument, List<SyntaxAnnotatedDocument>> realizeCorpusType(final String type) throws ParameterException {
        switch (type) {
            case "method":
                return new BuildMethodDocuments();
            case "classtype":
                return new BuildClassTypeDocuments();
            default:
                return new BuildMethodDocuments();
        }
    }
}
