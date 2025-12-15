package com.mishkin.redsecbot.application.exception;

public class UserAlreadyLinkedException extends RuntimeException {
    public UserAlreadyLinkedException(long discordId) {
        super("Ты уже привязал свой профиль, дурачок");
    }
}
