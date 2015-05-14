package edu.ua.cs.teaser.common;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Callable;

public final class FileContentLoader implements Callable<char[]> {

    private static final int BUFFER_SIZE = 16 * 1024;

    private final Path path;
    private final Charset cs;

    public FileContentLoader(final Path path) {
        this(path, StandardCharsets.UTF_8);
    }

    public FileContentLoader(final Path path, final Charset cs) {
        this.path = path;
        this.cs = cs;
    }

    public char[] call() {
        char[] content = null;
        InputStream is = null;
        InputStreamReader in = null;
        try {
            is = Files.newInputStream(path, StandardOpenOption.READ);
            in = new InputStreamReader(is, cs);
            final StringBuilder sb = new StringBuilder();
            char[] buf = new char[BUFFER_SIZE];
            int c = in.read(buf);
            while (c != -1) {
                sb.append(buf, 0, c);
                c = in.read(buf);
            }
            c = sb.length();
            content = new char[c];
            sb.getChars(0, c, content, 0);
        } catch (IOException e) {
            content = new char[0];
        } finally {
            try {
                if (in != null) {
                    in.close();
                } else if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                ;
            }
        }
        return content;
    }
}

