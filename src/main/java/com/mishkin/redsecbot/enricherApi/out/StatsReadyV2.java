package com.mishkin.redsecbot.enricherApi.out;

import com.mishkin.redsecbot.enricherApi.out.v2subDTOs.CareerRankDto;
import com.mishkin.redsecbot.enricherApi.out.v2subDTOs.PlayerIdentityDto;
import com.mishkin.redsecbot.enricherApi.out.v2subDTOs.RedSecModeStatsDto;

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

