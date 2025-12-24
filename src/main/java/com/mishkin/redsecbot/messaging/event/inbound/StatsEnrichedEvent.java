package com.mishkin.redsecbot.messaging.event.inbound;

/**
 * Событие, получаемое из N-сервиса
 * @author a.mishkin
 */
public record StatsEnrichedEvent(
        String correlationId,
        String text
) {}
