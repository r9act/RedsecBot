package com.mishkin.redsecbot.infrastructure.postgres.repo;

import com.mishkin.redsecbot.infrastructure.postgres.entity.UserMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author a.mishkin
 */
public interface UserMappingRepository extends JpaRepository<UserMappingEntity, Long> {

    Optional<UserMappingEntity> findByDiscordId(Long discordId);

    Optional<UserMappingEntity> findByPlatformUserIdentifier(String platformUserIdentifier);

    boolean existsByDiscordId(Long discordId);
}

