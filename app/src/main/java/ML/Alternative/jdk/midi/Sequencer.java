/*
 * Copyright (c) 1999, 2003, Oracle and/or its affiliates. All rights reserved.
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

import java.io.InputStream;
import java.io.IOException;
public interface Sequencer extends MidiDevice {

    public static final int LOOP_CONTINUOUSLY = -1;
    public void setSequence(Sequence sequence) throws InvalidMidiDataException;


    public void setSequence(InputStream stream) throws IOException, InvalidMidiDataException;

    public Sequence getSequence();

    public void start();

    public void stop();


    public boolean isRunning();

    public void startRecording();


    public void stopRecording();

    public boolean isRecording();

    public void recordEnable(Track track, int channel);


    public void recordDisable(Track track);


    public float getTempoInBPM();


    public void setTempoInBPM(float bpm);

    public float getTempoInMPQ();


    public void setTempoInMPQ(float mpq);

    public void setTempoFactor(float factor);


    public float getTempoFactor();

    public long getTickLength();


    public long getTickPosition();


    public void setTickPosition(long tick);


    public long getMicrosecondLength();


    public long getMicrosecondPosition();

    public void setMicrosecondPosition(long microseconds);


    public void setMasterSyncMode(SyncMode sync);


    public SyncMode getMasterSyncMode();


    public SyncMode[] getMasterSyncModes();


    public void setSlaveSyncMode(SyncMode sync);


    public SyncMode getSlaveSyncMode();


    public SyncMode[] getSlaveSyncModes();


    public void setTrackMute(int track, boolean mute);

    public boolean getTrackMute(int track);


    public void setTrackSolo(int track, boolean solo);


    public boolean getTrackSolo(int track);

    public boolean addMetaEventListener(MetaEventListener listener);

    public void removeMetaEventListener(MetaEventListener listener);

    public int[] addControllerEventListener(ControllerEventListener listener, int[] controllers);


    public int[] removeControllerEventListener(ControllerEventListener listener, int[] controllers);


    public void setLoopStartPoint(long tick);

    public long getLoopStartPoint();

    public void setLoopEndPoint(long tick);

    public long getLoopEndPoint();


    public void setLoopCount(int count);


    public int getLoopCount();

    public static class SyncMode {

        private String name;

        protected SyncMode(String name) {

            this.name = name;
        }



        public final boolean equals(Object obj) {

            return super.equals(obj);
        }


        public final int hashCode() {

            return super.hashCode();
        }


        public final String toString() {

            return name;
        }

        public static final SyncMode INTERNAL_CLOCK             = new SyncMode("Internal Clock");


        public static final SyncMode MIDI_SYNC                  = new SyncMode("MIDI Sync");

        public static final SyncMode MIDI_TIME_CODE             = new SyncMode("MIDI Time Code");


        public static final SyncMode NO_SYNC                            = new SyncMode("No Timing");

    } // class SyncMode
}
