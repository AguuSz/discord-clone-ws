package com.aguusz.discord.exceptions;

import lombok.Builder;

public class MissingDataException extends Exception {
    private static final long serialVersionUID = 1L;
    @Builder
    public MissingDataException(String message, Throwable ex) {
        super(message, ex);
    }

    @Builder
    public MissingDataException(String message) {
        super(message);
    }

    @Builder
    public MissingDataException(Throwable ex) {
        super(ex.getMessage(), ex);
    }
}
