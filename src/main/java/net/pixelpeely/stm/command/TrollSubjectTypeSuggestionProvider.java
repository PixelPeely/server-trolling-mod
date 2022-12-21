package net.pixelpeely.stm.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;

import java.util.concurrent.CompletableFuture;

public class TrollSubjectTypeSuggestionProvider implements SuggestionProvider<ServerCommandSource> {

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext context, SuggestionsBuilder builder) throws CommandSyntaxException {
        try {
            String subjectType = StringArgumentType.getString(context, "subjectType");

            if ("Target".contains(subjectType))
                builder.suggest("Target");
            if ("Listener".contains(subjectType))
                builder.suggest("Listener");
        } catch (IllegalArgumentException e) {
            builder.suggest("Target");
            builder.suggest("Listener");
        }

        return builder.buildFuture();
    }
}
