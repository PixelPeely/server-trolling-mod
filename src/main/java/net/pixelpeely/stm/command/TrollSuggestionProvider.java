package net.pixelpeely.stm.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;
import net.pixelpeely.stm.util.Trolls;

import java.util.concurrent.CompletableFuture;

public class TrollSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> ctx, SuggestionsBuilder builder) throws CommandSyntaxException {
        try {
            String input = StringArgumentType.getString(ctx, "trollId");

            Trolls.trolls.keySet().forEach((troll) -> {
                if (troll.contains(input))
                    builder.suggest(troll);
            });
            if ("RANDOM".contains(input))
                builder.suggest("RANDOM");
            if ("ALL".contains(input))
                builder.suggest("ALL");
        } catch (IllegalArgumentException e) {
            Trolls.trolls.keySet().forEach(builder::suggest);
            builder.suggest("RANDOM");
            builder.suggest("ALL");
        }

        return builder.buildFuture();
    }
}
