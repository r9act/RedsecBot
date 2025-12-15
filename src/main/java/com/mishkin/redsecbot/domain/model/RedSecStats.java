package com.mishkin.redsecbot.domain.model;

import java.time.Instant;
import java.util.Map;

/**
 * @author a.mishkin
 */
public record RedSecStats(
        PlayerIdentity playerIdentity,
        CareerRank careerRank,
        RedSecModeStats total,
        Map<RedSecMode, RedSecModeStats> modes,
        Instant fetchedAt,
        String source

) {}
