package com.mishkin.redsecbot.discord.handler;

import com.mishkin.redsecbot.application.event.StatsReadyPublisher;
import com.mishkin.redsecbot.application.service.PlayerStatsHistoryService;
import com.mishkin.redsecbot.domain.model.StatsWithSource;
import com.mishkin.redsecbot.discord.formatter.RedSecDiscordFormatter;
import com.mishkin.redsecbot.discord.reply.DiscordReplyRegistry;
import com.mishkin.redsecbot.domain.model.GameIdentity;
import com.mishkin.redsecbot.domain.model.RedSecStats;
import com.mishkin.redsecbot.infrastructure.tracker.client.TrackerGGPlayerSearchClient;
import com.mishkin.redsecbot.infrastructure.tracker.dto.in.player.TrackerSearchResultApiDto;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * @author a.mishkin
 */
@Component
public class CheckCommandHandler {

    Logger log = LoggerFactory.getLogger(CheckCommandHandler.class);

    private final TrackerGGPlayerSearchClient searchClient;
    private final PlayerStatsHistoryService statsHistoryService;
    private final RedSecDiscordFormatter formatter;
    private final PlayerSelectionStore selectionStore;
    private final Executor executor;
    private final StatsReadyPublisher statsReadyPublisher;
    private final DiscordReplyRegistry replyRegistry;

    public CheckCommandHandler(TrackerGGPlayerSearchClient searchClient, PlayerStatsHistoryService statsHistoryService, RedSecDiscordFormatter formatter, PlayerSelectionStore selectionStore, Executor executor, StatsReadyPublisher statsReadyPublisher, DiscordReplyRegistry replyRegistry) {
        this.searchClient = searchClient;
        this.statsHistoryService = statsHistoryService;
        this.formatter = formatter;
        this.selectionStore = selectionStore;
        this.executor = executor;
        this.statsReadyPublisher = statsReadyPublisher;
        this.replyRegistry = replyRegistry;
    }

    public void handle(SlashCommandInteractionEvent event) {

        event.deferReply(true).queue();
        String bfName = event.getOption("name").getAsString();
        String platform = event.getOption("platform").getAsString();
        long discordId = event.getUser().getIdLong();

        AtomicBoolean replied = new AtomicBoolean(false);

        Consumer<String> replyOnce = msg -> {
            if (replied.compareAndSet(false, true)) {
                event.getHook().sendMessage(msg).queue();
            }
        };

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
                                String correlationId = String.valueOf(UUID.randomUUID());
                                replyRegistry.register(correlationId, event.getHook());
                                statsReadyPublisher.onStatsReady(statsOpt.get(), correlationId);

                                event.getHook()
                                        .sendMessageEmbeds(formatter.format(statsOpt.get()))
                                        .queue();
                            });
                })
                .exceptionally(ex -> {
                    log.error("Command failed", ex);
                    replyOnce.accept("❌ Ошибка обработки");
                    return null;
                });
    }

    private Optional<RedSecStats> fetchStatsForSelected(String platform, TrackerSearchResultApiDto p) {
        return statsHistoryService.getRedSecStats(new GameIdentity(p.platformSlug(), p.platformUserIdentifier()))
                .map(StatsWithSource::stats);
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
