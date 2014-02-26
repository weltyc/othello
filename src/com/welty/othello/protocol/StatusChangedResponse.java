package com.welty.othello.protocol;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A message that tells the user that the engine's status has changed.
 */
@EqualsAndHashCode
public class StatusChangedResponse implements NBoardResponse {
    /**
     * This status is for debugging only. Users should get status from getStatus() as it is up-to-date and
     * references the correct engine.
     */
    private final String status;

    public StatusChangedResponse(String status) {
        this.status = status;
    }

    @Override public String toString() {
        return "status " + status;
    }
}
