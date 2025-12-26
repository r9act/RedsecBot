package com.mishkin.redsecbot.infrastructure.outbox.event;

import java.time.Instant;
import java.util.UUID;

/**
 * @author a.mishkin
 */
public record OutboxEvent(
        UUID id,
        String aggregateType,
        UUID aggregateId,          // correlationId
        String topic,              // "stats.ready"
        String kafkaKey,           // platformUserHandle
        int eventVersion,
        String payload,            // ! JSONB of EventEnvelope<StatsReadyV1>
        OutboxStatus status,
        Instant createdAt,
        Instant publishedAt
) {}
