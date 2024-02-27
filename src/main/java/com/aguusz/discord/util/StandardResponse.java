package com.aguusz.discord.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StandardResponse {
    private String message;

    @JsonIgnore
    private Throwable ex;

    @JsonIgnore
    private HttpStatus httpStatus;

    public int getCode() {
        return httpStatus.value();
    }

    @JsonIgnore
    private boolean devInfoEnabled; // Si es true voy a ser mas verboso

    public String getDevInfo() {
        if (devInfoEnabled) {
            if (ex != null)
                return ExceptionUtils.getStackTrace(ex);
            else
                return "No stack trace";
        } else {
            return null;
        }
    }

    public String getMessage(String message) {
        if (message != null)
            return message;
        else
            return ex.getMessage();
    }
}
