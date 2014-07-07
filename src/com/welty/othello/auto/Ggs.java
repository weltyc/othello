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

package com.welty.othello.auto;

import com.orbanova.common.feed.Feeds;
import com.orbanova.common.misc.Logger;
import com.orbanova.common.os.Processor;
import com.welty.othello.database.ArchiveStatus;
import com.welty.othello.database.GgsDownloader;
import org.apache.commons.compress.compressors.CompressorException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 */
public class Ggs {
    private static final Logger log = Logger.logger(Ggs.class);

    /**
     * Automatically download ggs game files and add them to book.
     * <p/>
     * This controls a single instance of ntest, giving it a set of games to add
     * and monitoring it for completeness.
     */
    public static void main(String[] args) {
        final NTest n = new NTest();
        //noinspection StatementWithEmptyBody
        while (n.run().wasSuccessful() && n.downloadNextGames()) ;
    }

    /**
     * An ntest instance.
     * <p/>
     * An 'instance' is in 1-1 correspondence to a directory. At any time there may be 0 or 1 processes
     * running in the directory. After a process ends a new process may be started.
     * <p/>
     * This class does not ensure that only one process is running in the directory; the calling function should only
     * call run() after the previous call to run() has returned.
     */
    static class NTest {
        final String directory = "c:/dev/oth2";
        final Path gamesPath = Paths.get(directory, "games.ggf");
        final File analysisFile = new File(directory, "analysis.txt");

        /**
         * Start a new ntest process in the directory and return when it is complete.
         * <p/>
         * If 'games.ggf' does not exist, it obtains the next games.ggf from the downloader.
         * The process merges in an external book, then adds games.ggf to book, then deletes games.ggf.
         * <p/>
         * Deleting games.ggf signals that adding it to book is complete.
         * If ntest does not appear to have completely added games.ggf to book, then this function will not delete it.
         */
        public @NotNull RunResult run() {
            if (!Files.exists(gamesPath)) {
                downloadNextGames();
            }
            final String[] command = {"cmd", "/c", directory + "/run.bat"};
            log.info("Starting ntest");
            try {
                new Processor(command).waitFor();
                log.info("Ntest batch file completed");
            } catch (IOException e) {
                log.warn("Ntest batch file failed to start: " + e.getMessage());
                return new RunResult(e.getMessage());
            }

            if (!analysisComplete()) {
                return new RunResult(false, "Analysis does not appear complete. Is the book corrupt?");
            }

            try {
                Files.delete(gamesPath);
                return new RunResult();
            } catch (IOException e) {
                log.warn("Ntest failed after completion: " + e.getMessage());
                return new RunResult(e.getMessage());
            }
        }

        private boolean analysisComplete() {
            final List<String> lines = Feeds.ofLines(analysisFile).grep("Analysis Complete").asList();
            return !lines.isEmpty();
        }

        /**
         * Download the next game archive to be processed
         *
         * @return true if there is another game archive to process, or false if all archives are complete
         */
        public boolean downloadNextGames() {
            final int archiveFileNumber = ArchiveStatus.getInstance().nextArchiveNumber();
            if (archiveFileNumber != 0) {
                try {
                    GgsDownloader.download(archiveFileNumber, gamesPath);
                } catch (IOException | CompressorException e) {
                    // no idea what causes this, so no idea how to handle it. Just terminate the program for now.
                    throw new RuntimeException(e);
                }
            }
            return archiveFileNumber != 0;
        }
    }

    static class RunResult {
        final @Nullable String problem;
        final boolean successful;

        RunResult() {
            this(true, null);
        }

        RunResult(@Nullable String problem) {
            this(true, problem);
        }

        RunResult(boolean successful, @Nullable String problem) {
            this.successful = successful;
            this.problem = problem;
        }

        @Override public String toString() {
            return problem == null ? "" : " " + problem;
        }

        public boolean wasSuccessful() {
            return problem == null;
        }
    }

}
