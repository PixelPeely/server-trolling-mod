package net.pixelpeely.stm.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.pixelpeely.stm.STMMain;

import java.util.Collection;

public class TrollSubjectCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment){
        dispatcher.register(CommandManager.literal("trollSubject").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                .then(CommandManager.argument("subjectType", StringArgumentType.string())
                        .suggests(new TrollSubjectTypeSuggestionProvider())
                        .then(CommandManager.argument("operation", StringArgumentType.string())
                                .suggests(new TrollSubjectOperationSuggestionProvider())
                                .then(CommandManager.argument("target", EntityArgumentType.players())
                                        .executes(context -> {
                                            String invalidOperation = "Invalid operation type";
                                            Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "target");

                                            switch (StringArgumentType.getString(context, "subjectType")){
                                                case "Target":
                                                    switch (StringArgumentType.getString(context, "operation")) {
                                                        case "Add" -> players.forEach(player -> {
                                                            if (STMMain.targets.contains(player))
                                                                context.getSource().getPlayer().sendMessage(Text.literal(player.getEntityName() + "Is already a target!").formatted(Formatting.RED));
                                                            else
                                                                STMMain.targets.add(player);
                                                        });
                                                        case "Remove" -> STMMain.targets.removeAll(players);
                                                        default -> context.getSource().getPlayer().sendMessage(Text.literal(invalidOperation).formatted(Formatting.RED));
                                                    }
                                                    break;
                                                case "Listener":
                                                    switch (StringArgumentType.getString(context, "operation")) {
                                                        case "Add" -> players.forEach(player -> {
                                                            if (STMMain.listeners.contains(player))
                                                                context.getSource().getPlayer().sendMessage(Text.literal(player.getEntityName() + "Is already a listener!").formatted(Formatting.RED));
                                                            else
                                                                STMMain.listeners.add(player);
                                                        });
                                                        case "Remove" -> STMMain.listeners.removeAll(players);
                                                        default -> context.getSource().getPlayer().sendMessage(Text.literal(invalidOperation).formatted(Formatting.RED));
                                                    }
                                            }
                                            context.getSource().getPlayer().sendMessage(Text.of("Operation complete. If you want subjects to persist upon closing, please edit the config."));
                                            return 1;
                                        })))));
    }
}
