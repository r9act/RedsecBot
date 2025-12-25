package com.mishkin.redsecbot.application.facade.impl;

import com.mishkin.redsecbot.application.facade.RedSecStatsFacade;
import com.mishkin.redsecbot.application.service.PlayerStatsHistoryService;
import com.mishkin.redsecbot.domain.model.GameIdentity;
import com.mishkin.redsecbot.domain.model.StatsWithSource;
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
    public Optional<StatsWithSource> getForPlayer(GameIdentity gameIdentity) {
        return statsHistoryService.getRedSecStats(gameIdentity);
    }
}

