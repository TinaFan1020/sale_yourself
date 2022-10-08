/*
 * Copyright (c) 1998, 2002, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package ML.Alternative.jdk.midi;


public abstract class MidiMessage implements Cloneable {

    protected byte[] data;


    protected int length = 0;


    protected MidiMessage(byte[] data) {
        this.data = data;
        if (data != null) {
            this.length = data.length;
        }
    }


    protected void setMessage(byte[] data, int length) throws InvalidMidiDataException {
        if (length < 0 || (length > 0 && length > data.length)) {
            throw new IndexOutOfBoundsException("length out of bounds: "+length);
        }
        this.length = length;

        if (this.data == null || this.data.length < this.length) {
            this.data = new byte[this.length];
        }
        System.arraycopy(data, 0, this.data, 0, length);
    }


    public byte[] getMessage() {
        byte[] returnedArray = new byte[length];
        System.arraycopy(data, 0, returnedArray, 0, length);
        return returnedArray;
    }


    public int getStatus() {
        if (length > 0) {
            return (data[0] & 0xFF);
        }
        return 0;
    }

    public int getLength() {
        return length;
    }

    public abstract Object clone();
}
