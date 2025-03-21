package dev.jabberdrake.charter.commands.suggestions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.jabberdrake.charter.jade.players.PlayerManager;
import dev.jabberdrake.charter.realms.RealmManager;
import dev.jabberdrake.charter.realms.Settlement;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CommonSettlementSuggestions {

    public static CompletableFuture<Suggestions> buildSuggestionsForAllSettlements(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {
        final List<String> stmNames = new ArrayList<>();

        RealmManager.getAllSettlements().forEach(settlement -> {
            stmNames.add(settlement.getName());
        });

        stmNames.stream()
                .filter(entry -> entry.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                .forEach(builder::suggest);

        return builder.buildFuture();
    }

    public static CompletableFuture<Suggestions> buildSuggestionsForSettlementsWithPlayer(final CommandContext<CommandSourceStack> context, final SuggestionsBuilder builder) {
        final List<String> stmNames = new ArrayList<>();
        Player player = (Player) context.getSource().getSender();

        RealmManager.getSettlementsForPlayer(player).forEach(settlement -> {
            stmNames.add(settlement.getName());
        });

        stmNames.stream()
                .filter(entry -> entry.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                .forEach(builder::suggest);

        return builder.buildFuture();
    }

}
