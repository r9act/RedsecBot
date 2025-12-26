package com.mishkin.redsecbot.discord.listener;

import com.mishkin.redsecbot.discord.handler.CheckCommandHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

/**
 * Router
 * @author a.mishkin
 */
@Component
public class CheckCommand extends ListenerAdapter {

    private final CheckCommandHandler handler;

    public CheckCommand(CheckCommandHandler handler) {
        this.handler = handler;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!"check".equals(event.getName())) {
            return;
        }

        handler.handle(event);
    }
}

