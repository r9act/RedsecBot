package com.mishkin.redsecbot.enricherApi.out.v2subDTOs;

import java.time.Duration;

/**
 * @author a.mishkin
 */
public record RedSecModeStatsDto(
        int matchesPlayed,
        int matchesWon,
        double winRate,
        int kills,
        int deaths,
        double kd,
        Duration timePlayed
) {}
