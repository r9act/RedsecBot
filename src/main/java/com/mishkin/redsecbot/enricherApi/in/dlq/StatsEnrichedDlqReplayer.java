package com.mishkin.redsecbot.enricherApi.in.dlq;

import com.mishkin.redsecbot.enricherApi.in.StatsEnrichedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author a.mishkin
 */
@Component
public class StatsEnrichedDlqReplayer {

    private final KafkaTemplate<String, StatsEnrichedEvent> kafka;

    public StatsEnrichedDlqReplayer(KafkaTemplate<String, StatsEnrichedEvent> kafka) {
        this.kafka = kafka;
    }

    public void replay(StatsEnrichedEvent event) {
        kafka.send("stats.enriched", event.correlationId(), event);
    }
}

