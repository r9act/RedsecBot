package com.mishkin.redsecbot.domain.model;

import java.util.UUID;

/**
 * @author a.mishkin
 */
public record StatsInteractionResult(
        UUID correlationId,
        StatsWithSource stats
) {}