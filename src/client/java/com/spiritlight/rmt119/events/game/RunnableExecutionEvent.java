package com.spiritlight.rmt119.events.game;

import com.spiritlight.rmt119.events.Event;

public class RunnableExecutionEvent extends Event {
    private final long key;

    private final Runnable runnable;

    public RunnableExecutionEvent(long key, Runnable runnable) {
        this.key = key;
        this.runnable = runnable;
    }

    public RunnableExecutionEvent(Runnable runnable) {
        this(0L, runnable);
    }

    public Runnable getRunnable(long key) {
        if(this.key != key && this.key != 0L) throw new IllegalArgumentException("cannot accept key " + key);
        return runnable;
    }

    public boolean checkKey(long key) {
        return this.key == key;
    }
}
