package com.mishkin.redsecbot.discord.listener;

import com.mishkin.redsecbot.discord.handler.RsCommandHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

/**
 * Router
 * @author a.mishkin
 */
@Component
public class RsCommand extends ListenerAdapter {

    private final RsCommandHandler handler;

    public RsCommand(RsCommandHandler handler) {
        this.handler = handler;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("rs")) {
            return;
        }

        handler.handle(event);
    }
}


