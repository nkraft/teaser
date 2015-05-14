package edu.ua.cs.teaser.io;

import static edu.ua.cs.teaser.lang.Strings.join;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Paths;

public final class Filenames {

    private static final char EXTENSION_SEPARATOR_CHAR = '.';

    public static String addExtension(final String filename, final String extension) {
        return filename + EXTENSION_SEPARATOR_CHAR + extension;
    }

    public static String getBasename(final String filename) {
        final int index = indexOfLastSeparator(filename);
        return (index == -1) ? filename : filename.substring(index + 1);
    }

    public static String getBasename(final String filename, final String extension) {
        final int sepIndex = indexOfLastSeparator(filename);
        String basename = (sepIndex == -1) ? filename : filename.substring(sepIndex + 1);
        final int extIndex = basename.lastIndexOf(EXTENSION_SEPARATOR_CHAR);
        if (extIndex > 0) {
            if(extension.equals(basename.substring(extIndex + 1))) {
                basename = basename.substring(0, extIndex);
            }
        }
        return basename;
    }

    public static String getDirname(final String filename) {
        final int index = indexOfLastSeparator(filename);
        return (index == -1) ? "" : filename.substring(0, index);
    }

    public static String getExtension(final String filename) {
        final int index = indexOfExtension(filename);
        return (index == -1) ? "" : filename.substring(index + 1);
    }

    public static char getExtensionSeparatorChar() {
        return EXTENSION_SEPARATOR_CHAR;
    }

    public static int indexOfExtension(final String filename) {
        final int index = filename.lastIndexOf(EXTENSION_SEPARATOR_CHAR);
        final int minIndex = Math.min(0, indexOfLastSeparator(filename));
        return (index <= minIndex) ? -1 : index;
    }

    public static int indexOfFirstSeparator(final String filename) {
        return filename.indexOf(File.separatorChar);
    }

    public static int indexOfLastSeparator(final String filename) {
        return filename.lastIndexOf(File.separatorChar);
    }

    public static boolean hasExtension(final String filename, final String extension) {
        if (extension.length() == 0) {
            return indexOfExtension(filename) == -1;
        }
        return extension.equals(getExtension(filename));
    }

    public static boolean hasExtension(final String filename, final String[] extensions) {
        final String filenameExt = getExtension(filename);
        for (final String extension : extensions) {
            if (extension.equals(filenameExt)) {
                return true;
            }
        }
        return false;
    }

    public static String makeFilename(final String dirname, final String basename, final String extension) {
        return makePath(dirname, addExtension(basename, extension));
    }

    public static String makePath(final String... paths) {
        return join(paths, File.separatorChar);
    }

    public static String normalize(final String filename) {
        return Paths.get(filename).normalize().toString();
    }

    private Filenames() {}
}
