/*
 * Copyright (c) 1999, 2010, Oracle and/or its affiliates. All rights reserved.
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

import java.util.List;


public interface MidiDevice extends AutoCloseable {


    public Info getDeviceInfo();
    public void open() throws MidiUnavailableException;


    public void close();

    public boolean isOpen();

    public long getMicrosecondPosition();


    public int getMaxReceivers();


    public int getMaxTransmitters();

    public Receiver getReceiver() throws MidiUnavailableException;


    List<Receiver> getReceivers();


    public Transmitter getTransmitter() throws MidiUnavailableException;

    List<Transmitter> getTransmitters();


    public static class Info {

        private String name;

        private String vendor;

        private String description;

        private String version;


        protected Info(String name, String vendor, String description, String version) {

            this.name = name;
            this.vendor = vendor;
            this.description = description;
            this.version = version;
        }


        public final boolean equals(Object obj) {
            return super.equals(obj);
        }

        public final int hashCode() {
            return super.hashCode();
        }


        public final String getName() {
            return name;
        }


        public final String getVendor() {
            return vendor;
        }

        public final String getDescription() {
            return description;
        }


        public final String getVersion() {
            return version;
        }


        public final String toString() {
            return name;
        }
    }


}
