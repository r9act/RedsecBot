package com.mishkin.redsecbot.infrastructure.tracker.dto.in.stats;

import java.util.Map;

/**
 * @author a.mishkin
 */
public record TrackerSegmentApiDto(
        String type,
        TrackerSegmentAttributes attributes,
        TrackerSegmentMetadata metadata,
        Map<String, TrackerStatApiDto> stats
) {}
