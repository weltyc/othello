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

package com.welty.othello.timer.gobbler;

import java.io.Closeable;
import java.io.InputStream;

/**
 * <PRE>
 * User: Chris
 * Date: Jul 24, 2009
 * Time: 11:06:08 PM
 * </PRE>
 */
public abstract class AbstractStreamGobbler extends Thread implements Closeable {
    InputStream is;

    AbstractStreamGobbler(InputStream is) {
        this.is = is;
    }
}
