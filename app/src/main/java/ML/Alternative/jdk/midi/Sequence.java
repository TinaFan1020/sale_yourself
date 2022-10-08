/*
 * Copyright (c) 1999, 2004, Oracle and/or its affiliates. All rights reserved.
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

import java.util.Vector;
import ML.Alternative.jdk.MidiUtils;

public class Sequence {


    // Timing types

    public static final float PPQ                                                       = 0.0f;

    public static final float SMPTE_24                                          = 24.0f;

    public static final float SMPTE_25                                          = 25.0f;

    public static final float SMPTE_30DROP                                      = 29.97f;

    public static final float SMPTE_30                                          = 30.0f;


    protected float divisionType;

    protected int resolution;
    protected Vector<Track> tracks = new Vector<Track>();

    public Sequence(float divisionType, int resolution) throws InvalidMidiDataException {

        if (divisionType == PPQ)
            this.divisionType = PPQ;
        else if (divisionType == SMPTE_24)
            this.divisionType = SMPTE_24;
        else if (divisionType == SMPTE_25)
            this.divisionType = SMPTE_25;
        else if (divisionType == SMPTE_30DROP)
            this.divisionType = SMPTE_30DROP;
        else if (divisionType == SMPTE_30)
            this.divisionType = SMPTE_30;
        else throw new InvalidMidiDataException("Unsupported division type: " + divisionType);

        this.resolution = resolution;
    }

    public Sequence(float divisionType, int resolution, int numTracks) throws InvalidMidiDataException {

        if (divisionType == PPQ)
            this.divisionType = PPQ;
        else if (divisionType == SMPTE_24)
            this.divisionType = SMPTE_24;
        else if (divisionType == SMPTE_25)
            this.divisionType = SMPTE_25;
        else if (divisionType == SMPTE_30DROP)
            this.divisionType = SMPTE_30DROP;
        else if (divisionType == SMPTE_30)
            this.divisionType = SMPTE_30;
        else throw new InvalidMidiDataException("Unsupported division type: " + divisionType);

        this.resolution = resolution;

        for (int i = 0; i < numTracks; i++) {
            tracks.addElement(new Track());
        }
    }

    public float getDivisionType() {
        return divisionType;
    }

    public int getResolution() {
        return resolution;
    }


    public Track createTrack() {

        Track track = new Track();
        tracks.addElement(track);

        return track;
    }


    public boolean deleteTrack(Track track) {

        synchronized(tracks) {

            return tracks.removeElement(track);
        }
    }


    public Track[] getTracks() {

        return (Track[]) tracks.toArray(new Track[tracks.size()]);
    }


    public long getMicrosecondLength() {
        return ML.Alternative.jdk.MidiUtils.tick2microsecond(this, getTickLength(), null);
    }


    public long getTickLength() {

        long length = 0;

        synchronized(tracks) {

            for(int i=0; i<tracks.size(); i++ ) {
                long temp = ((Track)tracks.elementAt(i)).ticks();
                if( temp>length ) {
                    length = temp;
                }
            }
            return length;
        }
    }

    public Patch[] getPatchList() {

        // $$kk: 04.09.99: need to implement!!
        return new Patch[0];
    }
}
