package com.mishkin.redsecbot.messaging.event.projection.v2;

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
