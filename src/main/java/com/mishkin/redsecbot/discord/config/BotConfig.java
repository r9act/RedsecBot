package com.mishkin.redsecbot.discord.config;

import com.mishkin.redsecbot.discord.listener.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author a.mishkin
 */
@Configuration
@RequiredArgsConstructor
public class BotConfig {

    @Value("${discord.token}")
    private String token;

    @Value("${discord.guild-id}") // твой сервер, для мгновенной регистрации команд
    private String guildId;

    private final RsCommand rsCommand;
    private final LinkCommand linkCommand;
    private final CheckCommand checkCommand;
    private final HelpCommand helpCommand;
    private final CheckSelectListener checkSelectListener;

    @PostConstruct
    public void startBot() throws Exception {

        JDA jda = JDABuilder.createDefault(token)
                .addEventListeners(
                        rsCommand,
                        linkCommand,
                        checkCommand,
                        helpCommand,
                        checkSelectListener,
                        new ReadyListener(guildId)
                )
                .build();

        jda.awaitReady();
        System.out.println("Bot started.");
    }

    @RequiredArgsConstructor
    public static class ReadyListener extends ListenerAdapter {

        private final String guildId;

        @Override
        public void onReady(ReadyEvent event) {

            Guild guild = event.getJDA().getGuildById(guildId);

            if (guild == null) {
                System.err.println("Guild not found: " + guildId);
                return;
            }

            guild.updateCommands()
                    .addCommands(
                            Commands.slash("link", "Привязать BF профиль")
                                    .addOption(OptionType.STRING, "platform", "origin/steam", true)
                                    .addOption(OptionType.STRING, "name", "Ваш ник в Battlefield", true),

                            Commands.slash("rs", "Показать RedSec статистику"),

                            Commands.slash("check", "Проверить игрока по BF имени")
                                    .addOption(OptionType.STRING, "platform", "origin/steam", true)
                                    .addOption(OptionType.STRING, "name", "Имя игрока для проверки", true),

                            Commands.slash("help", "Показать инструкцию")
                    )
                    .queue();

            System.out.println("Slash commands registered for guild: " + guildId);
        }
    }
}

