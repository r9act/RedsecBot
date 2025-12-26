package com.mishkin.redsecbot.infrastructure.outbox.publisher;

import com.mishkin.redsecbot.infrastructure.outbox.event.OutboxEvent;
import com.mishkin.redsecbot.infrastructure.outbox.repo.OutboxRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

/**
 * @author a.mishkin
 */
@Component
public class OutboxPublisher {

    private static final int BATCH_SIZE = 50;

    private final OutboxRepository outboxRepository;
    private final EventPublisher eventPublisher;

    public OutboxPublisher(OutboxRepository outboxRepository, EventPublisher eventPublisher) {
        this.outboxRepository = outboxRepository;
        this.eventPublisher = eventPublisher;
    }

    @Scheduled(fixedDelayString = "${outbox.publish.delay-ms:1000}")
    public void publishPendingEvents() {

        List<OutboxEvent> events = outboxRepository.findUnpublished(BATCH_SIZE);

        for (OutboxEvent event : events) {
            try {
                eventPublisher.publish(event);
                outboxRepository.markPublished(event.id(), Instant.now());
            } catch (Exception ex) {
                outboxRepository.markFailed(event.id());
            }
        }
    }
}
