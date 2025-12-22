package com.mishkin.redsecbot.enricherApi.out;

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
