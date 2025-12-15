package com.mishkin.redsecbot.infrastructure.tracker.dto.in.stats;

import java.util.List;

/**
 * @author a.mishkin
 */
public record UserInfoDto(
        String userId,
        Boolean isPremium,
        Boolean isVerified,
        Boolean isInfluencer,
        Boolean isPartner,
        String countryCode,
        String customAvatarUrl,
        String customHeroUrl,
        String customAvatarFrame,
        Object customAvatarFrameInfo,
        Object premiumDuration,
        List<Object> socialAccounts,
        Object badges,
        Integer pageviews,
        Object xpTier,
        Boolean isSuspicious
) {}
