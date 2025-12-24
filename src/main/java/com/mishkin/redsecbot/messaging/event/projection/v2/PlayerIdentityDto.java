package com.mishkin.redsecbot.messaging.event.projection.v2;

/**
 * @author a.mishkin
 */
public record PlayerIdentityDto(
        String platform,
        String userHandle
) {}

