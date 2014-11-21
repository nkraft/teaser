package edu.ua.cs.taser.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOError;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class LineReader implements Iterable<String> {

    public static LineReader forFile(final File file) {
        try {
            return new LineReader(new FileInputStream(file));
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    public static LineReader forResource(final String res) {
        final InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(res);
        if (is != null) {
            return new LineReader(is);
        } else {
            throw new IOError(new IOException("Failed to create input stream for resource '" + res + "'"));
        }
    }

    private InputStream in;

    public LineReader(InputStream in) {
        this.in = in;
    }

    public Iterator<String> iterator() {
        return new LineIterator();
    }

    private class LineIterator implements Iterator<String> {

        private final BufferedReader br;
        private String line;

        public LineIterator() {
            br = new BufferedReader(new InputStreamReader(in));
            advance();
        }

        public boolean hasNext() {
            return line != null;
        }

        public String next() {
            if (line == null) {
                throw new NoSuchElementException();
            }
            String next = line;
            advance();
            return next;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        private void advance() {
            try {
                line = br.readLine();
                if (line == null) {
                    br.close();
                }
            }
            catch (IOException e) {
                throw new IOError(e);
            }
        }
    }
}
