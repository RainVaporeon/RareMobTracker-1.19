package com.spiritlight.fishutils.objects;

import java.util.function.Function;
import java.util.function.Supplier;

public class ObjectUtils {

    public static <T, R> R evaluateOrDefault(T object, R defaultValue, Function<T, R> mapper) {
        if(object == null) return defaultValue;
        return mapper.apply(object);
    }

    public static boolean assertOrElse(Supplier<Boolean> test, Runnable otherwise) {
        if(test.get()) {
            return true;
        } else {
            otherwise.run();
            return false;
        }
    }

    public static <T> boolean execute(Supplier<T> sup) {
        try {
            sup.get();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
