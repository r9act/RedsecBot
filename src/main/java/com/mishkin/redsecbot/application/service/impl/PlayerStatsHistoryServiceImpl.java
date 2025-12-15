package com.mishkin.redsecbot.application.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mishkin.redsecbot.infrastructure.cassandra.PlayerStatsHistoryRow;
import com.mishkin.redsecbot.infrastructure.cassandra.repo.PlayerStatsHistoryRepository;
import com.mishkin.redsecbot.infrastructure.cassandra.StatsConstants;
import com.mishkin.redsecbot.application.service.PlayerStatsHistoryService;
import com.mishkin.redsecbot.domain.extractor.RedSecExtractor;
import com.mishkin.redsecbot.domain.model.RedSecStats;
import com.mishkin.redsecbot.infrastructure.tracker.dto.in.stats.TrackerProfileResponseDto;
import com.mishkin.redsecbot.infrastructure.tracker.client.TrackerGGStatsClient;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

/**
 * @author a.mishkin
 */
@Service
public class PlayerStatsHistoryServiceImpl implements PlayerStatsHistoryService {

    private static final String STATS_TYPE = "REDSEC";
    private static final String SOURCE = "TRACKER_GG";

    private final PlayerStatsHistoryRepository repository;
    private final TrackerGGStatsClient statsClient;
    private final RedSecExtractor redSecExtractor;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    public PlayerStatsHistoryServiceImpl(PlayerStatsHistoryRepository repository, TrackerGGStatsClient statsClient, RedSecExtractor redSecExtractor, ObjectMapper objectMapper, Clock clock) {
        this.repository = repository;
        this.statsClient = statsClient;
        this.redSecExtractor = redSecExtractor;
        this.objectMapper = objectMapper;
        this.clock = clock;
    }

    /**
     * Чтобы не ддсить tracker ходим туда не чаще, чем раз в 3 часа (на пользователя).
     * Для этого храним в касандре историю все запросов с временной меткой
     * @param playerKey discord:123123123 - для подписанных (/rs), bf:steam:123123123 - для /check команды
     * @param platformSlug для нового запроса по API (steam, origin...)
     * @param platformUserIdentifier id игрока для нового запроса по API
     * @return
     */
    @Override
    public Optional<RedSecStats> getRedSecStats(String playerKey, String platformSlug, String platformUserIdentifier) {
        Instant now = Instant.now(clock);

        return repository.findLatest(playerKey, STATS_TYPE)
                .filter(row -> !isExpired(row.fetchedAt(), now))
                .map(this::deserialize)
                .or(() -> refreshAndStore(playerKey, platformSlug, platformUserIdentifier, now));
    }

    private boolean isExpired(Instant fetchedAt, Instant now) {
        return fetchedAt.isBefore(now.minus(StatsConstants.STATS_TTL));
    }

    private Optional<RedSecStats> refreshAndStore(String playerKey, String platformSlug, String platformUserIdentifier, Instant now) {
        TrackerProfileResponseDto profile = statsClient.fetchProfile(platformSlug, platformUserIdentifier);

        Optional<RedSecStats> statsOpt = redSecExtractor.extract(profile);

        statsOpt.ifPresent(stats -> persist(playerKey, stats, now));
        return statsOpt;
    }

    private void persist(String playerKey, RedSecStats stats, Instant now) {
        try {
            String payload = objectMapper.writeValueAsString(stats);
            repository.save(new PlayerStatsHistoryRow(playerKey, STATS_TYPE, now, payload, SOURCE));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Ошибка сериализации RedSecStats", e);
        }
    }

    private RedSecStats deserialize(PlayerStatsHistoryRow row) {
        try {
            return objectMapper.readValue(row.payload(), RedSecStats.class);
        } catch (IOException e) {
            throw new IllegalStateException("Ошибка десериализации RedSecStats", e);
        }
    }
}

