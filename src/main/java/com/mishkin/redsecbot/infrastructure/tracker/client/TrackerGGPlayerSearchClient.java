package com.mishkin.redsecbot.infrastructure.tracker.client;

import com.mishkin.redsecbot.infrastructure.tracker.dto.in.player.TrackerSearchResponseDto;
import com.mishkin.redsecbot.infrastructure.tracker.dto.in.player.TrackerSearchResultApiDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Ищет игрока по платформе и нику
 * @author a.mishkin
 */
@Component
public class TrackerGGPlayerSearchClient {
    private static final Logger log = LoggerFactory.getLogger(TrackerGGPlayerSearchClient.class);

    private final WebClient webClient;

    public TrackerGGPlayerSearchClient(@Qualifier("trackerGGWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public List<TrackerSearchResultApiDto> searchPlayers(String platform, String userName) {

        String finalUri = "/api/v2/bf6/standard/search" +
                "?platform=" + platform +
                "&query=" + userName;

        log.info("Sending request to TrackerGG API: {}", finalUri);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v2/bf6/standard/search")
                        .queryParam("platform", platform)
                        .queryParam("query", userName)
                        .build())
                .retrieve()
                .onStatus(
                        status -> status.value() == 403,
                        response -> Mono.empty()
                )
                .bodyToMono(TrackerSearchResponseDto.class)
                .map(response -> {
                    if (response == null || response.data() == null) {
                        return List.<TrackerSearchResultApiDto>of();
                    }
                    return response.data();
                })
                .onErrorReturn(List.of()) // на всякий случай
                .block();
    }
}


