package com.mishkin.redsecbot.infrastructure.cassandra.repo;

import com.mishkin.redsecbot.infrastructure.cassandra.StatsInteractionRow;

public interface StatsInteractionRepository {
    void save(StatsInteractionRow row);
}
