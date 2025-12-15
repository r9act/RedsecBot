package com.mishkin.redsecbot.infrastructure.tracker.dto.in.stats;

/**
 * @author a.mishkin
 */
public record TrackerSegmentMetadata(
        String name,
        String category,
        String categoryName
) {}
