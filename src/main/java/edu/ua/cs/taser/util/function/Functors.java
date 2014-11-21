package edu.ua.cs.taser.util.function;

import rx.Observable;
import rx.util.functions.Func1;

public final class Functors {

    public static <T, R> Func1<Observable<T>, Observable<R>> map(final Func1<T, R> f) {
        return new Func1<Observable<T>, Observable<R>>() {
            @Override
            public Observable<R> call(Observable<T> t) {
                return t.map(f);
            }
        };
    }

    private Functors() {}
}
