package com.mishkin.redsecbot.domain.extractor;

import com.mishkin.redsecbot.domain.model.RedSecStats;
import com.mishkin.redsecbot.infrastructure.tracker.dto.in.stats.TrackerProfileResponseDto;

import java.util.Optional;

/**
 * @author a.mishkin
 */
public interface RedSecExtractor {

    Optional<RedSecStats> extract(TrackerProfileResponseDto profile);
}