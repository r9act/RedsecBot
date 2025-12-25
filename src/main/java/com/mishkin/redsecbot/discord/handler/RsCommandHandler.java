package com.mishkin.redsecbot.discord.handler;

import com.mishkin.redsecbot.application.event.StatsReadyPublisher;
import com.mishkin.redsecbot.application.facade.RedSecStatsFacade;
import com.mishkin.redsecbot.application.service.UserMappingService;
import com.mishkin.redsecbot.discord.formatter.RedSecDiscordFormatter;
import com.mishkin.redsecbot.discord.reply.DiscordReplyRegistry;
import com.mishkin.redsecbot.discord.utils.ExceptionUtils;
import com.mishkin.redsecbot.domain.model.GameIdentity;
import com.mishkin.redsecbot.domain.model.RedSecStats;
import com.mishkin.redsecbot.domain.model.StatsSource;
import com.mishkin.redsecbot.domain.model.StatsWithSource;
import com.mishkin.redsecbot.infrastructure.cassandra.StatsInteractionRow;
import com.mishkin.redsecbot.infrastructure.cassandra.repo.StatsInteractionRepository;
import com.mishkin.redsecbot.infrastructure.postgres.entity.UserMappingEntity;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
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
    private final StatsReadyPublisher statsReadyPublisher;
    private final DiscordReplyRegistry replyRegistry;
    private final StatsInteractionRepository interactionRepository;

    public RsCommandHandler(RedSecStatsFacade statsFacade, UserMappingService userMappingService,
                            RedSecDiscordFormatter formatter, Executor executor, StatsReadyPublisher statsReadyPublisher, DiscordReplyRegistry replyRegistry, StatsInteractionRepository interactionRepository) {
        this.statsFacade = statsFacade;
        this.userMappingService = userMappingService;
        this.formatter = formatter;
        this.executor = executor;
        this.statsReadyPublisher = statsReadyPublisher;
        this.replyRegistry = replyRegistry;
        this.interactionRepository = interactionRepository;
    }

    public void handle(SlashCommandInteractionEvent event) {

        event.deferReply().queue(); // снимаем 3с лимит Discorda - потом через хук отправляем
        String correlationId = UUID.randomUUID().toString();
        long discordId = event.getUser().getIdLong();
        //Освобождаем WebSocket
        CompletableFuture
                .supplyAsync(() -> loadStatsForDiscordUser(discordId), executor)
                .thenAccept(resultOpt -> {

                    if (resultOpt.isEmpty()) {
                        event.getHook()
                                .sendMessage("❌ Ты ещё не играл в REDSEC")
                                .setEphemeral(true)
                                .queue();
                        return;
                    }

                    StatsWithSource result = resultOpt.get();
                    RedSecStats stats = result.stats();
                    StatsSource source = result.source();

                    GameIdentity gameIdentity = new GameIdentity(stats.playerIdentity().platformSlug(),
                            stats.playerIdentity().platformUserIdentifier());

                    interactionRepository.save(
                            new StatsInteractionRow(
                                    UUID.fromString(correlationId),
                                    String.valueOf(discordId),
                                    gameIdentity.toPlayerKey(),
                                    stats.playerIdentity().platformUserHandle(),
                                    "REDSEC",
                                    stats.fetchedAt(),
                                    source,
                                    Instant.now()
                            )
                    );

                    replyRegistry.register(correlationId, event.getHook());

                    statsReadyPublisher.onStatsReady(stats, correlationId);

                    event.getHook()
                            .sendMessageEmbeds(formatter.format(stats))
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

    private Optional<StatsWithSource> loadStatsForDiscordUser(long discordId) {

        UserMappingEntity mapping = userMappingService.getByDiscordId(discordId);

        return statsFacade.getForPlayer(new GameIdentity(mapping.getPlatformSlug(), mapping.getPlatformUserIdentifier()));
    }

}

