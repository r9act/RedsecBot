package com.mishkin.redsecbot.infrastructure.tracker.client;

import com.mishkin.redsecbot.infrastructure.tracker.dto.in.stats.TrackerProfileResponseDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author a.mishkin
 */
@Component
public class TrackerGGStatsClient {

    private final WebClient webClient;

    public TrackerGGStatsClient(@Qualifier("trackerGGWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public TrackerProfileResponseDto fetchProfile(String platformSlug, String platformUserIdentifier) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v2/bf6/standard/profile/{platform}/{id}")
                        .build(platformSlug, platformUserIdentifier))
                .retrieve()
                .bodyToMono(TrackerProfileResponseDto.class)
                .block();
    }
}

