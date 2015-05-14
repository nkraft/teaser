package edu.ua.cs.teaser.cli.args;

import static edu.ua.cs.teaser.io.Filenames.makePath;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import java.util.LinkedList;
import java.util.List;

public final class RawArgs {

    @Parameter(description = "Input directories/files")
    private List<String> inputFiles = new LinkedList<String>();

    @Parameter(names = {"-o", "--output-dir"}, description = "Output directory")
    private String outputDir = "teaser-output";

    @Parameter(names = {"-t", "--corpus-type"}, description = "Corpus type")
    private String corpusType = "method";

    @Parameter(names = {"-p", "--project"}, description = "Project name")
    private String project = "unknown-0.0";

    @Parameter(names = {"-j", "--java-lang-spec"}, description = "Java language specification")
    private String jls = "Java5";

    public final List<String> getInputFiles() {
        return inputFiles;
    }

    public final String getOutputDir() {
        return outputDir;
    }

    public final String getCorpusType() {
        return corpusType;
    }

    public final String getProject() {
        return project;
    }

    public final String getJavaLanguageSpec() {
        return jls;
    }
}
