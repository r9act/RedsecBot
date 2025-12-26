package com.mishkin.redsecbot.application.event;

import io.micrometer.core.instrument.Metrics;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author a.mishkin
 */
@Aspect
@Component
public class DiscordInteractionAspect {

    private Logger log = LoggerFactory.getLogger(DiscordInteractionAspect.class);

    @AfterReturning(pointcut = "@annotation(interaction)", returning = "result")
    public void afterInteraction(JoinPoint jp, DiscordInteraction interaction, Optional<?> result) {

        if (result.isEmpty()) {
            return;
        }

        Object[] args = jp.getArgs();
        long requesterDiscordId = (long) args[0];

        //Лог
        log.info("Discord interaction executed: command={}, requester={}", interaction.command(), requesterDiscordId);

        //Метрика
        Metrics.counter("discord.interaction.success", "command", interaction.command()).increment();

        //MDC / tracing
        MDC.put("discord.command", interaction.command());
        MDC.put("requesterDiscordId", String.valueOf(requesterDiscordId));
    }
}


