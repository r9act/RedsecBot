package com.mishkin.redsecbot.discord.utils;

import com.mishkin.redsecbot.infrastructure.tracker.exception.TrackerIntegrationException;
import com.mishkin.redsecbot.infrastructure.tracker.exception.TrackerInvalidResponseException;

/**
 * @author a.mishkin
 */
public final class ExceptionUtils {

    private ExceptionUtils() {}

    public static String safeMessage(Throwable ex) {
        Throwable cause = unwrap(ex);

        if (cause instanceof TrackerIntegrationException) {
            return "Ошибка web-клиента";
        }

        if (cause instanceof TrackerInvalidResponseException) {
            return "TrackerGG вернул некорректные данные";
        }

        return cause.getMessage() != null ? cause.getMessage() : "unknown error";
    }

    public static Throwable unwrap(Throwable ex) {
        return ex.getCause() != null ? ex.getCause() : ex;
    }
}
