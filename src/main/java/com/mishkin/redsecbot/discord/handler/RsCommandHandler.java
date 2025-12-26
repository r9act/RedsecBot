package com.mishkin.redsecbot.discord.handler;

import com.mishkin.redsecbot.application.service.UserMappingService;
import com.mishkin.redsecbot.discord.formatter.RedSecDiscordFormatter;
import com.mishkin.redsecbot.discord.reply.DiscordReplyRegistry;
import com.mishkin.redsecbot.discord.utils.ExceptionUtils;
import com.mishkin.redsecbot.domain.model.GameIdentity;
import com.mishkin.redsecbot.domain.model.StatsInteractionResult;
import com.mishkin.redsecbot.domain.model.StatsWithSource;
import com.mishkin.redsecbot.infrastructure.postgres.entity.UserMappingEntity;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * @author a.mishkin
 */
@Component
public class RsCommandHandler {

    private final StatsInteractionUseCase interactionUseCase;
    private final UserMappingService userMappingService;
    private final RedSecDiscordFormatter formatter;
    private final DiscordReplyRegistry replyRegistry;

    @Qualifier("discordCommandExecutor")
    private final Executor executor;

    public RsCommandHandler(StatsInteractionUseCase interactionUseCase, UserMappingService userMappingService,
                            RedSecDiscordFormatter formatter, DiscordReplyRegistry replyRegistry, Executor executor) {
        this.interactionUseCase = interactionUseCase;
        this.userMappingService = userMappingService;
        this.formatter = formatter;
        this.replyRegistry = replyRegistry;
        this.executor = executor;
    }

    public void handle(SlashCommandInteractionEvent event) {

        event.deferReply().queue(); // снимаем 3с лимит Discorda - потом через хук отправляем
        long discordId = event.getUser().getIdLong();
        //Освобождаем WebSocket
        CompletableFuture
                .supplyAsync(() -> {

                    UserMappingEntity mapping = userMappingService.getByDiscordId(discordId);
                    GameIdentity gameIdentity = new GameIdentity(mapping.getPlatformSlug(), mapping.getPlatformUserIdentifier());
                    return interactionUseCase.handleRedsecStatsInteraction(discordId, gameIdentity);

                }, executor)
                .thenAccept(resultOpt -> {

                    if (resultOpt.isEmpty()) {
                        event.getHook()
                                .sendMessage("❌ Ты ещё не играл в REDSEC")
                                .setEphemeral(true)
                                .queue();
                        return;
                    }
                    StatsInteractionResult interactionResult = resultOpt.get();
                    StatsWithSource result = interactionResult.stats();

                    replyRegistry.register(interactionResult.correlationId().toString(), event.getHook());

                    // UI-ответ сразу
                    event.getHook()
                            .sendMessageEmbeds(
                                    formatter.format(result.stats()))
                            .queue();
                })
                .exceptionally(ex -> {
                    event.getHook()
                            .sendMessage(
                                    "❌ Ошибка загрузки REDSEC статистики:\n" +
                                            ExceptionUtils.safeMessage(ex))
                            .queue();
                    return null;
                });
    }
}


