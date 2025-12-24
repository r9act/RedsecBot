package com.mishkin.redsecbot.messaging.event.outbound;

import com.mishkin.redsecbot.messaging.event.projection.v2.CareerRankDto;
import com.mishkin.redsecbot.messaging.event.projection.v2.PlayerIdentityDto;
import com.mishkin.redsecbot.messaging.event.projection.v2.RedSecModeStatsDto;

import java.time.Instant;
import java.util.Map;

/**
 * @author a.mishkin
 */
public record StatsReadyV2(
        String correlationId,

        PlayerIdentityDto player,

        CareerRankDto careerRank,

        RedSecModeStatsDto total,

        Map<String, RedSecModeStatsDto> modes,

        Instant fetchedAt,

        String source
) {}

