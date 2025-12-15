package com.mishkin.redsecbot.discord.listener;

import com.mishkin.redsecbot.application.service.PlayerStatsHistoryService;
import com.mishkin.redsecbot.discord.formatter.RedSecDiscordFormatter;
import com.mishkin.redsecbot.discord.handler.PlayerSelectionStore;
import com.mishkin.redsecbot.domain.model.RedSecStats;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author a.mishkin
 */
@Component
public class CheckSelectListener extends ListenerAdapter {

    private final PlayerSelectionStore selectionStore;
    private final PlayerStatsHistoryService statsHistoryService;
    private final RedSecDiscordFormatter formatter;

    public CheckSelectListener(
            PlayerSelectionStore selectionStore,
            PlayerStatsHistoryService statsHistoryService,
            RedSecDiscordFormatter formatter
    ) {
        this.selectionStore = selectionStore;
        this.statsHistoryService = statsHistoryService;
        this.formatter = formatter;
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (!"check-select".equals(event.getComponentId())) return;

        event.deferEdit().queue();

        long discordId = event.getUser().getIdLong();
        String selectedIdentifier = event.getValues().get(0);

        selectionStore.getSelected(discordId, selectedIdentifier)
                .ifPresentOrElse(p -> {
                    String platform = p.platformSlug();
                    String playerKey = "bf:" + platform + ":" + p.platformUserIdentifier();

                    Optional<RedSecStats> statsOpt = statsHistoryService
                            .getRedSecStats(playerKey, p.platformSlug(), p.platformUserIdentifier());

                    if (statsOpt.isEmpty()) {
                        event.getHook().sendMessage("❌ Игрок не играл в REDSEC").setEphemeral(true).queue();
                        return;
                    }

                    selectionStore.clear(discordId);

                    event.getHook()
                            .editOriginalEmbeds(formatter.format(statsOpt.get()))
                            .setComponents() // убираем меню
                            .queue();

                }, () -> event.getHook().sendMessage("❌ Ошибка, попробуй заново").setEphemeral(true).queue());
    }
}
