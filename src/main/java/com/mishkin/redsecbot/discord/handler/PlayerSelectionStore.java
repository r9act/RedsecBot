package com.mishkin.redsecbot.discord.handler;

import com.mishkin.redsecbot.infrastructure.tracker.dto.in.player.TrackerSearchResultApiDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author a.mishkin
 */
@Component
public class PlayerSelectionStore {

    private final Map<Long, List<TrackerSearchResultApiDto>> store =
            new ConcurrentHashMap<>();

    // сохраняем варианты после /check
    public void put(long discordId, List<TrackerSearchResultApiDto> players) {
        store.put(discordId, players);
    }

    // достаём конкретно выбранного игрока
    public Optional<TrackerSearchResultApiDto> getSelected(long discordId, String platformUserIdentifier) {
        return Optional.ofNullable(store.get(discordId))
                .flatMap(list -> list.stream()
                        .filter(p -> p.platformUserIdentifier()
                                .equals(platformUserIdentifier))
                        .findFirst());
    }

    // чистим после выбора
    public void clear(long discordId) {
        store.remove(discordId);
    }
}

