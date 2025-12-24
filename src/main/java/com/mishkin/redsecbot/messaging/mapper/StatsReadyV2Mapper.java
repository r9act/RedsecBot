package com.mishkin.redsecbot.messaging.mapper;

import com.mishkin.redsecbot.domain.model.*;
import com.mishkin.redsecbot.messaging.event.outbound.StatsReadyV2;
import com.mishkin.redsecbot.messaging.event.projection.v2.CareerRankDto;
import com.mishkin.redsecbot.messaging.event.projection.v2.PlayerIdentityDto;
import com.mishkin.redsecbot.messaging.event.projection.v2.RedSecModeStatsDto;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author a.mishkin
 */
@Component
public class StatsReadyV2Mapper {

    public StatsReadyV2 map(RedSecStats stats, String correlationId) {

        return new StatsReadyV2(
                correlationId,
                mapPlayer(stats.playerIdentity()),
                mapCareer(stats.careerRank()),
                mapModeStats(stats.total()),
                mapModes(stats.modes()),
                stats.fetchedAt(),
                stats.source()
        );
    }

    private PlayerIdentityDto mapPlayer(PlayerIdentity p) {
        return new PlayerIdentityDto(
                p.platformSlug(),
                p.platformUserHandle()
        );
    }

    private CareerRankDto mapCareer(CareerRank r) {
        return new CareerRankDto(
                r.value(),
                r.rankName(),
                r.imageUrl()
        );
    }

    private RedSecModeStatsDto mapModeStats(RedSecModeStats s) {
        return new RedSecModeStatsDto(
                s.matchesPlayed(),
                s.matchesWon(),
                s.winRate(),
                s.kills(),
                s.deaths(),
                s.kd(),
                s.timePlayed()
        );
    }

    private Map<String, RedSecModeStatsDto> mapModes(
            Map<RedSecMode, RedSecModeStats> modes
    ) {
        return modes.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().name(),
                        e -> mapModeStats(e.getValue())
                ));
    }
}

