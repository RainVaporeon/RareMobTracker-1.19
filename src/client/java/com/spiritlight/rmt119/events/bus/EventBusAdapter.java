package com.spiritlight.rmt119.events.bus;

import com.spiritlight.rmt119.events.Event;
import com.spiritlight.rmt119.events.game.ClientCommandInitializationEvent;
import com.spiritlight.rmt119.events.game.RunnableExecutionEvent;
import com.spiritlight.rmt119.events.game.entity.EntityTrackingEvent;

public class EventBusAdapter implements IEventBusSubscriber {

    public void onEntityTracking(EntityTrackingEvent event) {

    }

    public void onClientCommandInitialization(ClientCommandInitializationEvent event) {

    }

    public void onRunnableExecution(RunnableExecutionEvent event) {

    }

    @Override
    public final void onEvent(Event event) {
        if(event instanceof EntityTrackingEvent e) {
            this.onEntityTracking(e);
        }
        if(event instanceof ClientCommandInitializationEvent e) {
            this.onClientCommandInitialization(e);
        }
        if(event instanceof RunnableExecutionEvent e) {
            this.onRunnableExecution(e);
        }
    }
}
