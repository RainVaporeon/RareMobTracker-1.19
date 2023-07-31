package com.spiritlight.fishutils.logging;

import java.io.File;

public final class Loggers {

    private static File DEFAULT = null;

    private Loggers() {

    }

    public static void setDefault(File out) {
        if(DEFAULT != null) throw new IllegalStateException("Log already set");
        DEFAULT = out;
    }

    public static Logger getThreadLogger() {
        return getThreadLogger(DEFAULT);
    }

    public static Logger getThreadLogger(File out) {
        return new Logger("Thread #" + Thread.currentThread().getId() + "/" + Thread.currentThread().getName(), out);
    }

    public static Logger getLogger(String name) {
        return new Logger(name, DEFAULT);
    }

    public static Logger getLogger(String name, File out) {
        return new Logger(name, out);
    }
}
