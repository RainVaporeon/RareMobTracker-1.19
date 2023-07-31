package com.spiritlight.rmt119;

import com.spiritlight.fishutils.logging.Loggers;
import com.spiritlight.rmt119.commands.RMTCommand;
import com.spiritlight.rmt119.commands.internal.CommandManager;
import com.spiritlight.rmt119.config.Config;
import com.spiritlight.rmt119.events.bus.EventBus;
import com.spiritlight.rmt119.events.bus.EventBusAdapter;
import com.spiritlight.rmt119.events.game.ClientTickEndEvent;
import com.spiritlight.rmt119.events.game.RunnableExecutionEvent;
import com.spiritlight.rmt119.events.handlers.EntityTrackingHandler;
import net.fabricmc.api.ClientModInitializer;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Main extends EventBusAdapter implements ClientModInitializer {

	public static final Config config = new Config();

	public static final long EXECUTION_KEY = new Random().nextLong();

	static {
		config.deserialize().onFail(t -> {
			Loggers.getThreadLogger().warn("Couldn't deserialize, file is probably not found", t);
		});
		Runtime.getRuntime().addShutdownHook(new Thread(config::serialize));

	}

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		ModInternals.initialize();
		EventBus.instance.subscribe(new EntityTrackingHandler());
		EventBus.instance.subscribe(this);
		CommandManager.instance.addCommand(new RMTCommand());
	}

	private final Queue<Runnable> runnableQueue = new ConcurrentLinkedQueue<>();

	@Override
	public void onRunnableExecution(RunnableExecutionEvent event) {
		if(event.checkKey(EXECUTION_KEY)) {
			try {
				runnableQueue.add(event.getRunnable(EXECUTION_KEY));
			} catch (IllegalArgumentException ex) {
				Loggers.getThreadLogger().warn("Can't retrieve runnable from valid key " + EXECUTION_KEY);
			}
		}
	}

	@Override
	public void onClientTickEnd(ClientTickEndEvent event) {
		for(Runnable runnable : runnableQueue) {
			runnable.run();
		}
		runnableQueue.clear();
	}
}