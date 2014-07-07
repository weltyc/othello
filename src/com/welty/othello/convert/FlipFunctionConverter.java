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
