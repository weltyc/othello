package com.welty.othello.protocol;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

@ToString @EqualsAndHashCode
public class ErrorResponse implements NBoardResponse {
    /**
     * The line of text, received from the engine, that caused the error
     */
    public final String message;

    /**
     * Comment on why the line was in error, or null if no comment
     */
    public final @Nullable String comment;

    public ErrorResponse(String message, @Nullable String comment) {
        this.message = message;
        this.comment = comment;
    }
}
