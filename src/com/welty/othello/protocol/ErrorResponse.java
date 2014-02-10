package com.welty.othello.protocol;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString @EqualsAndHashCode
public class ErrorResponse implements NBoardResponse {
    /**
     * The line of text, received from the engine, that caused the error
     */
    public final String message;

    public ErrorResponse(String message) {
        this.message = message;
    }
}
