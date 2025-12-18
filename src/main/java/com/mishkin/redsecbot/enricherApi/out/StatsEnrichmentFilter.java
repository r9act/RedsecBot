package com.mishkin.redsecbot.enricherApi.out;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author a.mishkin
 */
@Component
public class StatsEnrichmentFilter {

    private final Set<String> enabledCandidates;

    public StatsEnrichmentFilter(StatsEnrichmentProperties props) {
        this.enabledCandidates = props.getEnabledCandidates()
                .stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    public boolean isEnabled(String handle) {
        return handle != null && enabledCandidates.contains(handle.toLowerCase());
    }
}

