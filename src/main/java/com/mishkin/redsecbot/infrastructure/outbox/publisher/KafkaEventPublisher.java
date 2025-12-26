package com.mishkin.redsecbot.infrastructure.outbox.publisher;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mishkin.redsecbot.infrastructure.outbox.event.OutboxEvent;
import com.mishkin.redsecbot.messaging.event.envelope.EventEnvelope;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author a.mishkin
 */
@Component
public class KafkaEventPublisher implements EventPublisher {

    private final KafkaTemplate<String, EventEnvelope<JsonNode>> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaEventPublisher(KafkaTemplate<String, EventEnvelope<JsonNode>> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(OutboxEvent event) {
        try {
            JsonNode root = objectMapper.readTree(event.payload());

            EventEnvelope<JsonNode> envelope = new EventEnvelope<>(
                    root.get("type").asText(),
                    root.get("version").asInt(),
                    root.get("payload")
            );

            kafkaTemplate.send(event.topic(), event.kafkaKey(), envelope);

        } catch (Exception e) {
            throw new IllegalStateException("Failed to publish outbox event " + event.id(), e);
        }
    }
}

