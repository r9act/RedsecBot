package com.mishkin.redsecbot.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Clock;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author a.mishkin
 */
@Configuration
public class BeanConfiguration {
    @Bean
    @Qualifier("trackerGGWebClient")
    public WebClient trackerGGWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.tracker.gg")
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.USER_AGENT, "BF6-Discord-Bot/1.0")
                .exchangeStrategies(
                        ExchangeStrategies.builder()
                                .codecs(configurer ->
                                        configurer.defaultCodecs()
                                                .maxInMemorySize(2 * 1024 * 1024) // 2 MB
                                )
                                .build()
                )
                .build();
    }

    @Bean
    public ExecutorService commandExecutor() {
        return Executors.newFixedThreadPool(8);
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
