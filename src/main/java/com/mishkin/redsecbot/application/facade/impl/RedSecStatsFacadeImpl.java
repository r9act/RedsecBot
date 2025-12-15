package com.mishkin.redsecbot.application.facade.impl;

import com.mishkin.redsecbot.application.service.PlayerStatsHistoryService;
import com.mishkin.redsecbot.domain.model.RedSecStats;
import com.mishkin.redsecbot.application.facade.RedSecStatsFacade;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author a.mishkin
 */
@Service
public class RedSecStatsFacadeImpl implements RedSecStatsFacade {

    private final PlayerStatsHistoryService statsHistoryService;

    public RedSecStatsFacadeImpl(PlayerStatsHistoryService statsHistoryService) {
        this.statsHistoryService = statsHistoryService;
    }

    @Override
    public Optional<RedSecStats> getForPlayer(String playerKey, String platformSlug, String platformUserIdentifier) {
        return statsHistoryService.getRedSecStats(playerKey, platformSlug, platformUserIdentifier);
    }
}

