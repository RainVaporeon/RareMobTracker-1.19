package com.spiritlight.fishutils.action;

import com.spiritlight.fishutils.collections.Pair;

public interface ResultRunnable<V> {
    Pair<Result, V> run() throws Throwable;
}
