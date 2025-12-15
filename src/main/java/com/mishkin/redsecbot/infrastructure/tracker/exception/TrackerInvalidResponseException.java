package com.mishkin.redsecbot.infrastructure.tracker.exception;

/**
 * @author a.mishkin
 */
public class TrackerInvalidResponseException extends RuntimeException {
    public TrackerInvalidResponseException(String message) {
        super(message);
    }
}
