package com.mishkin.redsecbot.discord.handler;

import com.mishkin.redsecbot.application.service.PlayerStatsHistoryService;
import com.mishkin.redsecbot.discord.formatter.RedSecDiscordFormatter;
import com.mishkin.redsecbot.discord.utils.ExceptionUtils;
import com.mishkin.redsecbot.domain.model.RedSecStats;
import com.mishkin.redsecbot.infrastructure.tracker.dto.in.player.TrackerSearchResultApiDto;
import com.mishkin.redsecbot.infrastructure.tracker.client.TrackerGGPlayerSearchClient;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * @author a.mishkin
 */
@Component
public class CheckCommandHandler {

    private final TrackerGGPlayerSearchClient searchClient;
    private final PlayerStatsHistoryService statsHistoryService;
    private final RedSecDiscordFormatter formatter;
    private final PlayerSelectionStore selectionStore;
    private final ExecutorService executor;

    public CheckCommandHandler(TrackerGGPlayerSearchClient searchClient, PlayerStatsHistoryService statsHistoryService, RedSecDiscordFormatter formatter, PlayerSelectionStore selectionStore, ExecutorService executor) {
        this.searchClient = searchClient;
        this.statsHistoryService = statsHistoryService;
        this.formatter = formatter;
        this.selectionStore = selectionStore;
        this.executor = executor;
    }

    public void handle(SlashCommandInteractionEvent event) {

        event.deferReply().queue();

        String bfName = event.getOption("name").getAsString();
        String platform = event.getOption("platform").getAsString();
        long discordId = event.getUser().getIdLong();

        CompletableFuture
                .supplyAsync(() -> searchClient.searchPlayers(platform, bfName), executor)
                .thenCompose(players -> {

                    if (players.isEmpty()) {
                        event.getHook()
                                .sendMessage("❌ Игрок не найден: **" + bfName + "**")
                                .queue();
                        return CompletableFuture.completedFuture(null);
                    }

                    if (players.size() > 1) {
                        selectionStore.put(discordId, players);

                        event.getHook()
                                .sendMessage("Найдено несколько профилей. Выбери нужный:")
                                .addActionRow(buildSelectMenu(players))
                                .queue();

                        return CompletableFuture.completedFuture(null);
                    }

                    var p = players.get(0);

                    return CompletableFuture
                            .supplyAsync(() -> fetchStatsForSelected(platform, p), executor)
                            .thenAccept(statsOpt -> {

                                if (statsOpt.isEmpty()) {
                                    event.getHook()
                                            .sendMessage("❌ Игрок не играл в REDSEC")
                                            .setEphemeral(true)
                                            .queue();
                                    return;
                                }

                                event.getHook()
                                        .sendMessageEmbeds(formatter.format(statsOpt.get()))
                                        .queue();
                            });
                })
                .exceptionally(ex -> {
                    event.getHook()
                            .sendMessage("❌ " + ExceptionUtils.safeMessage(ex))
                            .queue();
                    return null;
                });
    }

    private Optional<RedSecStats> fetchStatsForSelected(String platform, TrackerSearchResultApiDto p) {
        String playerKey = "bf:" + platform + ":" + p.platformUserIdentifier();
        return statsHistoryService.getRedSecStats(playerKey, p.platformSlug(), p.platformUserIdentifier());
    }

    private StringSelectMenu buildSelectMenu(List<TrackerSearchResultApiDto> players) {
        return StringSelectMenu.create("check-select")
                .setPlaceholder("Выбери профиль")
                .addOptions(players.stream()
                        .limit(5)
                        .map(p -> SelectOption.of(formatLabel(p), p.platformUserIdentifier()))
                        .toList())
                .build();
    }

    private String formatLabel(TrackerSearchResultApiDto player) {
        String cc = (player.metadata() != null && player.metadata().countryCode() != null) ? player.metadata().countryCode() : "?";
        return player.platformUserHandle() + " | " + cc + " | " + player.status();
    }
}
