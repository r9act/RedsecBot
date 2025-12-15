CREATE KEYSPACE IF NOT EXISTS bfbot
    WITH replication = {
        'class': 'SimpleStrategy',
        'replication_factor': 1
        };

USE bfbot;

CREATE TABLE IF NOT EXISTS player_stats_history (
                                                    player_key text,
                                                    stats_type text,
                                                    fetched_at timestamp,
                                                    payload text,
                                                    source text,
                                                    PRIMARY KEY ((player_key, stats_type), fetched_at)
) WITH CLUSTERING ORDER BY (fetched_at DESC);
