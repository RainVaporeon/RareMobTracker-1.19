package com.spiritlight.rmt119.events.handlers;

import com.spiritlight.rmt119.Main;
import com.spiritlight.rmt119.events.bus.EventBus;
import com.spiritlight.rmt119.events.bus.EventBusAdapter;
import com.spiritlight.rmt119.events.game.RunnableExecutionEvent;
import com.spiritlight.rmt119.events.game.entity.EntityTrackingEvent;
import com.spiritlight.rmt119.utils.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EntityTrackingHandler extends EventBusAdapter {

    private static final ScheduledExecutorService THREAD_POOL = Executors.newScheduledThreadPool(50);

    @Override
    public void onEntityTracking(EntityTrackingEvent event) {
        if(MinecraftClient.getInstance().player == null) return;
        Entity entity = event.getEntity();
        THREAD_POOL.schedule(() -> detectEntity(entity), 55, TimeUnit.MILLISECONDS);
    }

    private void detectEntity(Entity entity) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if(MinecraftClient.getInstance().player == null) return;
        String entityName = entity.getDisplayName().getString();
        int blockX = entity.getBlockX();
        int blockY = entity.getBlockY();
        int blockZ = entity.getBlockZ();
        if(entity instanceof ServerPlayerEntity) {
            if(!Main.config.doSeekPlayer()) return;

            playAlertSoundConcurrent();

            Text text = this.setClickableStyle(this.getMobAlert(
                    Type.PLAYER, entityName, blockX, blockY, blockZ
            ), blockX, blockY, blockZ);

            sendMessageConcurrent(player, text);
            return;
        }
        if(entity instanceof PlayerEntity) return;
        if(Main.config.getSeekNames().stream().anyMatch(name -> entityName.toLowerCase(Locale.ROOT).contains(name.toLowerCase(Locale.ROOT)))) {
            playAlertSoundConcurrent();

            entity.setGlowing(true);

            Text text = this.setClickableStyle(this.getMobAlert(
                    Type.MOB, entityName, blockX, blockY, blockZ
            ), blockX, blockY, blockZ);

            sendMessageConcurrent(player, text);
            return;
        }

        boolean rare = entityName.contains(SharedConstants.RARE_MOB_ICON);
        if(!rare) return;
        // Assume rare

        if(Main.config.getIgnoredNames().stream().noneMatch(name -> entityName.toLowerCase(Locale.ROOT).contains(name.toLowerCase(Locale.ROOT)))) {
            playAlertSoundConcurrent();

            entity.setGlowing(true);

            Text text = this.setClickableStyle(this.getMobAlert(
                    Type.RARE_MOB, entityName, blockX, blockY, blockZ
            ), blockX, blockY, blockZ);

            sendMessageConcurrent(player, text);
        }
    }

    private void sendMessageConcurrent(ClientPlayerEntity entity, Text message) {
        EventBus.instance.fire(new RunnableExecutionEvent(Main.EXECUTION_KEY, () -> {
            entity.sendMessage(message);
        }));
    }

    private void playAlertSoundConcurrent() {
        EventBus.instance.fire(new RunnableExecutionEvent(Main.EXECUTION_KEY, this::playAlertSound));
    }

    private void playAlertSound() {
        World world = MinecraftClient.getInstance().world;

        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        if(world == null || player == null) return;

        world.playSound(
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
                SoundCategory.MASTER,
                1.0f, 1.0f, false
        );
    }

    private MutableText getMobAlert(Type type, String name, int x, int y, int z) {
        Formatting wrappingFormatting = switch (type) {
            case MOB -> Formatting.AQUA;
            case PLAYER -> Formatting.GREEN;
            case RARE_MOB -> Formatting.GOLD;
        };

        Formatting exclamationFormatting = switch (type) {
            case MOB -> Formatting.GOLD;
            case RARE_MOB -> Formatting.YELLOW;
            case PLAYER -> Formatting.RED;
        };

        String representation = switch (type) {
            case MOB -> "Mob";
            case PLAYER -> "Player";
            case RARE_MOB -> "Rare Mob";
        };

        Formatting textColor = switch(type) {
            case RARE_MOB -> Formatting.GREEN;
            case MOB -> Formatting.AQUA;
            case PLAYER -> Formatting.GREEN;
        };

        return Text.literal(
                wrappingFormatting + "[" + exclamationFormatting + "!" + wrappingFormatting + "] " +
                        textColor + representation + " " + name + textColor + " was found at " +
                        x + ", " + y + ", " + z + "!"
        );
    }

    private MutableText setClickableStyle(MutableText text, int x, int y, int z) {
        return text.setStyle(text.getStyle().withHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT, Text.of(
                Formatting.GOLD + "Click to track!"
        )
        )).withClickEvent(new ClickEvent(
                ClickEvent.Action.RUN_COMMAND, "/compass " + x + " " + y + " " + z
        )));
    }

    enum Type {
        RARE_MOB,
        MOB,
        PLAYER
    }
}
