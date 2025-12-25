package com.mishkin.redsecbot.application.facade;

import com.mishkin.redsecbot.domain.model.GameIdentity;
import com.mishkin.redsecbot.domain.model.StatsWithSource;

import java.util.Optional;

public interface RedSecStatsFacade {
    Optional<StatsWithSource> getForPlayer(GameIdentity gameIdentity);
}

