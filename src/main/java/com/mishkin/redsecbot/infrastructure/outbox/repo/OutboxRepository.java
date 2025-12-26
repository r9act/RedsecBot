package com.mishkin.redsecbot.infrastructure.outbox.repo;

import com.mishkin.redsecbot.infrastructure.outbox.event.OutboxEvent;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * @author a.mishkin
 */
public interface OutboxRepository {

    void save(OutboxEvent event);

    List<OutboxEvent> findUnpublished(int limit);

    void markPublished(UUID id, Instant publishedAt);

    void markFailed(UUID id);
}

