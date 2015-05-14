package edu.ua.cs.teaser.lang;

import java.util.Iterator;

public final class Strings {

    public static int countMatches(final String str, final String sub) {
        int matches = 0;
        int index = 0;
        while ((index = str.indexOf(sub, index)) != -1) {
            ++matches;
            index += sub.length();
        }
        return matches;
    }

    public static String join(final Iterable<?> iterable, final char separator) {
        return join(iterable.iterator(), separator);
    }

    public static String join(final Iterator<?> iterator, final char separator) {
        if (!iterator.hasNext()) {
            return "";
        }
        final Object first = iterator.next();
        if (!iterator.hasNext()) {
            return first.toString();
        }
        final int guess = 3 * (16 + 1);
        final StringBuilder sb = new StringBuilder(guess);
        sb.append(first);
        while (iterator.hasNext()) {
            sb.append(separator);
            sb.append(iterator.next());
        }
        return sb.toString();
    }

    public static String join(final Object[] array, final char separator) {
        final int count = array.length;
        final int guess = count * (16 + 1);
        final StringBuilder sb = new StringBuilder(guess);
        for (int i = 0; i < count; ++i) {
            if (i > 0) {
                sb.append(separator);
            }
            sb.append(array[i]);
        }
        return sb.toString();
    }

    // public static String join(final Object[] array, final String separator) {
    //     final int count = array.length;
    //     final int guess = count * (16 + separator.length());
    //     final StringBuilder sb = new StringBuilder(guess);
    //     for (int i = 0; i < count; ++i) {
    //         if (i > 0) {
    //             sb.append(separator);
    //         }
    //         sb.append(array[i]);
    //     }
    //     return sb.toString();
    // }

    private Strings() {}
}
