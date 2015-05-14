package edu.ua.cs.teaser.util.function;

import rx.util.functions.Func1;

public abstract class Predicate<T> implements Func1<T, Boolean> {
    public LoggingPredicate<T> toLoggingPredicate() {
        return LoggingPredicate.from(this);
    }
}
