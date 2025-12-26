package com.mishkin.redsecbot.discord.handler;

import com.mishkin.redsecbot.infrastructure.tracker.client.TrackerGGPlayerSearchClient;
import com.mishkin.redsecbot.infrastructure.tracker.dto.in.player.TrackerSearchResultApiDto;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
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
    private final PlayerSelectionStore selectionStore;
    private final Executor executor;

    public CheckCommandHandler(TrackerGGPlayerSearchClient searchClient, PlayerSelectionStore selectionStore, Executor executor) {
        this.searchClient = searchClient;
        this.selectionStore = selectionStore;
        this.executor = executor;
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

        CompletableFuture.supplyAsync(() -> searchClient.searchPlayers(platform, bfName), executor)
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
                    //TODO если 1 вариант - делаем пока в выпадающем для упрощения, потом будет autocomplete
                    TrackerSearchResultApiDto p = players.get(0);
                    selectionStore.put(discordId, List.of(p));

                    event.getHook()
                            .sendMessage("Найден профиль:")
                            .addActionRow(buildSelectMenu(List.of(p)))
                            .queue();

                    return CompletableFuture.completedFuture(null);

                })
                .exceptionally(ex -> {
                    log.error("/check command failed", ex);
                    replyOnce.accept("❌ Ошибка обработки");
                    return null;
                });
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
