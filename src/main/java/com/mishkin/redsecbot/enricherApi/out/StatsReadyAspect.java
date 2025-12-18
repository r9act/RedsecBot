package com.mishkin.redsecbot.enricherApi.out;

import com.mishkin.redsecbot.domain.model.RedSecStats;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * @author a.mishkin
 */
@Aspect
@Component
public class StatsReadyAspect {

    private final KafkaTemplate<String, StatsReadyEvent> kafkaTemplate;
    private final StatsEnrichmentFilter filter;

    public StatsReadyAspect(
            KafkaTemplate<String, StatsReadyEvent> kafkaTemplate,
            StatsEnrichmentFilter filter
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.filter = filter;
    }

    @After("@annotation(com.mishkin.redsecbot.enricherApi.out.StatsReady) && args(stats, correlationId)")
    public void afterStatsReady(RedSecStats stats, String correlationId) {

        String handle = stats.playerIdentity().platformUserHandle();

        if (!filter.isEnabled(handle)) {
            return;
        }

        StatsReadyEvent event = new StatsReadyEvent(
                correlationId,
                handle,
                Instant.now()
        );

        kafkaTemplate.send("stats.ready", handle, event);
    }
}

