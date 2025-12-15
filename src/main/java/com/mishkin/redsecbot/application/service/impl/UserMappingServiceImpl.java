package com.mishkin.redsecbot.application.service.impl;

import com.mishkin.redsecbot.infrastructure.postgres.entity.UserMappingEntity;
import com.mishkin.redsecbot.infrastructure.postgres.repo.UserMappingRepository;
import com.mishkin.redsecbot.application.service.UserMappingService;
import com.mishkin.redsecbot.infrastructure.tracker.dto.in.player.TrackerSearchResultApiDto;
import com.mishkin.redsecbot.application.exception.UserAlreadyLinkedException;
import com.mishkin.redsecbot.application.exception.UserNotLinkedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author a.mishkin
 */
@Service
@Transactional
public class UserMappingServiceImpl implements UserMappingService {

    private final UserMappingRepository repository;

    public UserMappingServiceImpl(UserMappingRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserMappingEntity getByDiscordId(long discordId) {
        return repository.findByDiscordId(discordId)
                .orElseThrow(() -> new UserNotLinkedException(discordId));
    }

    @Override
    public UserMappingEntity linkSelectedPlayer(long discordId, TrackerSearchResultApiDto p) {
        if (isLinked(discordId)) {
            throw new UserAlreadyLinkedException(discordId);
        }
        UserMappingEntity entity = repository.findByDiscordId(discordId)
                .orElseGet(UserMappingEntity::new);

        entity.setDiscordId(discordId);
        entity.setPlatformId(p.platformId());
        entity.setPlatformSlug(p.platformSlug());
        entity.setPlatformUserIdentifier(p.platformUserIdentifier());
        entity.setPlatformUserHandle(p.platformUserHandle());
        entity.setTitleUserId(p.titleUserId());

        if (p.metadata() != null) {
            entity.setCountryCode(p.metadata().countryCode());
        }

        entity.setStatus(p.status());
        entity.setRank(extractRank(p.status()));

        return repository.save(entity);
    }

    @Override
    public boolean isLinked(long discordId) {
        return repository.existsByDiscordId(discordId);
    }

    /**
     * "Rank 209 • Last updated 12 minutes ago" - остюда вычленить
     */
    private Integer extractRank(String status) {
        if (status == null) {
            return null;
        }

        Matcher m = Pattern.compile("Rank\\s+(\\d+)").matcher(status);
        return m.find() ? Integer.valueOf(m.group(1)) : null;
    }
}


