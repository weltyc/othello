package com.welty.othello.protocol;

import com.welty.othello.c.CReader;
import com.welty.othello.gdk.OsMoveListItem;

import java.io.EOFException;
import java.util.Queue;

/**
 * The response parser turns NBoard protocol text strings into NBoardResponses and puts them on a queue.
 * <p/>
 * It is also responsible for turning the stateful NBoard protocol into a stateless protocol.
 * <p/>
 * This class is thread-safe. It does this by having synchronized functions. Deadlocks are easy to prevent:
 * the only time it calls an external function is queue.add(); so long as this does not block, the function will
 * not block or deadlock.
 */
public class ResponseParser {
    private final Queue<NBoardResponse> queue;
    private String status;
    private String name;
    private int pong;

    public ResponseParser(Queue<NBoardResponse> queue) {
        this.queue = queue;
    }

    /**
     * Parse an NBoard message and add it to the queue.
     * <p/>
     * Also updates the engine-controlled shared state (ping and status)
     *
     * @param msg message to parse
     */
    public synchronized void parse(String msg) {
        final CReader in = new CReader(msg);
        final String command = in.readString();
        try {
            switch (command) {
//                    case "book":
//                    case "search":
//                        // computer giving hints
//                        // search [pv] [eval] 0         [depth] [freeform text]
//                        // book   [pv] [eval] [# games] [depth] [freeform text]
//                        final boolean isBook = sCommand.equals("book");
//
//                        final String pv = is.readString();
//                        final CMove move;
//                        try {
//                            move = new CMove(pv.substring(0, 2));
//                        } catch (IllegalArgumentException e) {
//                            throw new IllegalArgumentException("Can't create move from first two characters of pv (" + pv + ")");
//                        }
//                        final String eval = is.readString();
//                        final int nGames = is.readInt();
//                        final String depth = is.readString();
//                        final String freeformText = is.readLine();
//                        fireHint(m_pong, isBook, pv, move, eval, nGames, depth, freeformText);
//                        break;
//                    case "learn":
//                        setStatus("");
//                        break;
//\
                case "===":
//                    queue.add(new )
                    setStatus("");
                    // now update the move list

                    // Edax produces the mli with spaces between components rather than slashes.
                    // Translate to normal form if there are spaces.
                    final String mliText = in.readLine().trim().replaceAll("\\s+", "/");
                    final OsMoveListItem mli = new OsMoveListItem(mliText);
                    queue.add(new MoveResponse(pong, mli));
                    break;
                case "pong":
                    pong = in.readInt();
                    queue.add(new PongResponse(pong));
                    break;
                case "status":
                    in.ignoreWhitespace();
                    final String status = in.readLine();
                    setStatus(status);
                    break;
                case "set":
                    String variable = in.readString();
                    if (variable.equals("myname")) {
                        name = in.readString();
                        queue.add(new NameChangedResponse());
                    } else {
                        queue.add(new ErrorResponse(msg));
                    }
                    break;
                case "book":
                case "search":
                    // computer giving hints
                    // search [pv] [eval] 0         [depth] [freeform text]
                    // book   [pv] [eval] [# games] [depth] [freeform text]
                    final boolean isBook = command.equals("book");
                    queue.add(HintResponse.of(pong, isBook, in));
                    break;
                case "":
                    // ignore blank lines - they are not an error.
                    break;
                case "nodestats":
                    queue.add(NodeStatsResponse.of(pong, in));
                default:
                    queue.add(new ErrorResponse(msg));
            }
        } catch (EOFException | IllegalArgumentException e) {
            queue.add(new ErrorResponse(msg));
        }
    }

    private void setStatus(String status) {
        this.status = status;
        queue.add(new StatusChangedResponse());
    }

    public synchronized String getStatus() {
        return status;
    }

    public synchronized String getName() {
        return name;
    }
}
