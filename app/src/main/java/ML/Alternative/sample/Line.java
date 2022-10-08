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

package ML.Alternative.sample;

public interface Line extends AutoCloseable {

    public void open() throws LineUnavailableException;


    public void close();

    public boolean isOpen();


    /**
     * Obtains the set of controls associated with this line.
     * Some controls may only be available when the line is open.
     * If there are no controls, this method returns an array of length 0.
     * @return the array of controls
     * @see #getControl
     */
    //TODO control
    public Control[] getControls();

    /**
     * Indicates whether the line supports a control of the specified type.
     * Some controls may only be available when the line is open.
     * @param control the type of the control for which support is queried
     * @return <code>true</code> if at least one control of the specified type is
     * supported, otherwise <code>false</code>.
     */
    public boolean isControlSupported(Control.Type control);


    public Control getControl(Control.Type control);


    public void addLineListener(LineListener listener);


    /**
     * Removes the specified listener from this line's list of listeners.
     * @param listener listener to remove
     * @see #addLineListener
     */
    public void removeLineListener(LineListener listener);

    public static class Info {

        /**
         * The class of the line described by the info object.
         */
        private final Class lineClass;

        public Info(Class<?> lineClass) {

            if (lineClass == null) {
                this.lineClass = Line.class;
            } else {
                this.lineClass = lineClass;
            }
        }



        /**
         * Obtains the class of the line that this Line.Info object describes.
         * @return the described line's class
         */
        public Class<?> getLineClass() {
            return lineClass;
        }


        /**
         * Indicates whether the specified info object matches this one.
         * To match, the specified object must be identical to or
         * a special case of this one.  The specified info object
         * must be either an instance of the same class as this one,
         * or an instance of a sub-type of this one.  In addition, the
         * attributes of the specified object must be compatible with the
         * capabilities of this one.  Specifically, the routing configuration
         * for the specified info object must be compatible with that of this
         * one.
         * Subclasses may add other criteria to determine whether the two objects
         * match.
         *
         * @param info the info object which is being compared to this one
         * @return <code>true</code> if the specified object matches this one,
         * <code>false</code> otherwise
         */
        public boolean matches(Info info) {

            if (! (this.getClass().isInstance(info)) ) {
                return false;
            }


            // this.isAssignableFrom(that)  =>  this is same or super to that
            //                                                          =>      this is at least as general as that
            //                                                          =>      that may be subtype of this

            if (! (getLineClass().isAssignableFrom(info.getLineClass())) ) {
                return false;
            }

            return true;
        }


        /**
         * Obtains a textual description of the line info.
         * @return a string description
         */
        public String toString() {

            String fullPackagePath = "ML.alternative.";
            String initialString = new String(getLineClass().toString());
            String finalString;

            int index = initialString.indexOf(fullPackagePath);

            if (index != -1) {
                finalString = initialString.substring(0, index) + initialString.substring( (index + fullPackagePath.length()), initialString.length() );
            } else {
                finalString = initialString;
            }

            return finalString;
        }

    } // class Info

} // interface Line
