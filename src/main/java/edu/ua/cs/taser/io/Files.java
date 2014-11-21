package edu.ua.cs.taser.io;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public final class Files {

    public static boolean createDirectories(final String dirname) throws IOException {
        final Path dir = Paths.get(dirname);
        if (java.nio.file.Files.notExists(dir)) {
            java.nio.file.Files.createDirectories(dir);
        }
        return java.nio.file.Files.isDirectory(dir) & java.nio.file.Files.isWritable(dir);
    }

    public static List<Path> findFilesWithExtension(final Iterable<String> filenames, final String extension) {
        return findFilesWithExtensions(filenames, extension);
    }

    public static List<Path> findFilesWithExtensions(final Iterable<String> filenames, final String... extensions) {
        return Finder.findFilesWithExtensions(filenames, extensions);
    }

    private Files() {}
}
