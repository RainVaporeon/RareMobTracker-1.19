package com.spiritlight.rmt119;

import com.spiritlight.rmt119.events.bus.EventBus;
import com.spiritlight.rmt119.events.game.entity.EntityTrackingEvent;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;

public class ModInternals {
    static void initialize() {
        ClientEntityEvents.ENTITY_LOAD.register(((entity, world) -> {
            EventBus.instance.fire(new EntityTrackingEvent(entity));
        }));
    }
}
