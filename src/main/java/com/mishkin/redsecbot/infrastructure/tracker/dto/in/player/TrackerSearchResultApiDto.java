package com.mishkin.redsecbot.infrastructure.tracker.dto.in.player;

/**
 * @author a.mishkin
 */
public record TrackerSearchResultApiDto(
        Integer platformId,
        String platformSlug,
        String platformUserIdentifier,
        String platformUserId,
        String platformUserHandle,
        String avatarUrl,
        String titleUserId,
        String status,
        Object additionalParameters,
        TrackerSearchMetadataDto metadata
) {}

