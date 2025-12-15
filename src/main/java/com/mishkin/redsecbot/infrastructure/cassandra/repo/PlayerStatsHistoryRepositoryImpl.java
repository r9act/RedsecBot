package com.mishkin.redsecbot.infrastructure.cassandra.repo;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.*;
import com.mishkin.redsecbot.infrastructure.cassandra.PlayerStatsHistoryRow;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PlayerStatsHistoryRepositoryImpl implements PlayerStatsHistoryRepository {

    private static final String TABLE = "player_stats_history";

    private final CqlSession session;

    // lazy init
    private volatile PreparedStatement selectLatestStmt;
    private volatile PreparedStatement insertStmt;

    public PlayerStatsHistoryRepositoryImpl(CqlSession session) {
        this.session = session;
    }

    // ---------- public API ----------

    @Override
    public Optional<PlayerStatsHistoryRow> findLatest(String playerKey, String statsType) {
        BoundStatement stmt = selectLatestStmt().bind(playerKey, statsType);
        Row row = session.execute(stmt).one();
        return Optional.ofNullable(row).map(this::mapRow);
    }

    @Override
    public void save(PlayerStatsHistoryRow row) {
        BoundStatement stmt = insertStmt().bind(
                row.playerKey(),
                row.statsType(),
                row.fetchedAt(),
                row.payload(),
                row.source()
        );
        session.execute(stmt);
    }

    // ---------- lazy prepared statements ----------

    private PreparedStatement selectLatestStmt() {
        if (selectLatestStmt == null) {
            synchronized (this) {
                if (selectLatestStmt == null) {
                    selectLatestStmt = session.prepare(
                            "SELECT player_key, stats_type, fetched_at, payload, source " +
                                    "FROM " + TABLE + " " +
                                    "WHERE player_key = ? AND stats_type = ? " +
                                    "LIMIT 1"
                    );
                }
            }
        }
        return selectLatestStmt;
    }

    private PreparedStatement insertStmt() {
        if (insertStmt == null) {
            synchronized (this) {
                if (insertStmt == null) {
                    insertStmt = session.prepare(
                            "INSERT INTO " + TABLE + " " +
                                    "(player_key, stats_type, fetched_at, payload, source) " +
                                    "VALUES (?, ?, ?, ?, ?)"
                    );
                }
            }
        }
        return insertStmt;
    }

    // ---------- mapping ----------

    private PlayerStatsHistoryRow mapRow(Row row) {
        return new PlayerStatsHistoryRow(
                row.getString("player_key"),
                row.getString("stats_type"),
                row.getInstant("fetched_at"),
                row.getString("payload"),
                row.getString("source")
        );
    }
}

