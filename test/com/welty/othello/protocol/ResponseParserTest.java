package com.welty.othello.protocol;

import com.welty.othello.gdk.OsMoveListItem;
import junit.framework.TestCase;
import org.mockito.Mockito;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class ResponseParserTest extends TestCase {
    private ResponseHandler responseHandler;
    private ResponseParser parser;

    @Override protected void setUp() throws Exception {
        //noinspection unchecked
        responseHandler = Mockito.mock(ResponseHandler.class);
        parser = new ResponseParser(responseHandler, "test");
    }

    public void testParsePong() {
        parser.handle("pong 1");
        verify(responseHandler).handle(new PongResponse(1));
    }

    public void testParsePongError() {
        final String msg = "pong";
        testErrorResponse(msg, "pong command format:\npong {pong:int}");
    }

    public void testParsePongError2() {
        testErrorResponse("pong b", "pong command format:\npong {pong:int}");
    }

    public void testParseStatus() {
        parser.handle("status foo");
        verify(responseHandler).handle(new StatusChangedResponse());
        assertEquals("foo", parser.getStatus());
    }

    public void testParseEmptyStatus() {
        parser.handle("status");
        verify(responseHandler).handle(new StatusChangedResponse());
        assertEquals("", parser.getStatus());
    }

    public void testParseWhitespaceStatus() {
        parser.handle("status ");
        verify(responseHandler).handle(new StatusChangedResponse());
        assertEquals("", parser.getStatus());
    }

    public void testSetMyName() {
        parser.handle("set myname q");
        verify(responseHandler).handle(new NameChangedResponse());
        assertEquals("q", parser.getName());
    }

    public void testInvalidSetVariable() {
        testErrorResponse("set q myname", "Unknown variable: 'q'");
    }

    public void testBlankLine() {
        // not an error, not a message, not anything
        parser.handle("");
        verifyNoMoreInteractions(responseHandler);
    }

    public void testMoveResponse() {
        parser.handle("=== F5");
        final MoveResponse expected = new MoveResponse(0, new OsMoveListItem("F5"));
        verify(responseHandler).handle(expected);
    }

    public void testPongUpdating() {
        parser.handle("pong 13");
        parser.handle("=== F5");
        verify(responseHandler).handle(new PongResponse(13));
        verify(responseHandler).handle(new StatusChangedResponse());
        verify(responseHandler).handle(new MoveResponse(13, new OsMoveListItem("F5")));
    }

    public void testBookError() {
        // this comes from Edax 4.4 occasionally
        testErrorResponse("book    +1                                          d3",
                "book command format:\nbook {pv} {eval} {# games:long} {depth} {freeform text:string}");
    }

    public void testSearchHint() {
        parser.handle("search E3       4 0 3");
        verify(responseHandler).handle(new HintResponse(0, false, "E3", new Value(4), 0, new Depth(3), ""));
        //                        // search [pv] [eval] 0         [depth] [freeform text]
//        parser.parse()
    }

    public void testNodestats() {
        parser.handle("nodestats 24 0.00");
        verify(responseHandler).handle(new NodeStatsResponse(0, 24, 0.0));
    }

    private void testErrorResponse(String msg, String comment) {
        parser.handle(msg);
        verify(responseHandler).handle(new ErrorResponse(msg, comment));
    }
}
