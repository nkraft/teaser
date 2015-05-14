package edu.ua.cs.teaser.util.function;

import rx.Observable;
import rx.util.functions.Func1;

public final class Operators {

    public static <T> Observable<T> flatten(Observable<? extends Iterable<? extends T>> source) {
        return source.flatMap(new Func1<Iterable<? extends T>, Observable<T>>() {
            @Override
            public Observable<T> call(Iterable<? extends T> t1) {
                return Observable.from(t1);
            }
        });
    }

    private Operators() {}
}
