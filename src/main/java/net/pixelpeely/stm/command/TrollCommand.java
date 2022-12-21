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
import net.pixelpeely.stm.util.Trolls;

import java.util.Collection;
import java.util.function.Consumer;

public class TrollCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("troll").requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                .then(CommandManager.argument("target", EntityArgumentType.players())
                        .then(CommandManager.argument("trollId", StringArgumentType.string())
                                .suggests(new TrollSuggestionProvider())
                                .executes(context -> {
                                    Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "target");
                                    String id = StringArgumentType.getString(context, "trollId");

                                    switch (id){
                                        case "RANDOM":
                                            Trolls.getRandomTrolls().forEach(troll -> targets.forEach(target -> Trolls.trollPlayer(troll, target)));
                                            return 1;
                                        case "ALL":
                                            Trolls.trolls.values().forEach(troll -> targets.forEach(target -> Trolls.trollPlayer(troll, target)));
                                            return 1;
                                    }

                                    Consumer<ServerPlayerEntity> troll = Trolls.getTroll(StringArgumentType.getString(context, "trollId"));
                                    if (troll == null) {
                                        context.getSource().getPlayer().sendMessage(Text
                                                .literal("Please provide a valid troll ID.")
                                                .formatted(Formatting.RED));

                                        return 1;
                                    }
                                    targets.forEach(target -> Trolls.trollPlayer(troll, target));
                                    return 1;
                                }))));
    }
}
