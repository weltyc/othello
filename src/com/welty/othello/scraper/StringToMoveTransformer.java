package com.welty.othello.scraper;

import com.orbanova.common.feed.Feed;
import com.orbanova.common.feed.Transformer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses lines of an NTest log file and emits Moves.
 */
class StringToMoveTransformer implements Transformer<String, Move> {
    private static final Pattern pattern = Pattern.compile(" (\\d) (.*) \\d.*");
    private static final Pattern moverPattern = Pattern.compile("(White|Black) to move.*");

    public static final StringToMoveTransformer INSTANCE = new StringToMoveTransformer();

    /**
     * Use StringToMoveCombiner.instance instead
     */
    private StringToMoveTransformer() {
    }

    /**
     * Slurps in a Move from the feed, and returns once it's got all the data.
     *
     * @param lines data from Othello log file
     * @return Move
     */
    public Move next(@NotNull Feed<? extends String> lines) {
        final Map<Integer, String> rows = new HashMap<Integer, String>();
        boolean blackToMove = false;
        boolean isInitialPosition = false;

        for (String line; null!=(line=lines.next()); ) {
            final Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                int rowId = Integer.parseInt(matcher.group(1));
                rows.put(rowId, matcher.group(2));
            }
            final Matcher moverMatcher = moverPattern.matcher(line);
            if (moverMatcher.matches()) {
                blackToMove = moverMatcher.group(1).equals("Black");
            }
            if (line.contains("extending")) {
                isInitialPosition=true;
            }
            final Status status = Status.create(line);
            if (status != null) {
                return new Move(new Board(rows, blackToMove), status, isInitialPosition);
            }
        }
        return null;
    }
}
