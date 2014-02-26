package com.welty.othello.protocol;

import com.welty.othello.c.CReader;
import com.welty.othello.gdk.OsMoveListItem;
import org.jetbrains.annotations.NotNull;

import java.io.EOFException;

/**
 * The response parser turns NBoard protocol text strings into NBoardResponses and gives them to the responseHandler.
 * <p/>
 * It is also responsible for turning the stateful NBoard protocol into a stateless protocol.
 * <p/>
 * This class is thread-safe. It does this by having synchronized functions. Deadlocks are easy to prevent:
 * the only time it calls an external function is responseHandler.handle(); so long as this does not block or request
 * a monitor, the function will not block or deadlock.
 */
public class ResponseParser {
    private final ResponseHandler responseHandler;
    private @NotNull String status;
    private @NotNull String name;
    private int pong;

    /**
     * @param responseHandler responseHandler to add parsed responses
     * @param name            initial name of the engine, until it is overridden by a 'set myname' response.
     */
    public ResponseParser(ResponseHandler responseHandler, @NotNull String name) {
        status = "Loading " + name;
        this.responseHandler = responseHandler;
        this.name = name;
    }

    /**
     * Parse an NBoard message and give it to the responseHandler.
     * <p/>
     * Also updates the engine-controlled shared state (ping and status)
     *
     * @param msg message to parse
     */
    public synchronized void handle(String msg) {
        final CReader in = new CReader(msg);
        final String command = in.readString();
        try {
            switch (command) {
                case "===":
                    // a move has been made
                    setStatus("");

                    // Edax produces the mli with spaces between components rather than slashes.
                    // Translate to normal form if there are spaces.
                    final String mliText = in.readLine().trim().replaceAll("\\s+", "/");
                    final OsMoveListItem mli = new OsMoveListItem(mliText);
                    responseHandler.handle(new MoveResponse(pong, mli));
                    break;
                case "pong":
                    pong = in.readInt();
                    responseHandler.handle(new PongResponse(pong));
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
                        responseHandler.handle(new NameChangedResponse());
                    } else {
                        responseHandler.handle(new ErrorResponse(msg, "Unknown variable: '" + variable + "'"));
                    }
                    break;
                case "book":
                case "search":
                    // computer giving hints
                    // search [pv] [eval] 0         [depth] [freeform text]
                    // book   [pv] [eval] [# games] [depth] [freeform text]
                    final boolean isBook = command.equals("book");
                    responseHandler.handle(HintResponse.of(pong, isBook, in));
                    break;
                case "":
                    // ignore blank lines - they are not an error.
                    break;
                case "nodestats":
                    responseHandler.handle(NodeStatsResponse.of(pong, in));
                    break;
                case "analysis":
                    responseHandler.handle(AnalysisResponse.of(pong, in));
                    break;
                default:
                    responseHandler.handle(new ErrorResponse(msg, "Unknown command: " + command));
            }
        } catch (EOFException | IllegalArgumentException e) {
            final String comment = getComment(command, e);
            responseHandler.handle(new ErrorResponse(msg, comment));
        }
    }

    private String getComment(String command, Exception e) {
        final String format;

        switch (command) {
            case "book":
                format = "book {pv} {eval} {# games:long} {depth} {freeform text:string}";
                break;
            case "pong":
                format = "pong {pong:int}";
                break;
            default:
                format = null;
        }

        if (format == null) {
            return e.getMessage();
        } else {
            return command + " command format:\n" + format;
        }

    }

    private void setStatus(@NotNull String status) {
        this.status = status;
        responseHandler.handle(new StatusChangedResponse(status));
    }

    public synchronized String getStatus() {
        return status;
    }

    public synchronized String getName() {
        return name;
    }

    public synchronized int getPong() {
        return pong;
    }

    public void engineTerminated() {
        setStatus(getName() + " has terminated");
        responseHandler.handle(new EngineTerminatedResponse());
    }
}
