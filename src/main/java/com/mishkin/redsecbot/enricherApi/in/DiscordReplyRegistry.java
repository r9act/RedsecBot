package com.mishkin.redsecbot.enricherApi.in;

import net.dv8tion.jda.api.interactions.InteractionHook;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author a.mishkin
 */
@Component
public class DiscordReplyRegistry {

    private final Map<String, InteractionHook> hooks = new ConcurrentHashMap<>();

    public void register(String correlationId, InteractionHook hook) {
        hooks.put(correlationId, hook);
    }

    public Optional<InteractionHook> take(String correlationId) {
        return Optional.ofNullable(hooks.remove(correlationId));
    }
}

