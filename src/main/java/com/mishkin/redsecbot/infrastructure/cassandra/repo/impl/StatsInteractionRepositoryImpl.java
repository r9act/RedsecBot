package com.mishkin.redsecbot.infrastructure.cassandra.repo.impl;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.mishkin.redsecbot.infrastructure.cassandra.StatsInteractionRow;
import com.mishkin.redsecbot.infrastructure.cassandra.repo.StatsInteractionRepository;
import org.springframework.stereotype.Repository;

/**
 * @author a.mishkin
 */
@Repository
public class StatsInteractionRepositoryImpl implements StatsInteractionRepository {

    private static final String TABLE = "stats_interaction";

    private final CqlSession session;

    private volatile PreparedStatement insertStmt;

    public StatsInteractionRepositoryImpl(CqlSession cqlSession) {
        this.session = cqlSession;
    }


    @Override
    public void save(StatsInteractionRow row) {
        BoundStatement stmt = insertStmt().bind(
                row.correlationId(),
                row.requesterDiscordId(),
                row.playerKey(),
                row.platformUserHandle(),
                row.statsType(),
                row.fetchedAt(),
                row.source().name(),
                row.createdAt()
        );
        session.execute(stmt);
    }

    private PreparedStatement insertStmt() {
        if (insertStmt == null) {
            synchronized (this) {
                if (insertStmt == null) {
                    insertStmt = session.prepare(
                            "INSERT INTO " + TABLE + " (" +
                                    "correlation_id, " +
                                    "requesterDiscordId, " +
                                    "player_key, " +
                                    "player_name, " +
                                    "stats_type, " +
                                    "fetched_at, " +
                                    "source, " +
                                    "created_at" +
                                    ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
                    );
                }
            }
        }
        return insertStmt;
    }
}
