package com.mishkin.redsecbot.application.facade;

import com.mishkin.redsecbot.domain.model.RedSecStats;

import java.util.Optional;

public interface RedSecStatsFacade {
    Optional<RedSecStats> getForPlayer(String playerKey, String platformSlug, String platformUserIdentifier);
}

