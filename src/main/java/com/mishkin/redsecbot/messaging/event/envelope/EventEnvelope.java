package com.mishkin.redsecbot.messaging.event.envelope;

/**
 * @author a.mishkin
 */
public record EventEnvelope<T>(
        String type,
        int version,
        T payload
) {}

