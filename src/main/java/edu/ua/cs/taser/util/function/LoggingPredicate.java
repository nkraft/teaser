package edu.ua.cs.taser.util.function;

import rx.util.functions.Func1;

public class LoggingPredicate<T> implements Func1<T, Boolean> {

    public static <T> LoggingPredicate<T> from(final Predicate<? super T> p) {
        return new LoggingPredicate<T>(p);
    }

    private final Predicate<? super T> p;

    @Override
    public Boolean call(final T obj) {
        boolean value = p.call(obj);
        if (!value) {
            final StringBuilder sb = new StringBuilder();
            sb.append(obj.toString());
            sb.append(" does not satisfy the condition defined by ");
            sb.append(p.toString());
            System.err.println(sb.toString());
        }
        return value;
    }

    private LoggingPredicate(Predicate<? super T> p) {
        this.p = p;
    }
}
