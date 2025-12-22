package com.mishkin.redsecbot.enricherApi.in.dlq;

import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.util.backoff.FixedBackOff;

/**
 * @author a.mishkin
 */
@Configuration
public class KafkaErrorHandlingConfig {

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> kafkaTemplate) {

        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(kafkaTemplate,
                        (record, ex) ->
                                new TopicPartition(
                                        "stats.enriched.dlq",
                                        record.partition()
                                )
                );

        // retry 3 раза с паузой
        FixedBackOff backOff = new FixedBackOff(1000L, 3);

        DefaultErrorHandler handler =
                new DefaultErrorHandler(recoverer, backOff);

        // НЕ ретраим
        handler.addNotRetryableExceptions(IllegalArgumentException.class, DeserializationException.class);

        return handler;
    }
}

