package com.mishkin.redsecbot.enricherApi.in;

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

    public StatsEnrichedConsumer(DiscordReplyRegistry registry) {
        this.registry = registry;
    }

    @KafkaListener(topics = "stats.enriched", groupId = "redsec-bot-enriched")
    public void consume(StatsEnrichedEvent event) {
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

