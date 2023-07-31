package com.spiritlight.rmt119.commands;


import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.spiritlight.rmt119.Main;
import com.spiritlight.rmt119.commands.internal.AbstractCommandRegister;
import com.spiritlight.rmt119.commands.internal.Register;
import com.spiritlight.rmt119.utils.Side;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

@Register(Side.CLIENT)
public class RMTCommand extends AbstractCommandRegister<FabricClientCommandSource> {

    @Override
    protected LiteralArgumentBuilder<FabricClientCommandSource> getCommand() {
        return literal("rmt")
                .requires(src -> true)
                .executes(ctx -> {
                    ClientPlayerEntity player = ctx.getSource().getPlayer();
                    player.sendMessage(Text.of("""
                            [ - Rare Mob Tracker - | 1.19.4 Fabric Port ]
                            /rmt - Shows this message
                            /rmt include <string> - Adds a mob to inclusion
                            /rmt exclude <string> - Adds a mob to exclusion
                            /rmt toggle mod - Toggles mod status
                            /rmt toggle player - Toggles all player detection
                                                        
                            To remove an inclusion or exclusion, prefix with $
                            Example: /rmt include $void
                            To inspect the list, input 'list'
                            Example: /rmt include list
                            """));
                    return 1;
                })
                .then(literal("include")
                        .then(argument("include", StringArgumentType.greedyString())
                                .executes(ctx -> {
                                    ClientPlayerEntity player = ctx.getSource().getPlayer();
                                    String value = ctx.getArgument("include", String.class);
                                    if ("list".equalsIgnoreCase(value)) {
                                        player.sendMessage(Text.of("Current inclusion list: " +
                                                Main.config.getSeekNames()));
                                        return 1;
                                    }
                                    boolean invert = value.startsWith("$");
                                    String param = invert ? value.substring(1) : value;
                                    boolean contains = Main.config.getSeekNames().contains(param);
                                    if (contains) {
                                        // Removal
                                        if (invert) {
                                            boolean modified = Main.config.getSeekNames().remove(param);
                                            if (modified) {
                                                player.sendMessage(
                                                        Text.of("Successfully removed " + param + " from inclusion list!")
                                                );
                                            } else {
                                                player.sendMessage(
                                                        Text.of(param + " is not in the inclusion list!")
                                                );
                                            }
                                        } else {
                                            // Addition but already exists
                                            player.sendMessage(Text.of(
                                                    param + " is already in the inclusion list!"
                                            ));
                                        }
                                        return 1;
                                    }
                                    // Process addition, inversion already processed
                                    Main.config.getSeekNames().add(param);
                                    player.sendMessage(
                                            Text.of("Successfully added " + param + " into the inclusion list!")
                                    );
                                    return 1;
                                })))
                .then(literal("exclude").then(argument("exclude", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            ClientPlayerEntity player = ctx.getSource().getPlayer();
                            String value = ctx.getArgument("exclude", String.class);
                            if ("list".equalsIgnoreCase(value)) {
                                player.sendMessage(Text.of("Current exclusion list: " +
                                        Main.config.getIgnoredNames()));
                                return 1;
                            }
                            boolean invert = value.startsWith("$");
                            String param = invert ? value.substring(1) : value;
                            boolean contains = Main.config.getIgnoredNames().contains(param);
                            if (contains) {
                                // Removal
                                if (invert) {
                                    boolean modified = Main.config.getIgnoredNames().remove(param);
                                    if (modified) {
                                        player.sendMessage(
                                                Text.of("Successfully removed " + param + " from exclusion list!")
                                        );
                                    } else {
                                        player.sendMessage(
                                                Text.of(param + " is not in the exclusion list!")
                                        );
                                    }
                                } else {
                                    // Addition but already exists
                                    player.sendMessage(Text.of(
                                            param + " is already in the inclusion list!"
                                    ));
                                }
                                return 1;
                            }
                            // Process addition, inversion already processed
                            Main.config.getIgnoredNames().add(param);
                            player.sendMessage(
                                    Text.of("Successfully added " + param + " into the exclusion list!")
                            );
                            return 1;
                        })))
                .then(literal("toggle").then(argument("toggle", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            ClientPlayerEntity player = ctx.getSource().getPlayer();
                            String value = ctx.getArgument("toggle", String.class);

                            if ("mod".equalsIgnoreCase(value)) {
                                player.sendMessage(
                                        Text.of("Successfully toggled mod! Status: " + Main.config.toggleModEnabled())
                                );
                                return 1;
                            }
                            if ("player".equalsIgnoreCase(value)) {
                                player.sendMessage(
                                        Text.of("Successfully toggled player! Status: " + Main.config.toggleSeekPlayer())
                                );
                                return 1;
                            }
                            player.sendMessage(Text.of("Cannot parse the given key " + value + " to toggle!"));
                            return 1;
                        })));
    }
}
