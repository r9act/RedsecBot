package com.mishkin.redsecbot.infrastructure.cassandra;

import java.time.Duration;

/**
 * @author a.mishkin
 */
public final class StatsConstants {

    public static final Duration STATS_TTL = Duration.ofHours(3);

    private StatsConstants() {}
}