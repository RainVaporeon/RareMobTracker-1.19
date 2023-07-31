package com.spiritlight.rmt119.events.bus;

import com.spiritlight.rmt119.events.Event;

public interface IEventBusSubscriber {

    void onEvent(Event event);

}
