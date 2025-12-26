package com.mishkin.redsecbot.discord.listener;

import com.mishkin.redsecbot.discord.formatter.RedSecDiscordFormatter;
import com.mishkin.redsecbot.discord.handler.PlayerSelectionStore;
import com.mishkin.redsecbot.discord.handler.StatsInteractionUseCase;
import com.mishkin.redsecbot.discord.reply.DiscordReplyRegistry;
import com.mishkin.redsecbot.domain.model.GameIdentity;
import com.mishkin.redsecbot.domain.model.StatsInteractionResult;
import com.mishkin.redsecbot.domain.model.StatsWithSource;
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
    private final StatsInteractionUseCase interactionUseCase;
    private final RedSecDiscordFormatter formatter;
    private final DiscordReplyRegistry replyRegistry;

    public CheckSelectListener(PlayerSelectionStore selectionStore, StatsInteractionUseCase interactionUseCase,
                               RedSecDiscordFormatter formatter, DiscordReplyRegistry replyRegistry) {
        this.selectionStore = selectionStore;
        this.interactionUseCase = interactionUseCase;
        this.formatter = formatter;
        this.replyRegistry = replyRegistry;
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (!"check-select".equals(event.getComponentId())) return;

        event.deferEdit().queue();

        long discordId = event.getUser().getIdLong();
        String selectedIdentifier = event.getValues().get(0);

        selectionStore.getSelected(discordId, selectedIdentifier)
                .ifPresentOrElse(p -> {

                            GameIdentity gameIdentity = new GameIdentity(p.platformSlug(), p.platformUserIdentifier());
                            Optional<StatsInteractionResult> resultOpt = interactionUseCase
                                    .handleRedsecStatsInteraction(discordId, gameIdentity);

                            if (resultOpt.isEmpty()) {
                                event.getHook()
                                        .sendMessage("❌ Игрок не играл в REDSEC")
                                        .setEphemeral(true)
                                        .queue();
                                return;
                            }
                            StatsInteractionResult interactionResult = resultOpt.get();
                            StatsWithSource result = interactionResult.stats();
                            replyRegistry.register(interactionResult.correlationId().toString(), event.getHook());

                            selectionStore.clear(discordId);

                            event.getHook()
                                    .editOriginalEmbeds(formatter.format(result.stats()))
                                    .setComponents()
                                    .queue();
                        }, () -> event.getHook()
                                .sendMessage("❌ Ошибка, попробуй заново")
                                .setEphemeral(true)
                                .queue()
                );
    }
}
