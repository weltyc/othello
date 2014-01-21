package com.welty.othello.convert;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <PRE>
 * User: chris
 * Date: 3/9/11
 * Time: 4:51 PM
 * </PRE>
 */
public class FlipFunctionConverter {
    // void FlipFunctionBBWhite1() {
    private static final Pattern headerPattern = Pattern.compile("void (FlipFunctionBB.*)\\(\\) \\{");

    /**
     * Convert original ntest's flip functions to 64-bit.
     *
     * <p/> Inputs C code from file flipfuncBB.cpp; Outputs C Code to file flipFunc64.cpp
     *
     * @throws IOException if error reading or writing to files
     */
    public static void main(String[] args) throws IOException {
        final File dir = new File("C:/dev/n64/src/src/pattern");
        final File inFile = new File(dir, "flipfuncBB.cpp");
        final File outFile = new File(dir, "flipFunc64.cpp");
        final BufferedReader in = new BufferedReader(new FileReader(inFile));
        final PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outFile)));


        FlipFunction flipFunction = null;

        String line;
        while (null != (line = in.readLine())) {
            Matcher headerMatcher = headerPattern.matcher(line);
            if (headerMatcher.matches()) {
                out.println("u64 " + headerMatcher.group(1) + "(TConfig* configs) {");
                flipFunction = new FlipFunction();
            } else if (flipFunction != null) {
                flipFunction = flipFunction.process(line, out);
            } else {
                out.println(line);
            }
        }

        in.close();
        out.close();
    }
}
