package com.mishkin.redsecbot.discord.listener;

import com.mishkin.redsecbot.discord.handler.LinkCommandHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

/**
 * Router
 * @author a.mishkin
 */
@Component
public class LinkCommand extends ListenerAdapter {

    private final LinkCommandHandler handler;

    public LinkCommand(LinkCommandHandler handler) {
        this.handler = handler;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!"link".equals(event.getName())) {
            return;
        }

        handler.handle(event);
    }
}


