package com.mishkin.redsecbot.messaging.event.outbound;

import java.time.Instant;

/**
 * Событие для отправки в N-сервис
 * @author a.mishkin
 */
public record StatsReadyV1(
        String correlationId,
        String platformUserHandle,
        Instant occurredAt
) {}
