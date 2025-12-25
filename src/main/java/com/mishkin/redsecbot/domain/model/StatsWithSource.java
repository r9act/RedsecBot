package com.mishkin.redsecbot.domain.model;

/**
 * @author a.mishkin
 */
public record StatsWithSource(
        RedSecStats stats,
        StatsSource source
) {}

