package com.mishkin.redsecbot.discord.handler;

import com.mishkin.redsecbot.application.service.UserMappingService;
import com.mishkin.redsecbot.discord.formatter.RedSecDiscordFormatter;
import com.mishkin.redsecbot.application.facade.RedSecStatsFacade;
import com.mishkin.redsecbot.discord.utils.ExceptionUtils;
import com.mishkin.redsecbot.domain.model.RedSecStats;
import com.mishkin.redsecbot.infrastructure.postgres.entity.UserMappingEntity;
import com.mishkin.redsecbot.infrastructure.tracker.client.TrackerGGPlayerSearchClient;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * @author a.mishkin
 */
@Component
public class RsCommandHandler {

    private final RedSecStatsFacade statsFacade;
    private final UserMappingService userMappingService;
    private final RedSecDiscordFormatter formatter;
    @Qualifier("discordCommandExecutor")
    private final Executor executor;

    public RsCommandHandler(RedSecStatsFacade statsFacade, UserMappingService userMappingService,
                            RedSecDiscordFormatter formatter, Executor executor) {
        this.statsFacade = statsFacade;
        this.userMappingService = userMappingService;
        this.formatter = formatter;
        this.executor = executor;
    }

    public void handle(SlashCommandInteractionEvent event) {

        event.deferReply().queue(); // снимаем 3с лимит Discorda - потом через хук отправляем

        long discordId = event.getUser().getIdLong();
        //Освобождаем WebSocket
        CompletableFuture
                .supplyAsync(() -> loadStatsForDiscordUser(discordId), executor)
                .thenAccept(statsOpt -> {

                    if (statsOpt.isEmpty()) {
                        event.getHook()
                                .sendMessage("❌ Ты ещё не играл в REDSEC")
                                .setEphemeral(true)
                                .queue();
                        return;
                    }

                    event.getHook()
                            .sendMessageEmbeds(
                                    formatter.format(statsOpt.get())
                            )
                            .queue();
                })
                .exceptionally(ex -> {
                    event.getHook()
                            .sendMessage(
                                    "❌ Ошибка загрузки REDSEC статистики:\n" +
                                            ExceptionUtils.safeMessage(ex)
                            )
                            .queue();
                    return null;
                });
    }

    private Optional<RedSecStats> loadStatsForDiscordUser(long discordId) {

        UserMappingEntity mapping = userMappingService.getByDiscordId(discordId);

        String playerKey = "discord:" + discordId;

        return statsFacade.getForPlayer(playerKey, mapping.getPlatformSlug(), mapping.getPlatformUserIdentifier());
    }

}

