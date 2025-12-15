package com.mishkin.redsecbot.infrastructure.tracker.client;

import com.mishkin.redsecbot.infrastructure.tracker.exception.TrackerIntegrationException;
import com.mishkin.redsecbot.infrastructure.tracker.exception.TrackerInvalidResponseException;
import com.mishkin.redsecbot.infrastructure.tracker.dto.in.player.TrackerSearchResponseDto;
import com.mishkin.redsecbot.infrastructure.tracker.dto.in.player.TrackerSearchResultApiDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

/**
 * @author a.mishkin
 */
@Component
public class TrackerGGPlayerSearchClient {

    private final WebClient webClient;

    public TrackerGGPlayerSearchClient(@Qualifier("trackerGGWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public List<TrackerSearchResultApiDto> searchPlayers(String platform, String query) {

        try {
            TrackerSearchResponseDto response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v2/bf6/standard/search")
                            .queryParam("platform", platform)
                            .queryParam("query", query)
                            .build())
                    .retrieve()
                    .bodyToMono(TrackerSearchResponseDto.class)
                    .block();

            if (response == null) {
                throw new TrackerInvalidResponseException("TrackerGG вернул пустой body");
            }

            if (response.data() == null) {
                throw new TrackerInvalidResponseException("TrackerGG вернул пустой data");
            }

            return response.data();

        } catch (WebClientResponseException e) {
            throw new TrackerIntegrationException("TrackerGG API error: " + e.getStatusCode(), e);
        }
    }
}


