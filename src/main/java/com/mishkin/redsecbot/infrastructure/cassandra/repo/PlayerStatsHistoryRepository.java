package com.mishkin.redsecbot.infrastructure.cassandra.repo;

import com.mishkin.redsecbot.infrastructure.cassandra.PlayerStatsHistoryRow;

import java.util.Optional;

public interface PlayerStatsHistoryRepository {

    Optional<PlayerStatsHistoryRow> findLatest(
            String playerKey,
            String statsType
    );

    void save(PlayerStatsHistoryRow row);
}


