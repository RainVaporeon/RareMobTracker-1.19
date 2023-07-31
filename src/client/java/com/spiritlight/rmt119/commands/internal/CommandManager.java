package com.spiritlight.rmt119.commands.internal;

import com.spiritlight.rmt119.events.Event;
import com.spiritlight.rmt119.events.bus.EventBus;
import com.spiritlight.rmt119.events.bus.EventBusAdapter;
import com.spiritlight.rmt119.events.bus.IEventBusSubscriber;
import com.spiritlight.rmt119.events.game.ClientCommandInitializationEvent;
import com.spiritlight.rmt119.utils.Side;

import java.util.*;

public class CommandManager extends EventBusAdapter {
    public static final CommandManager instance = new CommandManager();

    private final Set<AbstractCommandRegister<?>> commandList = new HashSet<>();

    private CommandManager() {
        EventBus.instance.subscribe(this);
    }

    public void addCommand(AbstractCommandRegister<?> command) {
        this.commandList.add(command);
    }

    public void removeCommand(AbstractCommandRegister<?> command) {
        this.commandList.remove(command);
    }

    @Override
    public void onClientCommandInitialization(ClientCommandInitializationEvent event) {
        for(AbstractCommandRegister<?> commandRegister : commandList) {
            if(commandRegister.getRegisterSide() == Side.CLIENT) {
                try {
                    commandRegister.register(Side.CLIENT);
                } catch (RegistrationFailureException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
