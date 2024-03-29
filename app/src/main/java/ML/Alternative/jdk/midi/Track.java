/*
 * Copyright (c) 1999, 2006, Oracle and/or its affiliates. All rights reserved.
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

import java.util.ArrayList;
import java.util.HashSet;
import ML.Alternative.jdk.MidiUtils;

public class Track {


    // the list containing the events
    private ArrayList eventsList = new ArrayList();

    // use a hashset to detect duplicate events in add(MidiEvent)
    private HashSet set = new HashSet();

    private MidiEvent eotEvent;


    /**
     * Package-private constructor.  Constructs a new, empty Track object,
     * which initially contains one event, the meta-event End of Track.
     */
    Track() {
        // start with the end of track event
        MetaMessage eot = new ImmutableEndOfTrack();
        eotEvent = new MidiEvent(eot, 0);

        eventsList.add(eotEvent);
        set.add(eotEvent);
    }

    public boolean add(MidiEvent event) {
        if (event == null) {
            return false;
        }
        synchronized(eventsList) {

            if (!set.contains(event)) {
                int eventsCount = eventsList.size();

                // get the last event
                MidiEvent lastEvent = null;
                if (eventsCount > 0) {
                    lastEvent = (MidiEvent) eventsList.get(eventsCount - 1);
                }
                // sanity check that we have a correct end-of-track
                if (lastEvent != eotEvent) {
                    // if there is no eot event, add our immutable instance again
                    if (lastEvent != null) {
                        // set eotEvent's tick to the last tick of the track
                        eotEvent.setTick(lastEvent.getTick());
                    } else {
                        // if the events list is empty, just set the tick to 0
                        eotEvent.setTick(0);
                    }
                    // we needn't check for a duplicate of eotEvent in "eventsList",
                    // since then it would appear in the set.
                    eventsList.add(eotEvent);
                    set.add(eotEvent);
                    eventsCount = eventsList.size();
                }

                // first see if we are trying to add
                // and endoftrack event.
                if (MidiUtils.isMetaEndOfTrack(event.getMessage())) {
                    // since end of track event is useful
                    // for delays at the end of a track, we want to keep
                    // the tick value requested here if it is greater
                    // than the one on the eot we are maintaining.
                    // Otherwise, we only want a single eot event, so ignore.
                    if (event.getTick() > eotEvent.getTick()) {
                        eotEvent.setTick(event.getTick());
                    }
                    return true;
                }

                // prevent duplicates
                set.add(event);

                // insert event such that events is sorted in increasing
                // tick order
                int i = eventsCount;
                for ( ; i > 0; i--) {
                    if (event.getTick() >= ((MidiEvent)eventsList.get(i-1)).getTick()) {
                        break;
                    }
                }
                if (i == eventsCount) {
                    // we're adding an event after the
                    // tick value of our eot, so push the eot out.
                    // Always add at the end for better performance:
                    // this saves all the checks and arraycopy when inserting

                    // overwrite eot with new event
                    eventsList.set(eventsCount - 1, event);
                    // set new time of eot, if necessary
                    if (eotEvent.getTick() < event.getTick()) {
                        eotEvent.setTick(event.getTick());
                    }
                    // add eot again at the end
                    eventsList.add(eotEvent);
                } else {
                    eventsList.add(i, event);
                }
                return true;
            }
        }

        return false;
    }


    /**
     * Removes the specified event from the track.
     * @param event the event to remove
     * @return <code>true</code> if the event existed in the track and was removed,
     * otherwise <code>false</code>
     */
    public boolean remove(MidiEvent event) {

        // this implementation allows removing the EOT event.
        // pretty bad, but would probably be too risky to
        // change behavior now, in case someone does tricks like:
        //
        // while (track.size() > 0) track.remove(track.get(track.size() - 1));

        // also, would it make sense to adjust the EOT's time
        // to the last event, if the last non-EOT event is removed?
        // Or: document that the ticks() length will not be reduced
        // by deleting events (unless the EOT event is removed)
        synchronized(eventsList) {
            if (set.remove(event)) {
                int i = eventsList.indexOf(event);
                if (i >= 0) {
                    eventsList.remove(i);
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Obtains the event at the specified index.
     * @param index the location of the desired event in the event vector
     * @throws <code>ArrayIndexOutOfBoundsException</code>  if the
     * specified index is negative or not less than the current size of
     * this track.
     * @see #size
     */
    public MidiEvent get(int index) throws ArrayIndexOutOfBoundsException {
        try {
            synchronized(eventsList) {
                return (MidiEvent)eventsList.get(index);
            }
        } catch (IndexOutOfBoundsException ioobe) {
            throw new ArrayIndexOutOfBoundsException(ioobe.getMessage());
        }
    }


    /**
     * Obtains the number of events in this track.
     * @return the size of the track's event vector
     */
    public int size() {
        synchronized(eventsList) {
            return eventsList.size();
        }
    }


    /**
     * Obtains the length of the track, expressed in MIDI ticks.  (The
     * duration of a tick in seconds is determined by the timing resolution
     * of the <code>Sequence</code> containing this track, and also by
     * the tempo of the music as set by the sequencer.)
     * @return the duration, in ticks
     * @see Sequence#Sequence(float, int)
     * @see Sequencer#setTempoInBPM(float)
     * @see Sequencer#getTickPosition()
     */
    public long ticks() {
        long ret = 0;
        synchronized (eventsList) {
            if (eventsList.size() > 0) {
                ret = ((MidiEvent)eventsList.get(eventsList.size() - 1)).getTick();
            }
        }
        return ret;
    }

    private static class ImmutableEndOfTrack extends MetaMessage {
        private ImmutableEndOfTrack() {
            super(new byte[3]);
            data[0] = (byte) META;
            data[1] = MidiUtils.META_END_OF_TRACK_TYPE;
            data[2] = 0;
        }

        public void setMessage(int type, byte[] data, int length) throws InvalidMidiDataException {
            throw new InvalidMidiDataException("cannot modify end of track message");
        }
    }

}
