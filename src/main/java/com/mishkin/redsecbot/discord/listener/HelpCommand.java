package com.mishkin.redsecbot.discord.listener;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

/**
 * @author a.mishkin
 */
@Component
public class HelpCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("help")) return;

        event.reply("""
                **Инструкция по использованию бота**

                1️⃣ Привяжите свой профиль Battlefield:
                `/link <bfName>`

                2️⃣ Посмотреть RedSec статистику:
                `/rs`
                
                3️⃣ Проверить любого игрока:
                `/check <bfName>`

                ❗ Если команда не работает — сначала выполните `/link`.

                """).setEphemeral(true).queue();
    }
}

