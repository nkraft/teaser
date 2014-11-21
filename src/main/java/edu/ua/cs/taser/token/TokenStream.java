package edu.ua.cs.taser.token;

import java.util.List;
import java.util.ListIterator;

public class TokenStream<T extends Token> {

    private ListIterator<T> iterator;
    private final List<T> tokens;
    // public String name;

    public TokenStream(final List<T> tokens) {
        this.tokens = tokens;
        iterator = this.tokens.listIterator(0);
    }

    public T getNext() {
        return iterator.next();
    }

    public T getPrevious() {
        // System.out.println("GET previous for " + name);
        // T t = iterator.previous();
        // System.out.println("GOT previous for " + name);
        // return t;
        return iterator.previous();
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public boolean hasPrevious() {
        return iterator.hasPrevious();
    }

    public boolean isEmpty() {
        return tokens.size() == 0;
    }

    public void remove() {
        iterator.remove();
    }

    public void replace(T token) {
        iterator.set(token);
    }

    public void replace(List<T> replacements) {
        iterator.set(replacements.get(0));
        final int size = replacements.size();
        for (int i = 1; i < size; i++) {
            iterator.add(replacements.get(i));
        }
    }

    public void reset() {
        iterator = tokens.listIterator(0);
    }
}
