package com.spiritlight.fishutils.logging;

public interface ILogger {

    void newline();

    default void success(String message) {
        success(message, null);
    }

    void success(String message, Throwable t);

    default void info(String message) {
        info(message, null);
    }

    void info(String message, Throwable t);

    default void warn(String message) {
        warn(message, null);
    }

    void warn(String message, Throwable t);

    default void error(String message) {
        error(message, null);
    }

    void error(String message, Throwable t);

    default void fatal(String message) {
        fatal(message, null);
    }

    void fatal(String message, Throwable t);

    void debug(String message);

    static ILogger of(String name) {
        return new Logger(name);
    }
}
