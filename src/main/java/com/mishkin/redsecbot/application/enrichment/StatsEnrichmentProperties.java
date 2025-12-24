package com.mishkin.redsecbot.application.enrichment;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author a.mishkin
 */
@Configuration
@ConfigurationProperties(prefix = "stats-enrichment")
public class StatsEnrichmentProperties {

    private List<String> enabledCandidates = new ArrayList<>();

    public List<String> getEnabledCandidates() {
        return enabledCandidates;
    }

    public void setEnabledCandidates(List<String> enabledCandidates) {
        this.enabledCandidates = enabledCandidates;
    }
}

