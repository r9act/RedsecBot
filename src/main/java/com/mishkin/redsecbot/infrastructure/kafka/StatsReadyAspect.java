package com.mishkin.redsecbot.infrastructure.kafka;

import com.mishkin.redsecbot.domain.model.RedSecStats;
import com.mishkin.redsecbot.messaging.event.envelope.EventEnvelope;
import com.mishkin.redsecbot.messaging.event.outbound.StatsReadyV1;
import com.mishkin.redsecbot.messaging.mapper.StatsReadyV2Mapper;
import com.mishkin.redsecbot.application.enrichment.StatsEnrichmentFilter;
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

    private final KafkaTemplate<String, EventEnvelope<?>> kafkaTemplate;
    private final StatsEnrichmentFilter filter;
    private final StatsReadyV2Mapper mapper;

    public StatsReadyAspect(KafkaTemplate<String, EventEnvelope<?>> kafkaTemplate, StatsEnrichmentFilter filter, StatsReadyV2Mapper mapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.filter = filter;
        this.mapper = mapper;
    }

    @After("@annotation(com.mishkin.redsecbot.application.event.StatsReady) && args(stats, correlationId)")
    public void afterStatsReady(RedSecStats stats, String correlationId) {

        String userName = stats.playerIdentity().platformUserHandle();

        if (!filter.isEnabled(userName)) {
            return;
        }

        StatsReadyV1 event = new StatsReadyV1(
                correlationId,
                userName,
                Instant.now()
        );

        EventEnvelope<StatsReadyV1> envelope = new EventEnvelope<>(
                "stats.ready",
                1,
                event
        );

//        StatsReadyV2 payload = mapper.map(stats, correlationId);

//        EventEnvelope<StatsReadyV2> envelope =
//                new EventEnvelope<>(
//                        "stats.ready",
//                        2,
//                        payload
//                );
        kafkaTemplate.send("stats.ready", userName, envelope);
    }
}

