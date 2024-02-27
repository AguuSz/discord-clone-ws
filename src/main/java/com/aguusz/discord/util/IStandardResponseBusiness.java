package com.aguusz.discord.util;

import org.springframework.http.HttpStatus;

public interface IStandardResponseBusiness {
    public StandardResponse build(HttpStatus httpStatus, Throwable ex, String message);
}
