package com.spiritlight.fishutils.objects;

import java.util.function.Consumer;
import java.util.function.Function;

public class ObjectFunction<T> {
    private final T t;

    public ObjectFunction(T t) {
        this.t = t;
    }

    public <R> R run(Function<T, R> function) {
        return function.apply(t);
    }

    public void consume(Consumer<T> consumer) {
        consumer.accept(t);
    }
}
