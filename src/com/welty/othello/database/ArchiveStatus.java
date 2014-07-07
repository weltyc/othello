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

import com.orbanova.common.feed.Feeds;
import com.orbanova.common.misc.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;

/**
 * Store information about which GGS archives have been processed (or processing has started)
 * <p/>
 * This information is stored in an archive file. The format of the archive file is
 * a list of ints; each int is an archive that has been sent off to a processor.
 */
public class ArchiveStatus {
    private static final Logger log = Logger.logger(ArchiveStatus.class);
    private final static int firstNumber = 143;
    private final static int lastNumber = 158;
    private static final Path saveFile = GgsDownloader.cacheLocation.resolve("ArchiveStatus.txt");
    private static ArchiveStatus instance;

    private Set<Integer> processed = new HashSet<>();

    /**
     * @return the singleton ArchiveStatus.
     */
    public synchronized static ArchiveStatus getInstance() {
        if (instance == null) {
            instance = new ArchiveStatus();
        }
        return instance;
    }

    /**
     * Read in the set of files that have already been processed.
     */
    private ArchiveStatus() {
        if (!Files.exists(saveFile)) {
            log.info("Save file doesn't exist");
        } else {
            for (String line : Feeds.ofLines(saveFile.toFile())) {
                try {
                    processed.add(Integer.parseInt(line.trim()));
                } catch (NumberFormatException e) {
                    log.warn("Error reading save file : got '" + line + "'");
                }
            }
        }
    }

    /**
     * Get the next archive number to process, and store the fact that it has been processed.
     *
     * @return the number of the next archive that needs to be processed, or 0 if all archives are complete.
     */
    public synchronized int nextArchiveNumber() {
        for (int i = firstNumber; i <= lastNumber; i++) {
            if (!processed.contains(i)) {
                processed.add(i);
                updateArchive(i);
                return i;
            }
        }

        return 0;
    }

    private synchronized void updateArchive(int i) {
        try {
            final OutputStream out = Files.newOutputStream(saveFile, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
            out.write((i + "\n").getBytes());
            out.flush();
            out.close();
            log.info("Sending next archive for processing: " + i);
        } catch (IOException e) {
            log.warn("Error writing to archive status file (archive " + i + "): " + e.getMessage());
        }
    }
}
