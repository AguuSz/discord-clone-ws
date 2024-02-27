package com.aguusz.discord.exceptions;

import lombok.Builder;

public class NotAllowedException extends Exception{

    @Builder
    public NotAllowedException(String message, Throwable ex) {
        super(message, ex);
    }

    @Builder
    public NotAllowedException(String message) {
        super(message);
    }

    @Builder
    public NotAllowedException(Throwable ex) {
        super(ex.getMessage(), ex);
    }

}
