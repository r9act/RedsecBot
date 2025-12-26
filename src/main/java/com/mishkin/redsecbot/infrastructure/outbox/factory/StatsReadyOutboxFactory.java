package com.mishkin.redsecbot.infrastructure.outbox.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mishkin.redsecbot.domain.model.RedSecStats;
import com.mishkin.redsecbot.infrastructure.outbox.event.OutboxEvent;
import com.mishkin.redsecbot.infrastructure.outbox.event.OutboxStatus;
import com.mishkin.redsecbot.messaging.event.envelope.EventEnvelope;
import com.mishkin.redsecbot.messaging.event.outbound.StatsReadyV1;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

/**
 * Фабрика делает OutboxEvent из RedSecStats
 * @author a.mishkin
 */
@Component
public class StatsReadyOutboxFactory {

    private final ObjectMapper objectMapper;

    public StatsReadyOutboxFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public OutboxEvent create(RedSecStats stats, UUID correlationId) {

        String userHandle = stats.playerIdentity().platformUserHandle();

        StatsReadyV1 event = new StatsReadyV1(
                correlationId.toString(),
                userHandle,
                stats.fetchedAt()
        );

        EventEnvelope<StatsReadyV1> envelope =
                new EventEnvelope<>(
                        "stats.ready",
                        1,
                        event
                );

        return new OutboxEvent(
                UUID.randomUUID(),
                "StatsInteraction",
                correlationId,
                "stats.ready",
                userHandle,
                1,
                serialize(envelope),
                OutboxStatus.NEW,
                Instant.now(),
                null
        );
    }

    private String serialize(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Failed to serialize outbox payload", e);
        }
    }
}

