package com.mishkin.redsecbot.infrastructure.tracker.dto.in.stats;

import java.util.List;

/**
 * @author a.mishkin
 */
public record TrackerProfileDataDto(
        PlatformInfoDto platformInfo,
        UserInfoDto userInfo,
        List<TrackerSegmentApiDto> segments
) {}
