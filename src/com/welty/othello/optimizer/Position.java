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

package com.welty.othello.optimizer;

/**
 * <PRE>
 * User: chris
 * Date: 3/16/11
 * Time: 2:28 PM
 * </PRE>
 */
class Position {
    final int nNodes;
    private final double staticValue;
    final double searchValue;
    final int enemyMobs;
    final int moverMobs;

    Position(String line) {
        String[] parts = line.split(",");
        moverMobs = Integer.parseInt(parts[0]);
        enemyMobs = Integer.parseInt(parts[1]);
        nNodes = Integer.parseInt(parts[2]);
        searchValue = Double.parseDouble(parts[5]);
        double s = Double.parseDouble(parts[6]);
        s = Math.max(-64, s);
        s = Math.min(64, s);
        staticValue = s;
    }

    int getStaticInt() {
        return (int) Math.round(staticValue * 0.5) * 2;
    }
}
