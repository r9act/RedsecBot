package com.mishkin.redsecbot.enricherApi.out;

/**
 * @author a.mishkin
 */
public record EventEnvelope<T>(
        String type,
        int version,
        T payload
) {}

