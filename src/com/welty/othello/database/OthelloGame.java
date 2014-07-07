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

import com.orbanova.common.misc.Require;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class OthelloGame {
    private final String innerText;
    private final List<Element> elements = new ArrayList<Element>();

    /**
     * Hash map.
     * <p/>
     * This is for convenience when there is only a single Element with a given tag.
     * <p/>
     * Returns the last element with a given tag.
     */
    private final Map<String, String> map = new HashMap<>();

    OthelloGame(String text) {
        innerText = text;
        final String[] parts = text.split("\\]");
        for (String part : parts) {
            final String[] keyValue = part.split("\\[");
            final Element e = new Element(keyValue);
            elements.add(e);
            map.put(e.key, e.value);
        }
    }

    /**
     * @return Game type, for instance "8" for standard 8x8 othello or "s8r20" for synchronized 8x8 othello with 20 random discs.
     */
    public String type() {
        return map.get("TY");
    }

    /**
     * @return rating of black player
     */
    public double blackRating() {
        return Double.parseDouble(map.get("RB"));
    }

    /**
     * @return rating of white player
     */
    public double whiteRating() {
        return Double.parseDouble(map.get("RW"));
    }

    /**
     *
     * @return game score, for example +2 for a win by 2.
     */
    public double score() {
        return result().score;
    }

    /**
     * The game result. See {@link Result}
     * @return the result of the game
     */
    public Result result() {
        return new Result(map.get("RE"));
    }

    /**
     * @return name of black player
     */
    public String blackName() {
        return map.get("PB");
    }

    /**
     * @return name of black player
     */
    public String whiteName() {
        return map.get("PW");
    }

    private static class Element {
        private final String key;
        private final String value;

        private Element(String[] keyValue) {
            Require.eq(keyValue.length, "key value size", 2);
            this.key = keyValue[0];
            this.value = keyValue[1];
        }
    }

    @Override public String toString() {
        return "(;" + innerText + ";)";
    }

    /**
     * The game's result, parsed into fields
     */
    public static class Result {
        final double score;
        final @Nullable String comment;

        public Result(String resultText) {
            final String[] parts = resultText.split(":", 2);
            score = Double.parseDouble(parts[0]);
            comment = parts.length == 2 ? parts[1]:null;
        }
    }
}
