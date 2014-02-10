package com.welty.othello.protocol;

import com.welty.othello.gdk.OsMoveListItem;
import junit.framework.TestCase;
import org.mockito.Mockito;

import java.util.Queue;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class ResponseParserTest extends TestCase {
    private Queue<NBoardResponse> queue;
    private ResponseParser parser;

    @Override protected void setUp() throws Exception {
        //noinspection unchecked
        queue = (Queue<NBoardResponse>) Mockito.mock(Queue.class);
        parser = new ResponseParser(queue);
    }

    public void testParsePong() {
        parser.parse("pong 1");
        verify(queue).add(new PongResponse(1));
    }

    public void testParsePongError() {
        final String msg = "pong";
        testErrorResponse(msg);
    }

    public void testParsePongError2() {
        testErrorResponse("pong b");
    }

    public void testParseStatus() {
        parser.parse("status foo");
        verify(queue).add(new StatusChangedResponse());
        assertEquals("foo", parser.getStatus());
    }

    public void testParseEmptyStatus() {
        parser.parse("status");
        verify(queue).add(new StatusChangedResponse());
        assertEquals("", parser.getStatus());
    }

    public void testParseWhitespaceStatus() {
        parser.parse("status ");
        verify(queue).add(new StatusChangedResponse());
        assertEquals("", parser.getStatus());
    }

    public void testSetMyName() {
        parser.parse("set myname q");
        verify(queue).add(new NameChangedResponse());
        assertEquals("q", parser.getName());
    }

    public void testInvalidSetVariable() {
        testErrorResponse("set q myname");
    }

    public void testBlankLine() {
        // not an error, not a message, not anything
        parser.parse("");
        verifyNoMoreInteractions(queue);
    }

    public void testMoveResponse() {
        parser.parse("=== F5");
        final MoveResponse expected = new MoveResponse(0, new OsMoveListItem("F5"));
        verify(queue).add(expected);
    }

    public void testPongUpdating() {
        parser.parse("pong 13");
        parser.parse("=== F5");
        verify(queue).add(new PongResponse(13));
        verify(queue).add(new StatusChangedResponse());
        verify(queue).add(new MoveResponse(13, new OsMoveListItem("F5")));
    }

    public void testBookError() {
        // this comes from Edax 4.4 occasionally
        testErrorResponse("book    +1                                          d3");
    }

    public void testSearchHint() {
        parser.parse("search E3       4 0 3");
        verify(queue).add(new HintResponse(0, false, "E3", 4, 0, "3", ""));
        //                        // search [pv] [eval] 0         [depth] [freeform text]
//        parser.parse()
    }

    public void testNodestats() {
        parser.parse("nodestats 24 0.00");
        verify(queue).add(new NodeStatsResponse(0, 24, 0.0));
    }

    private void testErrorResponse(String msg) {
        parser.parse(msg);
        verify(queue).add(new ErrorResponse(msg));
    }
}
