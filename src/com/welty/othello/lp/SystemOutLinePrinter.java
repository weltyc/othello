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

package com.welty.othello.lp;

/**
 * Created by IntelliJ IDEA.
* User: HP_Administrator
* Date: Jun 21, 2009
* Time: 11:09:52 PM
* To change this template use File | Settings | File Templates.
*/
public class SystemOutLinePrinter extends PrintStreamLinePrinter {
    public SystemOutLinePrinter() {
        super(System.out);
    }
}

