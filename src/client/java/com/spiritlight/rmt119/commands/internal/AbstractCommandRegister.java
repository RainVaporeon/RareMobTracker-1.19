package com.spiritlight.rmt119.commands.internal;


import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.spiritlight.rmt119.utils.Side;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.impl.command.client.ClientCommandInternals;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;

public abstract class AbstractCommandRegister<S extends CommandSource> {
    private final boolean hasAnnotation = this.getClass().isAnnotationPresent(Register.class);
    private final Register annotation = this.getClass().getAnnotation(Register.class);

    protected Side getRegisterSide() {
        if(hasAnnotation) {
            return annotation.value();
        }
        return null;
    }

    abstract protected LiteralArgumentBuilder<S> getCommand();

    @SuppressWarnings("unchecked")
    protected final void register(Side side) throws RegistrationFailureException {
        try {
            switch(side) {
                case CLIENT -> {
                    if(ClientCommandInternals.getActiveDispatcher() == null) {
                        throw new RegistrationFailureException("Cannot register client command due to invalid dispatcher");
                    }
                    ClientCommandInternals.getActiveDispatcher().register(
                            (LiteralArgumentBuilder<FabricClientCommandSource>) getCommand()
                    );
                }
                case SERVER -> CommandRegistrationCallback.EVENT.register(
                        ((dispatcher, registryAccess, environment) -> dispatcher.register(
                                (LiteralArgumentBuilder<ServerCommandSource>) getCommand()
                        ))
                );
            }
        } catch (Exception e) {
            if(e instanceof RegistrationFailureException) throw e;
            throw new RegistrationFailureException(e);
        }
    }
}
