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

package com.welty.othello.scraper;

import com.orbanova.common.feed.Feed;
import com.orbanova.common.feed.Feeds;

import java.io.File;

/**
 * Contains information about a move in a game
 */
class Move {
    public final Board board;
    public final Status status;
    public final boolean isInitialPosition;  // true if this is the first position in a game where we're extending the draw tree

    public Move(Board board, Status status, boolean isInitialPosition) {
        this.board = board;
        this.status = status;
        this.isInitialPosition = isInitialPosition;
    }

    public static Feed<Move> fileFeed(String fileName) {
        final File file = new File(fileName);
        return fileFeed(file);
    }

    public static Feed<Move> fileFeed(File file) {
        return Feeds.ofLines(file).transform(StringToMoveTransformer.INSTANCE);
    }
}
