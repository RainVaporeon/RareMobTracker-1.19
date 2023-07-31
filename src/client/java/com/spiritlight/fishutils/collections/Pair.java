package com.spiritlight.fishutils.collections;

public interface Pair<K, V> {

    K getKey();

    V getValue();

    static <K, V> Pair<K, V> of(K key, V value) {
        return new Pair<>() {
            @Override
            public K getKey() {
                return key;
            }

            @Override
            public V getValue() {
                return value;
            }
        };
    }
}
