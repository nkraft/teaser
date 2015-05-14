package edu.ua.cs.teaser.util;

import java.util.Deque;

public final class Stacks {

    public static <T> T pop(Deque<T> stack) {
        return stack.removeFirst();
    }

    public static <T> void push(Deque<T> stack, T item) {
        stack.addFirst(item);
    }

    public static <T> T top(Deque<T> stack) {
        return stack.peekFirst();
    }

    private Stacks() {}
}
