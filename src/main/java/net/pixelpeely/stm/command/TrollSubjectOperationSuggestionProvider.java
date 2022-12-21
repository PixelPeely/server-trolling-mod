package net.pixelpeely.stm.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

public class TrollSubjectOperationSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext context, SuggestionsBuilder builder) throws CommandSyntaxException {
        try {
            String operation = StringArgumentType.getString(context, "operation");

            if ("Add".contains(operation))
                builder.suggest("Add");
            if ("Remove".contains(operation))
                builder.suggest("Remove");
        } catch (IllegalArgumentException e) {
            builder.suggest("Add");
            builder.suggest("Remove");
        }

        return builder.buildFuture();
    }
}
