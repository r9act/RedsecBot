package com.mishkin.redsecbot.messaging.event.projection.v2;

/**
 * @author a.mishkin
 */
public record CareerRankDto(
        int rank,
        String name,
        String imageUrl
) {}

