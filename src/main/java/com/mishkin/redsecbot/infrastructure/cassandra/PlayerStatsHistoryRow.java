package com.mishkin.redsecbot.infrastructure.cassandra;

import java.time.Instant;

/**
 * @author a.mishkin
 */
public record PlayerStatsHistoryRow(
        String playerKey,
        String statsType,
        Instant fetchedAt,
        String payload,
        String source
) {}
