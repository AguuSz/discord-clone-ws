package com.aguusz.discord.exceptions;

import lombok.Builder;

public class BadPasswordException extends Exception {
    private static final long serialVersionUID = -9079454849611061074L;

    @Builder
    public BadPasswordException(String message, Throwable ex) {
        super(message, ex);
    }

    @Builder
    public BadPasswordException(String message) {
        super(message);
    }

    @Builder
    public BadPasswordException(Throwable ex) {
        super(ex.getMessage(), ex);
    }
}
