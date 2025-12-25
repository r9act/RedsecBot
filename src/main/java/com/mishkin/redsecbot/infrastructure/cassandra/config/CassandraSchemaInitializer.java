package com.mishkin.redsecbot.infrastructure.cassandra.config;

import com.datastax.oss.driver.api.core.CqlSession;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * @author a.mishkin
 */
@Component
@Profile("dev")
public class CassandraSchemaInitializer {

    private static final Logger log = LoggerFactory.getLogger(CassandraSchemaInitializer.class);
    private final CqlSession adminSession;

    public CassandraSchemaInitializer(@Qualifier("adminCqlSession") CqlSession adminSession) {
        this.adminSession = adminSession;
    }

    @PostConstruct
    public void init() {
        log.info("Starting Cassandra schema initialization...");

        // 1. Создаем Keyspace
        adminSession.execute("""
                    CREATE KEYSPACE IF NOT EXISTS bfbot
                    WITH replication = {
                      'class': 'SimpleStrategy',
                      'replication_factor': 1
                    }
                """);

        adminSession.execute("""
                    CREATE TABLE IF NOT EXISTS bfbot.player_stats_history (
                        player_key text,
                        stats_type text,
                        fetched_at timestamp,
                        payload text,
                        source text,
                        PRIMARY KEY ((player_key, stats_type), fetched_at)
                    ) WITH CLUSTERING ORDER BY (fetched_at DESC)
                """);

        adminSession.execute("""
                    CREATE TABLE IF NOT EXISTS bfbot.stats_interaction (
                        correlation_id uuid,
                        requesterDiscordId text,
                        player_key text,
                        player_name text,
                        stats_type text, 
                        fetched_at timestamp,
                        source text,
                        created_at timestamp,
                        PRIMARY KEY (correlation_id)
                    )
                """);

        log.info("✅ Cassandra schema initialized successfully.");
    }
}

