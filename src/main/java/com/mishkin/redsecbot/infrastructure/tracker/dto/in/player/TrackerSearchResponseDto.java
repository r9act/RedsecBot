package com.mishkin.redsecbot.infrastructure.tracker.dto.in.player;

import java.util.List;

/**
 * @author a.mishkin
 */
public record TrackerSearchResponseDto(
        List<TrackerSearchResultApiDto> data
) {}
