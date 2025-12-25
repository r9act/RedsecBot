package com.mishkin.redsecbot.application.service;

import com.mishkin.redsecbot.domain.model.GameIdentity;
import com.mishkin.redsecbot.domain.model.StatsWithSource;

import java.util.Optional;

/**
 * @author a.mishkin
 */
public interface PlayerStatsHistoryService {

    Optional<StatsWithSource> getRedSecStats(GameIdentity gameIdentity);
}




