package net.pixelpeely.stm.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.pixelpeely.stm.TrollHandler;

public class TrollCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("troll")
                .then(CommandManager.argument("target", EntityArgumentType.players())
                        .then(CommandManager.argument("trollId", IntegerArgumentType.integer(0, TrollHandler.numTrolls() - 1))
                                .executes(context -> {
                                    EntityArgumentType.getPlayers(context, "target").forEach((target) -> TrollHandler.executeTroll(target, IntegerArgumentType.getInteger(context, "trollId")));
                                    return 1;
                                }))));
    }
}
