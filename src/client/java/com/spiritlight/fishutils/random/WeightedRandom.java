package com.spiritlight.fishutils.random;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class WeightedRandom<T> {
    private final Map<T, Integer> weightMap = new HashMap<>();
    private final Random RANDOM = new Random(this.nextSalt(System.currentTimeMillis()));

    private int totalWeight = 0;

    private final Object mutex = new Object();

    public T getNext() {
        synchronized (mutex) {
            int value = RANDOM.nextInt(totalWeight) + 1;
            for(Map.Entry<T, Integer> entry : weightMap.entrySet()) {
                value -= entry.getValue();
                if(value <= 0) {
                    return entry.getKey();
                }
            }
        }
        throw new IllegalStateException("totalWeight is larger than all items.");
    }


    public void addObject(T object, int weight) {
        if(weight < 0) throw new IllegalArgumentException("weight cannot be less than 0");
        weightMap.put(object, weight);

        synchronized (mutex) {
            updateWeight();
        }
    }

    public void removeObject(T object) {
        weightMap.remove(object);

        synchronized (mutex) {
            updateWeight();
        }
    }

    public double getProbabilityOf(T object) {
        if(!weightMap.containsKey(object)) return 0;
        return (double) weightMap.get(object) / totalWeight;
    }

    private void updateWeight() {
        totalWeight = weightMap.values().stream().mapToInt(Integer::intValue).sum();
    }

    private long nextSalt(long in) {
        long v1 = in * in >>> 2;
        long v2 = v1 << 1 + 1;
        long v3 = (int) Math.log(v2) * v2;
        return v3 << 2 + in ^ Objects.hash(in, v1, v2, v3);
    }
}
