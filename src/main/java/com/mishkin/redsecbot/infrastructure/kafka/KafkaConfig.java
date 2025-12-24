package com.mishkin.redsecbot.infrastructure.kafka;

import com.mishkin.redsecbot.messaging.event.inbound.StatsEnrichedEvent;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

/**
 * @author a.mishkin
 */
@Configuration
public class KafkaConfig {
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, StatsEnrichedEvent> mainKafkaListenerContainerFactory(
            ConsumerFactory<String, StatsEnrichedEvent> cf, KafkaTemplate<String, StatsEnrichedEvent> kafkaTemplate) {
        var recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, ex) ->
                        new TopicPartition("stats.enriched.dlq", record.partition())
        );

        var errorHandler = new DefaultErrorHandler(
                recoverer,
                new FixedBackOff(1000L, 3)
        );

        var factory = new ConcurrentKafkaListenerContainerFactory<String, StatsEnrichedEvent>();
        factory.setConsumerFactory(cf);
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, StatsEnrichedEvent> dlqKafkaListenerContainerFactory(
            ConsumerFactory<String, StatsEnrichedEvent> cf) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, StatsEnrichedEvent>();
        factory.setConsumerFactory(cf);

        // ВАЖНО: no DeadLetterPublishingRecoverer here
        factory.setCommonErrorHandler(new DefaultErrorHandler(
                new FixedBackOff(0L, 0L)
        ));

        return factory;
    }
}
