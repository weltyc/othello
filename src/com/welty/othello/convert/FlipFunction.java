package com.welty.othello.convert;

import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* <PRE>
* User: chris
* Date: 3/11/11
* Time: 9:26 AM
* </PRE>
*/
class FlipFunction {
    private long mover;
    private long empty;

    static final Pattern pattern = Pattern.compile("\tbb\\.(mover|empty)\\.u4s\\[(\\d)\\]\\^=0x([0-9a-fA-F]+);");

    /**
     * Process a line of the function.
     * @param line line of text to process
     * @param out location to write output
     * @return the function if we're still in it, else null
     */
    FlipFunction process(String line, PrintWriter out) {
        boolean done = false;

        Matcher matcher = pattern.matcher(line);
        if (matcher.matches()) {
            long mask = Long.parseLong(matcher.group(3), 16);
            mask <<= 32 * Integer.parseInt(matcher.group(2));
            if (matcher.group(1).equals("mover")) {
                mover |= mask;
            } else {
                empty |= mask;
            }
        } else {
            if (line.equals("}")) {
                out.println("\t" + "return 0x" + Long.toHexString(mover) + ";");
                done = true;
            }
            out.println(line);
        }

        return done?null:this;
    }
}
