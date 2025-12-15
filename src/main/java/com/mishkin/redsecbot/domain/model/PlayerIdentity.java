package com.mishkin.redsecbot.domain.model;

public record PlayerIdentity(
        String platformSlug,              // origin
        String platformUserIdentifier,    // 2989609253
        String platformUserHandle
) {}

