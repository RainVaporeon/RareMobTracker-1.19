package com.spiritlight.fishutils.misc;

public interface ThrowingFunction<T, R> {
    R apply(T t) throws Throwable;
}
