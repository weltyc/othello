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

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Class representing the position on an othello board, including side-to-move.
 *
 * <pre>
 * User: chris
 * Date: 7/7/11
 * Time: 3:26 PM
 * </pre>
 */
class Board implements Comparable<Board> {
    private static final String header = "  A B C D E F G H  ";

    private final @NotNull Map<Integer, String> rows;
    private final boolean blackToMove;

    public Board(Map<Integer, String> rows, boolean blackToMove) {
        this.rows = rows;
        this.blackToMove = blackToMove;
    }

    public String toScrZebra() {
        StringBuilder sb = new StringBuilder();

        for (int j=1; j<=8; j++) {
            final String rowText = rows.get(j);
            for (int k = 0; k<8; k++) {
                char c = rowText.charAt(2*k);
                final char d = solverChar(c, blackToMove);
                sb.append(d);
            }
        }
        return sb.toString();
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(header).append("\n");
        for (int j=1; j<=8; j++) {
            final String rowText = rows.get(j);
            sb.append(j).append(" ").append(rowText).append(" ").append(j).append("\n");
        }
        sb.append(header).append("\n");
        return sb.toString();
    }

    private static char solverChar(char c, boolean blackToMove) {
        final char d;
        // solver positions, by convention, have white to move
        // but at 18 empties, game positions have black to move
        // so switch the colors around here
        switch(c) {
            case '-':
                d = '.';
                break;
            case 'O':
                d=blackToMove?'b':'w';
                break;
            case '*':
                d = blackToMove?'w':'b';
                break;
            default:
                throw new IllegalArgumentException("Unknown piece char : " + c);
        }
        return d;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Board board = (Board) o;

        return blackToMove == board.blackToMove && rows.equals(board.rows);

    }

    @Override
    public int hashCode() {
        int result = rows.hashCode();
        result = 31 * result + (blackToMove ? 1 : 0);
        return result;
    }

    public int compareTo(Board o) {
        return toString().compareTo(o.toString());
    }
}
