/*
 * Copyright (c) 2014 Chris Welty.
 *
 * This is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * For the license, see <http://www.gnu.org/licenses/gpl.html>.
 */

package com.welty.othello.gdk;

import com.welty.othello.c.CReader;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

/**
 * Information about a player in a GGF format game
 */
@EqualsAndHashCode
public class OsPlayerInfo {
    public static final OsPlayerInfo UNKNOWN = new OsPlayerInfo("", 0);

    public final @NotNull String name;
    public final double rating;

    /**
     * Read in player info from an OsMatch message
     *
     * todo: move this to OsMatch
     *
     * @param is stream to read from
     */
    OsPlayerInfo(@NotNull CReader is) {
        rating = is.readDoubleNoExponent();
        name = is.readString();
    }

    /**
     * Construct an OsPlayerInfo
     *
     * @param name player name
     * @param rating player rating
     */
    OsPlayerInfo(@NotNull String name, double rating) {
        this.name = name;
        this.rating = rating;
    }
}
