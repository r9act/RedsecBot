package com.mishkin.redsecbot.discord.handler;

import com.mishkin.redsecbot.application.enrichment.StatsEnrichmentFilter;
import com.mishkin.redsecbot.application.event.DiscordInteraction;
import com.mishkin.redsecbot.application.service.PlayerStatsHistoryService;
import com.mishkin.redsecbot.domain.model.GameIdentity;
import com.mishkin.redsecbot.domain.model.RedSecStats;
import com.mishkin.redsecbot.domain.model.StatsInteractionResult;
import com.mishkin.redsecbot.domain.model.StatsWithSource;
import com.mishkin.redsecbot.infrastructure.cassandra.StatsInteractionRow;
import com.mishkin.redsecbot.infrastructure.cassandra.repo.StatsInteractionRepository;
import com.mishkin.redsecbot.infrastructure.outbox.repo.OutboxRepository;
import com.mishkin.redsecbot.infrastructure.outbox.factory.StatsReadyOutboxFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * @author a.mishkin
 */
@Service
public class StatsInteractionUseCase {

    private final PlayerStatsHistoryService statsService;
    private final StatsInteractionRepository interactionRepository;
    private final OutboxRepository outboxRepository;
    private final StatsReadyOutboxFactory outboxFactory;
    private final StatsEnrichmentFilter enrichmentFilter;

    public StatsInteractionUseCase(PlayerStatsHistoryService statsService, StatsInteractionRepository interactionRepository,
                                   OutboxRepository outboxRepository, StatsReadyOutboxFactory outboxFactory, StatsEnrichmentFilter enrichmentFilter) {
        this.statsService = statsService;
        this.interactionRepository = interactionRepository;
        this.outboxRepository = outboxRepository;
        this.outboxFactory = outboxFactory;
        this.enrichmentFilter = enrichmentFilter;
    }

    /**
     * entrypoint для всех Discord-команд,
     * которые запрашивают стату
     */
    @DiscordInteraction(command = "REDSEC")
    @Transactional
    public Optional<StatsInteractionResult> handleRedsecStatsInteraction(long requesterDiscordId, GameIdentity gameIdentity) {

        // получение самой статы по platform+userId (бизнес-функционал)
        Optional<StatsWithSource> resultOpt = statsService.getRedSecStats(gameIdentity);

        if (resultOpt.isEmpty()) {
            return Optional.empty();
        }

        StatsWithSource result = resultOpt.get();
        RedSecStats stats = result.stats();

        UUID correlationId = UUID.randomUUID();
        Instant now = Instant.now();
        String handle = stats.playerIdentity().platformUserHandle();
        //сохранение discord-взаимодействия
        interactionRepository.save(
                new StatsInteractionRow(
                        correlationId,
                        String.valueOf(requesterDiscordId),
                        gameIdentity.toPlayerKey(),
                        stats.playerIdentity().platformUserHandle(),
                        "REDSEC",
                        stats.fetchedAt(),
                        result.source(),        // CACHE | REFRESH
                        now
                )
        );

        //проверка имени по списку и создание outbox-event для дальнейшего publish
        if (enrichmentFilter.isEnabled(handle)) {
            outboxRepository.save(
                    outboxFactory.create(stats, correlationId)
            );
        }
        return Optional.of(
                new StatsInteractionResult(correlationId, result)
        );
    }
}

