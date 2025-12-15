package com.mishkin.redsecbot.discord.dto.out;

/**
 * @author a.mishkin
 */
public record DiscordRsStatsDto(
        String title,
        String playerName,
        DiscordRsModeDto gauntlet,
        DiscordRsModeDto duos,
        DiscordRsModeDto quads
) {}