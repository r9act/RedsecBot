package com.mishkin.redsecbot.application.exception;

public class UserNotLinkedException extends RuntimeException {
    public UserNotLinkedException(long discordId) {
        super("Пользователь еще не привязал свой профиль (discordId = " + discordId + ")");
    }
}
