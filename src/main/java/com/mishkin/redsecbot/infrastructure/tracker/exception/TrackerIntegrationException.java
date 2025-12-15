package com.mishkin.redsecbot.infrastructure.tracker.exception;

/**
 * @author a.mishkin
 */
public class TrackerIntegrationException extends RuntimeException {
    public TrackerIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
