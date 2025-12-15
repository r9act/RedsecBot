package com.mishkin.redsecbot.infrastructure.tracker.dto.in.stats;

import java.util.Map;

/**
 * @author a.mishkin
 */
public record TrackerStatApiDto(
        String displayName,
        String displayCategory,
        String category,
        Map<String, Object> metadata,
        Double value,
        String displayValue,
        String displayType
) {}
