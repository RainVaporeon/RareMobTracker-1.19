package com.spiritlight.rmt119.commands.internal;

import com.spiritlight.rmt119.utils.Side;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Useful annotation to specify the side of registration
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Register {
    Side value();
}
