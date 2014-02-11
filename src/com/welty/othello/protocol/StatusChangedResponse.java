package com.welty.othello.protocol;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A message that tells the user that the engine's status has changed.
 */
@EqualsAndHashCode @ToString
public class StatusChangedResponse implements NBoardResponse {
}
