package com.mishkin.redsecbot.messaging.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mishkin.redsecbot.messaging.event.inbound.StatsEnrichedEvent;
import com.mishkin.redsecbot.messaging.idempotancy.DedupStore;
import com.mishkin.redsecbot.messaging.event.envelope.EventEnvelope;
import com.mishkin.redsecbot.discord.reply.DiscordReplyRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author a.mishkin
 */
@Component
public class StatsEnrichedConsumer {
    private static final Logger log = LoggerFactory.getLogger(StatsEnrichedConsumer.class);
    private final DiscordReplyRegistry registry;
    private final DedupStore dedupStore;
    private final ObjectMapper objectMapper;

    public StatsEnrichedConsumer(DiscordReplyRegistry registry, DedupStore dedupStore, ObjectMapper objectMapper) {
        this.registry = registry;
        this.dedupStore = dedupStore;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "stats.enriched", groupId = "redsec-bot-enriched",
            containerFactory = "mainKafkaListenerContainerFactory")
    public void consume(EventEnvelope<StatsEnrichedEvent> envelope) {
        if (envelope.version() != 1) {
            log.warn("Unsupported stats.enriched version {}", envelope.version());
            return;
        }
        //для десериализации нужен явный objectMapper, т.к. spring.json.value.default.type примет payload за LinkedHashMap
        StatsEnrichedEvent event = objectMapper.convertValue(envelope.payload(),StatsEnrichedEvent.class);

        if (!dedupStore.claim(event.correlationId())) {
            return;
        }

        var hookOpt = registry.take(event.correlationId());
        if (hookOpt.isEmpty()) {
            return;
        }

        try {
            hookOpt.get()
                    .sendMessage(event.text())
                    .complete(); // БЛОКИРУЕМСЯ осознанно (вместо асинхронного .queue()) -> ловим exception
        } catch (Exception e) {
            log.error("Discord send failed", e);
            throw e; // даём Kafka увидеть ошибку
        }
    }
}

