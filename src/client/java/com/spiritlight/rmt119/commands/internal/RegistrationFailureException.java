package com.spiritlight.rmt119.commands.internal;

public class RegistrationFailureException extends Exception {
    public RegistrationFailureException(Throwable cause) {
        super(cause);
    }

    public RegistrationFailureException(String message) {
        super(message);
    }

    public RegistrationFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
