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

package com.welty.othello.timer.timer;

/**
 * <PRE>
* User: Chris
* Date: Jul 25, 2009
* Time: 10:54:11 AM
* </PRE>
*/
class Inputs {
    final int nCopies;
    final String machineName;
    final String processorType;

    Inputs(int nCopies, String machineName, String processorType) {
        this.nCopies = nCopies;
        this.machineName = machineName;
        this.processorType = processorType;
    }
}
