package com.mishkin.redsecbot.infrastructure.outbox.publisher;

import com.mishkin.redsecbot.infrastructure.outbox.event.OutboxEvent;

public interface EventPublisher {
    void publish(OutboxEvent event);
}
