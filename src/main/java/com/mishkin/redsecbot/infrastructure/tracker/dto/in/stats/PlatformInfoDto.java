package com.mishkin.redsecbot.infrastructure.tracker.dto.in.stats;

/**
 * @author a.mishkin
 */
public record PlatformInfoDto(
        String platformSlug,
        String platformUserId,
        String platformUserHandle,
        String platformUserIdentifier,
        String avatarUrl,
        Object additionalParameters
) {}
