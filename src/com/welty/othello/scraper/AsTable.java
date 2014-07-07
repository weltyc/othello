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


import com.orbanova.common.data.table.DataTables;

/**
 * Class to display the contents of an Othello log file in a table window
 * <pre>
 * User: chris
 * Date: 7/14/11
 * Time: 2:57 PM
 * </pre>
 */
public class AsTable {
    /**
     * Display contents of an Othello log file in a table window
     */
    public static void main(String[] args) {
        final String filename = "c:/dev/oth1/log.txt";
        DataTables.ofPojos(Move.class, Move.fileFeed(filename)).showTable("Moves");
    }
}
