package com.mishkin.redsecbot.domain.model;

/**
 * @author a.mishkin
 */
public record GameIdentity(String platformSlug, String platformUserIdentifier) {
    public String toPlayerKey() {
        return "bf:" + platformSlug + ":" + platformUserIdentifier;
    }
}
