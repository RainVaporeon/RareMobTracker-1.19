package com.spiritlight.fishutils.misc;

public interface ThrowingSupplier<T> {
    T get() throws Throwable;
}
