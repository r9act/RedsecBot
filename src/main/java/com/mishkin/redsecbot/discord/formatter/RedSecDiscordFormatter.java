package com.mishkin.redsecbot.discord.formatter;

import com.mishkin.redsecbot.domain.model.RedSecStats;
import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 * @author a.mishkin
 */
public interface RedSecDiscordFormatter {

    MessageEmbed format(RedSecStats stats);
}

