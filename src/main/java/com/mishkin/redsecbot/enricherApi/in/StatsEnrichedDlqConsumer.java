package com.mishkin.redsecbot.enricherApi.in;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * @author a.mishkin
 */
@Component
public class StatsEnrichedDlqConsumer {

    private static final Logger log = LoggerFactory.getLogger(StatsEnrichedDlqConsumer.class);

    @KafkaListener(topics = "stats.enriched.dlq", groupId = "redsec-bot-enriched-dlq")
    public void consume(ConsumerRecord<String, StatsEnrichedEvent> record) {

        StatsEnrichedEvent event = record.value();

        log.error("DLQ event received: correlationId={}, reason={}", event.correlationId(), extractError(record.headers()));
    }

    private String extractError(Headers headers) {
        Header h = headers.lastHeader(KafkaHeaders.DLT_EXCEPTION_MESSAGE);
        return h != null ? new String(h.value(), StandardCharsets.UTF_8) : "unknown";
    }
}

