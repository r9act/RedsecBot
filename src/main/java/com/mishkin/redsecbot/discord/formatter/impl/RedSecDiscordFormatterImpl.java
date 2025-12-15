package com.mishkin.redsecbot.discord.formatter.impl;

import com.mishkin.redsecbot.discord.formatter.RedSecDiscordFormatter;
import com.mishkin.redsecbot.domain.model.RedSecMode;
import com.mishkin.redsecbot.domain.model.RedSecModeStats;
import com.mishkin.redsecbot.domain.model.RedSecStats;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.time.Duration;

/**
 * @author a.mishkin
 */
@Component
public class RedSecDiscordFormatterImpl implements RedSecDiscordFormatter {

    @Override
    public MessageEmbed format(RedSecStats stats) {

        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle(
                stats.playerIdentity().platformUserHandle() + " — Rank "
                        + stats.careerRank().value()
                        + " (" + stats.careerRank().rankName() + ")"
        );

        embed.setColor(Color.RED);
        embed.setTimestamp(stats.fetchedAt());
        if (stats.careerRank().imageUrl() != null) {
            embed.setThumbnail(stats.careerRank().imageUrl());
        }

        embed.addField("TOTAL", formatMode(stats.total()), false);

        // ----- MODES -----
        stats.modes().forEach((mode, modeStats) ->
                embed.addField(
                        formatModeTitle(mode),
                        formatMode(modeStats),
                        true
                )
        );

        // ----- FOOTER -----
        embed.setFooter("Source: " + stats.source(), null);

        return embed.build();
    }


    // ---------------- private ----------------
    private String formatModeTitle(RedSecMode mode) {
        return switch (mode) {
            case QUADS -> "BR Quads";
            case DUOS -> "BR Duos";
            case GAUNTLET -> "Gauntlet";
            default -> mode.name();
        };
    }


    private String formatMode(RedSecModeStats stats) {
        return String.format("""
                🕹 Matches: %d
                🏆 Wins: %d (%.1f%%)
                🔫 K/D: %.2f
                ⏱ Time: %s
                """, stats.matchesPlayed(), stats.matchesWon(), stats.winRate(), stats.kd(), formatDuration(stats.timePlayed()));
    }

    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.minusHours(hours).toMinutes();

        return hours + "h " + minutes + "m";
    }
}

