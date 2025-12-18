package com.mishkin.redsecbot.enricherApi.in;

/**
 * Событие, получаемое из N-сервиса
 * @author a.mishkin
 */
public record StatsEnrichedEvent(
        String correlationId,
        String text
) {}
