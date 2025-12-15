package com.mishkin.redsecbot.discord.dto.out;

/**
 * @author a.mishkin
 */
public record DiscordRsModeDto(
        String kd,
        String kills,
        String deaths,
//        String assists,
//        String revives,
        String wins,
        String games,
        String timePlayed
) {}
