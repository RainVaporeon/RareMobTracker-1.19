package com.spiritlight.fishutils.math;

public class Numbers {

    public static short clamp(short val, short min, short max) {
        if(val > max) return max;
        if(val < min) return min;
        return val;
    }

    public static double clamp(double val, double min, double max) {
        if(val > max) return max;
        return Math.max(val, min);
    }

    public static int clamp(int val, int min, int max) {
        if(val > max) return max;
        return Math.max(val, min);
    }

    public static long clamp(long val, long min, long max) {
        if(val > max) return max;
        return Math.max(val, min);
    }

    public static float clamp(float val, float min, float max) {
        if(val > max) return max;
        return Math.max(val, min);
    }

    public static boolean in(long val, long min, long max) {
        return val >= min && val < max;
    }

    public static boolean in(double val, double min, double max) {
        return val >= min && val < max;
    }
}
