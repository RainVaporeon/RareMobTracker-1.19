package com.spiritlight.rmt119;

import com.spiritlight.rmt119.events.Event;
import com.spiritlight.rmt119.events.bus.EventBus;
import com.spiritlight.rmt119.events.game.ClientTickEndEvent;
import com.spiritlight.rmt119.events.game.entity.EntityTrackingEvent;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class ModInternals {
    static void initialize() {
        ClientEntityEvents.ENTITY_LOAD.register(((entity, world) -> {
            EventBus.instance.fire(new EntityTrackingEvent(entity));
        }));

        ClientTickEvents.END_CLIENT_TICK.register((client -> {
            EventBus.instance.fire(new ClientTickEndEvent());
        }));
    }
}
