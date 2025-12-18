package com.mishkin.redsecbot.enricherApi.out;

import com.mishkin.redsecbot.domain.model.RedSecStats;
import org.springframework.stereotype.Component;

/**
 * @author a.mishkin
 */
@Component
public class StatsPipeline {
    @StatsReady
    public void onStatsReady(RedSecStats stats, String correlationId) {
    }
}
