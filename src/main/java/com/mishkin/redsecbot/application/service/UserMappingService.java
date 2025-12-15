package com.mishkin.redsecbot.application.service;

import com.mishkin.redsecbot.infrastructure.tracker.dto.in.player.TrackerSearchResultApiDto;
import com.mishkin.redsecbot.infrastructure.postgres.entity.UserMappingEntity;

public interface UserMappingService {

    UserMappingEntity getByDiscordId(long discordId);

    UserMappingEntity linkSelectedPlayer(long discordId, TrackerSearchResultApiDto selectedPlayer);

    boolean isLinked(long discordId);
}

