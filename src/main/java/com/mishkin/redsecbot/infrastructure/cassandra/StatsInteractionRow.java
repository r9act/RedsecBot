package com.mishkin.redsecbot.infrastructure.cassandra;

import com.mishkin.redsecbot.domain.model.StatsSource;

import java.time.Instant;
import java.util.UUID;

/**
 * @author a.mishkin
 */
public record StatsInteractionRow(
        UUID correlationId,
        String requesterDiscordId,
        String playerKey,
        String platformUserHandle,
        String statsType,
        Instant fetchedAt,
        StatsSource source,
        Instant createdAt
) {}
