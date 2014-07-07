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

package com.welty.othello.database;

import com.orbanova.common.feed.Feed;
import com.orbanova.common.feed.Mapper;
import com.orbanova.common.feed.Transformer;
import com.orbanova.common.misc.Require;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.List;

/**
 * GGS Matches can be either a single game or two games, played simultaneously, with colours reversed.
 */
public class OthelloMatch {
    public static final Mapper<String, OthelloMatch> PARSER = new Mapper<String, OthelloMatch>() {
        @NotNull @Override public OthelloMatch y(String x) {
            try {
                return new OthelloMatch(x);
            } catch (EOFException e) {
                throw new RuntimeException(e);
            }
        }
    };

    final int nGames;
    public List<OthelloGame> games = new ArrayList<>();

    /**
     * Create an OthelloMatch from its string representation
     *
     * @param stringRepresentation text representation of the match, as found in GGS game file downloads
     * @throws EOFException if EOF is reached before parsing the match
     */
    public OthelloMatch(String stringRepresentation) throws EOFException {
        final Parser parser = new Parser(stringRepresentation);
        nGames = parser.readPositiveInt();
        parser.skipWs();
        while (parser.notEof()) {
            parser.expect("(;");
            final String ggsMap = parser.readUntil(';');
            games.add(new OthelloGame(ggsMap));
            parser.expect(";)");
            parser.skipWs();
        }
        Require.eq(games.size(), "# games", nGames);
    }

}
