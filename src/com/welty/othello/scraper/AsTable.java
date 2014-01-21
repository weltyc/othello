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
