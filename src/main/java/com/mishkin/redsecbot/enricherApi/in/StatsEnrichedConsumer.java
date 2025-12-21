package com.mishkin.redsecbot.enricherApi.in;

import com.mishkin.redsecbot.enricherApi.idempotancy.DedupStore;
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

    public StatsEnrichedConsumer(DiscordReplyRegistry registry, DedupStore dedupStore) {
        this.registry = registry;
        this.dedupStore = dedupStore;
    }

    @KafkaListener(topics = "stats.enriched", groupId = "redsec-bot-enriched")
    public void consume(StatsEnrichedEvent event) {
        if (!dedupStore.claim(event.correlationId())) {
            return;
        }

        var hookOpt = registry.take(event.correlationId());

        hookOpt.ifPresent(hook ->
                hook.sendMessage(event.text())
                        .queue(
                                success -> log.info("Discord message sent"),
                                error -> log.error("Discord send failed", error)
                        )
        );

        registry.take(event.correlationId())
                .ifPresent(hook -> hook.sendMessage(event.text()).queue());
    }
}

