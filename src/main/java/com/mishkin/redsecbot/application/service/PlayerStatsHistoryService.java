package com.mishkin.redsecbot.application.service;

import com.mishkin.redsecbot.domain.model.RedSecStats;

import java.util.Optional;

/**
 * @author a.mishkin
 */
public interface PlayerStatsHistoryService {

    Optional<RedSecStats> getRedSecStats(
            String playerKey,
            String platformSlug,
            String platformUserIdentifier
    );
}




