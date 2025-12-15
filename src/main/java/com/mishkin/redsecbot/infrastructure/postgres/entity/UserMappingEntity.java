package com.mishkin.redsecbot.infrastructure.postgres.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

/**
 * @author a.mishkin
 */
@Entity
@Table(
        name = "discord_user_mapping",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_discord_user_mapping_discord_id",
                        columnNames = "discord_id"
                )
        },
        indexes = {
                @Index(
                        name = "idx_user_mapping_platform_identifier",
                        columnList = "platform_user_identifier"
                )
        }
)
@Data
public class UserMappingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "discord_id", nullable = false)
    private Long discordId;

    @Column(name = "game", nullable = false)
    private String game;

    @Column(name = "platform_id", nullable = false)
    private Integer platformId;

    @Column(name = "platform_slug", nullable = false, length = 32)
    private String platformSlug;

    @Column(name = "platform_user_identifier", nullable = false, length = 64)
    private String platformUserIdentifier;

    @Column(name = "platform_user_handle", nullable = false, length = 64)
    private String platformUserHandle;

    @Column(name = "title_user_id", length = 64)
    private String titleUserId;

    @Column(name = "country_code", length = 8)
    private String countryCode;

    @Column(name = "rank")
    private Integer rank;

    @Column(name = "status", length = 128)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.game == null) {
            this.game = "BF6";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

}


