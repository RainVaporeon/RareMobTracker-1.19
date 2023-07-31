package com.spiritlight.fishutils.predicates;

import java.util.function.DoublePredicate;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;

public class NumberPredicate {

    public static DoublePredicate negativeDouble() {
        return i -> i < 0;
    }

    public static IntPredicate negativeInt() {
        return i -> i < 0;
    }

    public static LongPredicate negativeLong() {
        return i -> i < 0;
    }

    public static DoublePredicate positiveDouble() {
        return i -> i > 0;
    }

    public static IntPredicate positiveInt() {
        return i -> i > 0;
    }

    public static LongPredicate positiveLong() {
        return i -> i > 0;
    }
}
