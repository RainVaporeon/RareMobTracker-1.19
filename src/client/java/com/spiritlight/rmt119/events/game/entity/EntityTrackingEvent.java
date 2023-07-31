package com.spiritlight.rmt119.events.game.entity;

import com.spiritlight.rmt119.events.Event;
import com.spiritlight.rmt119.events.bus.EventBus;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;

public class EntityTrackingEvent extends Event{

    private final Entity entity;

    public EntityTrackingEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

}
