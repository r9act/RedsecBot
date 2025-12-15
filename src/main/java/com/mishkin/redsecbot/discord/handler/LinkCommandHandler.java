package com.mishkin.redsecbot.discord.handler;

import com.mishkin.redsecbot.application.service.UserMappingService;
import com.mishkin.redsecbot.infrastructure.tracker.dto.in.player.TrackerSearchResultApiDto;
import com.mishkin.redsecbot.infrastructure.tracker.client.TrackerGGPlayerSearchClient;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * @author a.mishkin
 */
@Component
public class LinkCommandHandler {

    private final TrackerGGPlayerSearchClient searchClient;
    private final UserMappingService userMappingService;
    private final PlayerSelectionStore selectionStore;
    private final Executor executor;

    public LinkCommandHandler(TrackerGGPlayerSearchClient searchClient, UserMappingService userMappingService,
                              PlayerSelectionStore selectionStore, Executor executor) {
        this.searchClient = searchClient;
        this.userMappingService = userMappingService;
        this.selectionStore = selectionStore;
        this.executor = executor;
    }

    public void handle(SlashCommandInteractionEvent event) {

        event.deferReply(true).queue();

        long discordId = event.getUser().getIdLong();
        String bfName = event.getOption("name").getAsString();
        String platform = "origin";

        CompletableFuture.supplyAsync(() -> searchClient.searchPlayers(platform, bfName), executor)
                .thenAccept(players -> {
                    if (players.isEmpty()) {
                        event.getHook()
                                .sendMessage("❌ Игрок не найден: **" + bfName + "**")
                                .queue();
                        return;
                    }
                    // ровно один — сразу link
                    if (players.size() == 1) {
                        TrackerSearchResultApiDto selected = players.get(0);
                        userMappingService.linkSelectedPlayer(discordId, selected);
                        event.getHook()
                                .sendMessage("✅ Профиль привязан:\n" + "**" + selected.platformUserHandle() + "**")
                                .queue();
                        return;
                    }

                    // несколько — Select Menu
                    selectionStore.put(discordId, players);

                    event.getHook()
                            .sendMessage("❌ Найдено несколько профилей. Выбери нужный:")
                            .addActionRow(buildSelectMenu(players))
                            .queue();
                })
                .exceptionally(ex -> {
                    event.getHook()
                            .sendMessage("❌ Не удалось привязать: " + extractMessage(ex))
                            .queue();
                    return null;
                });
    }

    //TODO переделать на autocomplete
    private StringSelectMenu buildSelectMenu(List<TrackerSearchResultApiDto> players) {
        return StringSelectMenu.create("link-select")
                .setPlaceholder("Выбери профиль")
                .addOptions(
                        players.stream()
                                .limit(5)
                                .map(p -> SelectOption.of(
                                        formatLabel(p),
                                        p.platformUserIdentifier()
                                ))
                                .toList()
                )
                .build();
    }

    private String formatLabel(TrackerSearchResultApiDto p) {
        String country = p.metadata() != null && p.metadata().countryCode() != null
                        ? p.metadata().countryCode()
                        : "?";

        return p.platformUserHandle() + " | " + country + " | " + p.status();
    }

    private String extractMessage(Throwable ex) {
        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
        return cause.getMessage() != null
                ? cause.getMessage()
                : "unknown error";
    }
}

