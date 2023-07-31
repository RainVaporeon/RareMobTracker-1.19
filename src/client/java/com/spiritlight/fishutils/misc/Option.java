package com.spiritlight.fishutils.misc;

import java.util.HashMap;
import java.util.Map;

public class Option {
    private static final Map<String, Option> map = new HashMap<>();

    private static final Object readlock = new Object();

    private static final Object writelock = new Object();

    private final Object value;

    private Option(String key, Object value) {
        if(map.containsKey(key)) throw new IllegalArgumentException("option " + key + " already registered");
        this.value = value;
        map.put(key, this);
    }

    public static Option register(String key, Object value) {
        return new Option(key, value);
    }

    public static Option update(String key, Object value) {
        synchronized (writelock) {
            Option defer = map.remove(key);
            Option.register(key, value);
            return defer;
        }
    }

    public static boolean remove(String key) {
        synchronized (writelock) {
            return map.remove(key) != null;
        }
    }

    public Object get() {
        return this.value;
    }

    @SuppressWarnings("unchecked")
    public <T> T auto() {
        return (T) this.value;
    }

    public static Object get(String key) {
        synchronized (readlock) {
            return map.get(key).get();
        }
    }

    public static <T> T auto(String key) {
        synchronized (readlock) {
            return map.get(key).auto();
        }
    }

    public static Option getOption(String key) {
        synchronized (readlock) {
            return map.get(key);
        }
    }

    public int getAsInt() {
        if(this.value instanceof Integer) return (int) this.value;
        try {
            return Integer.parseInt(String.valueOf(this.value));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(this.value + " cannot be casted to int");
        }
    }

    public double getAsDouble() {
        if(this.value instanceof Number n) return n.doubleValue();
        try {
            return Double.parseDouble(String.valueOf(this.value));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(this.value + " cannot be casted to int");
        }
    }

    public String getAsString() {
        return String.valueOf(this.value);
    }
}
