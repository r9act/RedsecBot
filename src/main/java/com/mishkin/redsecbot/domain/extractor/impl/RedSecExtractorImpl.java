package com.mishkin.redsecbot.domain.extractor.impl;

import com.mishkin.redsecbot.domain.extractor.RedSecExtractor;
import com.mishkin.redsecbot.domain.model.*;
import com.mishkin.redsecbot.infrastructure.tracker.dto.in.stats.PlatformInfoDto;
import com.mishkin.redsecbot.infrastructure.tracker.dto.in.stats.TrackerProfileResponseDto;
import com.mishkin.redsecbot.infrastructure.tracker.dto.in.stats.TrackerSegmentApiDto;
import com.mishkin.redsecbot.infrastructure.tracker.dto.in.stats.TrackerStatApiDto;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * @author a.mishkin
 */
@Component
public class RedSecExtractorImpl implements RedSecExtractor {

    private static final String SOURCE = "TRACKER_GG";
    private static final Double AVG_DEATHS_GAUNTLET = 10d;
    private static final Double AVG_DEATHS_DUOS = 2d;
    private static final Double AVG_DEATHS_QUADS = 2.2d;
    private static final Double AVG_DEATHS_TOTAL = 3.1d;

    @Override
    public Optional<RedSecStats> extract(TrackerProfileResponseDto profile) {

        List<TrackerSegmentApiDto> segments = profile.data().segments();

        PlayerIdentity playerIdentity = extractPlayerIdentity(profile.data().platformInfo());
        CareerRank careerRank = extractCareerRank(segments);

        //тотал не просто для суммы всех режимов, в нем есть доп данные для вычисления kd
        Optional<RedSecModeStats> totalOpt =
                extractMode(segments, "gamemode-category", "gm_granite");
        if (totalOpt.isEmpty()) {
            return Optional.empty();
        }

        // Modes
        Map<RedSecMode, RedSecModeStats> modes = new EnumMap<>(RedSecMode.class);

        extractMode(segments, "gamemode", "gm_brsquad")
                .ifPresent(stats -> modes.put(RedSecMode.QUADS, stats));

        extractMode(segments, "gamemode", "gm_graniteDuo")
                .ifPresent(stats -> modes.put(RedSecMode.DUOS, stats));

        extractMode(segments, "gamemode", "gm_gntgauntlet")
                .ifPresent(stats -> modes.put(RedSecMode.GAUNTLET, stats));

        return Optional.of(new RedSecStats(
                playerIdentity,
                careerRank,
                totalOpt.get(),
                modes,
                Instant.now(),
                SOURCE
        ));
    }

    private PlayerIdentity extractPlayerIdentity(PlatformInfoDto platformInfo) {
        return new PlayerIdentity(
                platformInfo.platformSlug(),
                platformInfo.platformUserIdentifier(),
                platformInfo.platformUserHandle()
        );
    }

    private Optional<RedSecModeStats> extractMode(List<TrackerSegmentApiDto> segments, String type, String key) {
        return segments.stream()
                .filter(s -> type.equals(s.type()))
                .filter(s -> key.equals(s.attributes().key()))
                .findFirst()
                .map(this::mapStats);
    }

    private CareerRank extractCareerRank(List<TrackerSegmentApiDto> segments) {

        return segments.stream()
                .filter(s -> "overview".equals(s.type()))
                .map(TrackerSegmentApiDto::stats)
                .filter(Objects::nonNull)
                .map(stats -> stats.get("careerPlayerRank"))
                .filter(Objects::nonNull)
                .map(this::mapCareerRank)
                .findFirst()
                .orElse(null);
    }

    private CareerRank mapCareerRank(TrackerStatApiDto stat) {

        Map<String, Object> meta = stat.metadata() != null ? stat.metadata() : Map.of();

        String rankName = meta.get("rankName") instanceof String s ? s : null;
        String imageUrl = meta.get("imageUrl") instanceof String s ? s : null;

        return new CareerRank(
                stat.value().intValue(),
                rankName,
                imageUrl
        );
    }

    private RedSecModeStats mapStats(TrackerSegmentApiDto segment) {

        Map<String, TrackerStatApiDto> stats = segment.stats();

        int matchesPlayed = intVal(stats, "matchesPlayed");
        int matchesWon = intVal(stats, "matchesWon");
        double winRate = doubleVal(stats, "winPercentage");
        int kills = intVal(stats, "kills");
        int deaths = intVal(stats, "deaths");
        double kd = switch (segment.attributes().key()) {
            case "gm_graniteDuo" -> kills / (AVG_DEATHS_DUOS * matchesPlayed);
            case "gm_brsquad" -> kills / (AVG_DEATHS_QUADS * matchesPlayed);
            case "gm_gntgauntlet" -> kills / (AVG_DEATHS_GAUNTLET * matchesPlayed);
            case "gm_granite" -> kills / (AVG_DEATHS_TOTAL * matchesPlayed);
            default -> 0.0d;
        };

        //double kd = deaths == 0 ? kills : (double) kills / deaths;

        Duration timePlayed = Duration.ofSeconds(longVal(stats, "timePlayed"));

        return new RedSecModeStats(matchesPlayed, matchesWon, winRate, kills, deaths, kd, timePlayed);
    }

    private int intVal(Map<String, TrackerStatApiDto> stats, String key) {
        return stats.containsKey(key) ? stats.get(key).value().intValue() : 0;
    }

    private long longVal(Map<String, TrackerStatApiDto> stats, String key) {
        return stats.containsKey(key) ? stats.get(key).value().longValue() : 0L;
    }

    private double doubleVal(Map<String, TrackerStatApiDto> stats, String key) {
        return stats.containsKey(key) ? stats.get(key).value() : 0.0;
    }
}

