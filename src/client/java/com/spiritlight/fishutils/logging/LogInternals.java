package com.spiritlight.fishutils.logging;

import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class LogInternals {

    private LogInternals() {}

    private static final List<File> sharedFileNames = new CopyOnWriteArrayList<>();

    private static final Object accessor = new Object();

    static File getSharedObjectIfPresent(File file) {
        synchronized (accessor) {
            int index = sharedFileNames.indexOf(file);
            if(index >= 0) return sharedFileNames.get(index);
            return file;
        }
    }

    static void appendFileIfAbsent(File file) {
        synchronized (accessor) {
            if(sharedFileNames.contains(file)) return;
            sharedFileNames.add(file);
        }
    }

    static void removeFile(File file) {
        synchronized (accessor) {
            sharedFileNames.remove(file);
        }
    }
}
