package com.mishkin.redsecbot.enricherApi.out;

import com.mishkin.redsecbot.domain.model.RedSecStats;
import com.mishkin.redsecbot.enricherApi.mapper.StatsReadyV2Mapper;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author a.mishkin
 */
@Aspect
@Component
public class StatsReadyAspect {

    private final KafkaTemplate<String, EventEnvelope<StatsReadyV2>> kafkaTemplate;
    private final StatsEnrichmentFilter filter;
    private final StatsReadyV2Mapper mapper;

    public StatsReadyAspect(KafkaTemplate<String, EventEnvelope<StatsReadyV2>> kafkaTemplate, StatsEnrichmentFilter filter, StatsReadyV2Mapper mapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.filter = filter;
        this.mapper = mapper;
    }

    @After("@annotation(com.mishkin.redsecbot.enricherApi.out.StatsReady) && args(stats, correlationId)")
    public void afterStatsReady(RedSecStats stats, String correlationId) {

        String handle = stats.playerIdentity().platformUserHandle();

        if (!filter.isEnabled(handle)) {
            return;
        }

//        StatsReadyV1 event = new StatsReadyV1(
//                correlationId,
//                handle,
//                Instant.now()
//        );

        StatsReadyV2 payload = mapper.map(stats, correlationId);

        EventEnvelope<StatsReadyV2> event =
                new EventEnvelope<>(
                        "stats.ready",
                        2,
                        payload
                );
        kafkaTemplate.send("stats.ready", handle, event);
    }
}

