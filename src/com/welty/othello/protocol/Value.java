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

package com.welty.othello.protocol;

import com.orbanova.common.misc.Require;
import lombok.EqualsAndHashCode;

import java.text.DecimalFormat;

@EqualsAndHashCode
public final class Value {
    /**
     * Value if draws count as wins for the player to move.
     * <p/>
     * This value may be NaN if no value was produced.
     */
    public final float drawSeekingValue;

    /**
     * Value if draws count as losses for the player to move.
     * <p/>
     * This value may be NaN if no value was produced.
     */
    public final float drawAvoidingValue;

    public Value(float value) {
        this(value, value);
    }

    public Value(float drawSeekingValue, float drawAvoidingValue) {
        this.drawSeekingValue = drawSeekingValue;
        this.drawAvoidingValue = drawAvoidingValue;
        validate();
    }

    /**
     * Construct a Value using the text format used by the NBoard protocol
     *
     * @param text {decimal} or {decimal,decimal}
     */
    public Value(String text) {
        final String[] parts = text.split(",");
        if (parts.length > 2) {
            throw new IllegalArgumentException("Value must have no more than 2 components but had " + parts.length + ": " + text);
        }
        drawSeekingValue = Float.parseFloat(parts[0]);
        drawAvoidingValue = parts.length == 1 ? drawSeekingValue : Float.parseFloat(parts[1]);
        validate();
    }

    private void validate() {
        if (!Float.isNaN(drawAvoidingValue)) {
            Require.leq(drawAvoidingValue, "draw seeking value", drawSeekingValue, "draw avoiding value");
        }
    }

    public String toString() {
        if (drawSeekingValue == drawAvoidingValue) {
            return format(drawSeekingValue);
        } else {
            return format(drawSeekingValue) + "," + format(drawAvoidingValue);
        }
    }

    private static String format(float v) {
        return String.format("%.2f", v);
    }
}
