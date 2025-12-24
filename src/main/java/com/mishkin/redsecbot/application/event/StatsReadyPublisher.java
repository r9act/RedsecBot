package com.mishkin.redsecbot.application.event;

import com.mishkin.redsecbot.domain.model.RedSecStats;
import org.springframework.stereotype.Component;

/**
 * @author a.mishkin
 */
@Component
public class StatsReadyPublisher {
    @StatsReady
    public void onStatsReady(RedSecStats stats, String correlationId) {
    }
}
