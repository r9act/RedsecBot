package com.mishkin.redsecbot.infrastructure.outbox.repo.impl;

import com.mishkin.redsecbot.infrastructure.outbox.event.OutboxEvent;
import com.mishkin.redsecbot.infrastructure.outbox.event.OutboxStatus;
import com.mishkin.redsecbot.infrastructure.outbox.repo.OutboxRepository;
import org.postgresql.util.PGobject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Outbox - журнал доставки
 * @author a.mishkin
 */
@Repository
public class JdbcOutboxRepository implements OutboxRepository {

    private final JdbcTemplate jdbc;

    public JdbcOutboxRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void save(OutboxEvent event) {
        jdbc.update(con -> {

            PreparedStatement ps = con.prepareStatement(
                    """
                    INSERT INTO outbox_event (
                        id,
                        aggregate_type,
                        aggregate_id,
                        topic,
                        kafka_key,
                        event_version,
                        payload,
                        status,
                        created_at
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """
            );

            ps.setObject(1, event.id());
            ps.setString(2, event.aggregateType());
            ps.setObject(3, event.aggregateId());
            ps.setString(4, event.topic());
            ps.setString(5, event.kafkaKey());
            ps.setInt(6, event.eventVersion());

            //bug: JSONB
            PGobject jsonb = new PGobject();
            jsonb.setType("jsonb");
            jsonb.setValue(event.payload());
            ps.setObject(7, jsonb);

            ps.setString(8, event.status().name());
            ps.setTimestamp(9, Timestamp.from(event.createdAt()));

            return ps;
        });
    }

    @Override
    public List<OutboxEvent> findUnpublished(int limit) {
        return jdbc.query(
                """
                        SELECT
                            id,
                            aggregate_type,
                            aggregate_id,
                            topic,
                            kafka_key,
                            event_version,
                            payload,
                            status,
                            created_at,
                            published_at
                        FROM outbox_event
                        WHERE status = 'NEW'
                        ORDER BY created_at
                        LIMIT ?
                        """,
                this::mapRow,
                limit
        );
    }

    @Override
    public void markPublished(UUID id, Instant publishedAt) {
        jdbc.update(
                """
                        UPDATE outbox_event
                        SET status = 'PUBLISHED',
                            published_at = ?
                        WHERE id = ?
                        """,
                Timestamp.from(publishedAt),
                id
        );
    }

    @Override
    public void markFailed(UUID id) {
        jdbc.update(
                """
                        UPDATE outbox_event
                        SET status = 'FAILED'
                        WHERE id = ?
                        """,
                id
        );
    }

    private OutboxEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new OutboxEvent(
                UUID.fromString(rs.getString("id")),
                rs.getString("aggregate_type"),
                UUID.fromString(rs.getString("aggregate_id")),
                rs.getString("topic"),
                rs.getString("kafka_key"),
                rs.getInt("event_version"),
                rs.getString("payload"),
                OutboxStatus.valueOf(rs.getString("status")),
                rs.getTimestamp("created_at").toInstant(),
                rs.getTimestamp("published_at") != null
                        ? rs.getTimestamp("published_at").toInstant()
                        : null
        );
    }
}

