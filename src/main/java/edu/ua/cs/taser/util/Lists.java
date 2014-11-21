package edu.ua.cs.taser.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class Lists {

    public static <U extends Comparable<? super U>, T extends U>
            int bisect(List<U> list, T element) {
        int index = Collections.binarySearch(list, element);
        return (index < 0) ? -(index) - 1 : index;
    }

    public static <U, T extends U, S extends Comparator<? super U>>
            int bisect(List<U> list, T element, S c) {
        int index = Collections.binarySearch(list, element, c);
        return (index < 0) ? -(index) - 1 : index;
    }

    public static <U extends Comparable<? super U>, T extends U>
            void insort(List<U> list, T element) {
        list.add(bisect(list, element), element);
    }

    public static <U, T extends U, S extends Comparator<? super U>>
            void insort(List<U> list, T element, S c) {
        list.add(bisect(list, element, c), element);
    }

    private Lists() {}
}
