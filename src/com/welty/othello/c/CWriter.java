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

package com.welty.othello.c;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: HP_Administrator
 * Date: May 9, 2009
 * Time: 11:05:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class CWriter {
    private final PrintStream out;

    public CWriter(File file) {
        this(file, false);
    }

    public CWriter(String filename, boolean append) {
        this(new File(filename), append);
    }

    public CWriter(File file, boolean append) {
        try {
            out = new PrintStream(new BufferedOutputStream(new FileOutputStream(file, append)));
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void println(Object obj) {
        out.println(obj.toString());
        out.flush();
    }

    public void close() {
        out.close();
    }
}
