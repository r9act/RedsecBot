package com.mishkin.redsecbot.infrastructure.cassandra.config;

import com.datastax.oss.driver.api.core.CqlSession;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * @author a.mishkin
 */
@Component
@Profile("dev")
public class CassandraSchemaInitializer {

    private final CqlSession session;

    public CassandraSchemaInitializer(CqlSession session) {
        this.session = session;
    }

    @PostConstruct
    public void init() {
        session.execute("""
            CREATE KEYSPACE IF NOT EXISTS bfbot
            WITH replication = {
              'class': 'SimpleStrategy',
              'replication_factor': 1
            }
        """);

        session.execute("""
            CREATE TABLE IF NOT EXISTS bfbot.player_stats_history (
                player_key text,
                stats_type text,
                fetched_at timestamp,
                payload text,
                source text,
                PRIMARY KEY ((player_key, stats_type), fetched_at)
            ) WITH CLUSTERING ORDER BY (fetched_at DESC)
        """);
    }
}

