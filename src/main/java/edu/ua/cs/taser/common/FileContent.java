package edu.ua.cs.taser.common;

import java.nio.file.Path;

public class FileContent {

    private Path path;
    private char[] content;

    public FileContent(Path path, char[] content) {
        this.path = path;
        this.content = content;
    }

    public Path getPath() {
        return path;
    }

    public char[] getCharacters() {
        return content;
    }

    public String toString() {
        return "|" + path.toString() + "| = " + content.length;
    }
}
