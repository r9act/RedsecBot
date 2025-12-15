package com.mishkin.redsecbot.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Clock;

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

    /**
     * Bug 1 DevTools вызывает executor.shutdown(), но НО старые listener’ы / JDA callbacks ещё живы
     * они пытаются отправить задачу в СТАРЫЙ executor
     * нужен не ExecutorService, а ThreadPoolTaskExecutor, т.к.
     * Spring НЕ закрывает его неожиданно
     * корректно пересоздаётся
     * интегрирован с lifecycle
     * переживает devtools
     * @return
     */
    @Bean
    public ThreadPoolTaskExecutor discordCommandExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("command-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(5);
        executor.initialize();
        return executor;
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
