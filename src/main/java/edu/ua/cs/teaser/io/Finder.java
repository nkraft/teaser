package edu.ua.cs.teaser.io;

import static edu.ua.cs.teaser.lang.Strings.join;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.FileVisitResult;
import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.SKIP_SUBTREE;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Finder extends SimpleFileVisitor<Path> {

    public static List<Path> findFilesWithExtensions(final Iterable<String> filenames, final String... extensions) {
        final List<Path> paths = new LinkedList<Path>();
        final PathMatcher fileMatcher = newPathMatcher(newGlobFromExtensions(extensions));
        for (final String filename : filenames) {
            final Path path = Paths.get(filename);
            if (Files.isDirectory(path, NOFOLLOW_LINKS)) {
                paths.addAll(findFilesWithExtensions(path, fileMatcher));
            } else if (Files.isRegularFile(path, NOFOLLOW_LINKS) && keepFile(path, fileMatcher)) {
                paths.add(path);
            }
        }
        return paths;
    }

    private static List<Path> findFilesWithExtensions(final Path dir, final PathMatcher fileMatcher) {
        List<Path> files = null;
        try {
            final Finder finder = new Finder(fileMatcher);
            Files.walkFileTree(dir, finder);
            files = finder.files;
        } catch (IOException e) {
            if (files == null) {
                files = new LinkedList<Path>();
            }
        }
        return files;
    }

    private static String newGlobFromExtensions(final String[] extensions) {
        return "glob:*.{" + join(extensions, ',') + "}";
    }

    private static PathMatcher newPathMatcher(final String glob) {
        return FileSystems.getDefault().getPathMatcher(glob);
    }

    private static boolean keepFile(final Path file, final PathMatcher matcher) {
        final Path name = file.getFileName();
        return Files.isReadable(file) && (name != null) && matcher.matches(name);
    }

    private static List<String> EXCLUDE_DIRS;
    static {
        final String[] dirs = {".git", ".hg", ".svn", "Attic", "CVS", "SCCS"};
        EXCLUDE_DIRS = Arrays.asList(dirs);
    }

    private static boolean skipDirectory(final Path dir) {
        return EXCLUDE_DIRS.contains(dir.getFileName());
    }

    private final List<Path> files = new LinkedList<Path>();
    private final PathMatcher fileMatcher;

    protected Finder(final PathMatcher fileMatcher) {
        this.fileMatcher = fileMatcher;
    }

    @Override
    public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attr) {
        return skipDirectory(dir) ? SKIP_SUBTREE : CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(final Path file, final BasicFileAttributes attr) {
        if (keepFile(file, fileMatcher)) {
            files.add(file);
        }
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(final Path file, final IOException e) {
        return CONTINUE;
    }
}
