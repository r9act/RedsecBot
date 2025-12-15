package com.mishkin.redsecbot.domain.model;

import java.time.Duration;

/**
 * @author a.mishkin
 */
public record RedSecModeStats(
        int matchesPlayed,
        int matchesWon,
        double winRate,
        int kills,
        int deaths,
        double kd,
        Duration timePlayed
) {}

