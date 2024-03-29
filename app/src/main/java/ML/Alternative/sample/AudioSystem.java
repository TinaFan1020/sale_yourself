/*
 * Copyright (c) 1999, 2011, Oracle and/or its affiliates. All rights reserved.
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

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.ArrayList;

import ML.Alternative.sample.spi.AudioFileWriter;
import ML.Alternative.sample.spi.AudioFileReader;
import ML.Alternative.sample.spi.FormatConversionProvider;
import ML.Alternative.sample.spi.MixerProvider;
//TODO jdk
import ML.Alternative.jdk.JDK13Services;




public class AudioSystem {

    /**
     * An integer that stands for an unknown numeric value.
     * This value is appropriate only for signed quantities that do not
     * normally take negative values.  Examples include file sizes, frame
     * sizes, buffer sizes, and sample rates.
     * A number of Java Sound constructors accept
     * a value of <code>NOT_SPECIFIED</code> for such parameters.  Other
     * methods may also accept or return this value, as documented.
     */
    public static final int NOT_SPECIFIED = -1;

    /**
     * Private no-args constructor for ensuring against instantiation.
     */
    private AudioSystem() {
    }


    /**
     * Obtains an array of mixer info objects that represents
     * the set of audio mixers that are currently installed on the system.
     * @return an array of info objects for the currently installed mixers.  If no mixers
     * are available on the system, an array of length 0 is returned.
     * @see #getMixer
     */
    public static Mixer.Info[] getMixerInfo() {

        List infos = getMixerInfoList();
        Mixer.Info[] allInfos = (Mixer.Info[]) infos.toArray(new Mixer.Info[infos.size()]);
        return allInfos;
    }


    /**
     * Obtains the requested audio mixer.
     * @param info a <code>Mixer.Info</code> object representing the desired
     * mixer, or <code>null</code> for the system default mixer
     * @return the requested mixer
     * @throws SecurityException if the requested mixer
     * is unavailable because of security restrictions
     * @throws IllegalArgumentException if the info object does not represent
     * a mixer installed on the system
     * @see #getMixerInfo
     */
    public static Mixer getMixer(Mixer.Info info) {

        Mixer mixer = null;
        List providers = getMixerProviders();

        for(int i = 0; i < providers.size(); i++ ) {

            try {
                return ((MixerProvider)providers.get(i)).getMixer(info);

            } catch (IllegalArgumentException e) {
            } catch (NullPointerException e) {
                // $$jb 08.20.99:  If the strings in the info object aren't
                // set, then Netscape (using jdk1.1.5) tends to throw
                // NPE's when doing some string manipulation.  This is
                // probably not the best fix, but is solves the problem
                // of the NPE in Netscape using local classes
                // $$jb 11.01.99: Replacing this patch.
            }
        }

        //$$fb if looking for default mixer, and not found yet, add a round of looking
        if (info == null) {
            for(int i = 0; i < providers.size(); i++ ) {
                try {
                    MixerProvider provider = (MixerProvider) providers.get(i);
                    Mixer.Info[] infos = provider.getMixerInfo();
                    // start from 0 to last device (do not reverse this order)
                    for (int ii = 0; ii < infos.length; ii++) {
                        try {
                            return provider.getMixer(infos[ii]);
                        } catch (IllegalArgumentException e) {
                            // this is not a good default device :)
                        }
                    }
                } catch (IllegalArgumentException e) {
                } catch (NullPointerException e) {
                }
            }
        }


        throw new IllegalArgumentException("Mixer not supported: "
                                           + (info!=null?info.toString():"null"));
    }


    //$$fb 2002-11-26: fix for 4757930: DOC: AudioSystem.getTarget/SourceLineInfo() is ambiguous

    public static Line.Info[] getSourceLineInfo(Line.Info info) {

        Vector vector = new Vector();
        Line.Info[] currentInfoArray;

        Mixer mixer;
        Line.Info fullInfo = null;
        Mixer.Info[] infoArray = getMixerInfo();

        for (int i = 0; i < infoArray.length; i++) {

            mixer = getMixer(infoArray[i]);

            currentInfoArray = mixer.getSourceLineInfo(info);
            for (int j = 0; j < currentInfoArray.length; j++) {
                vector.addElement(currentInfoArray[j]);
            }
        }

        Line.Info[] returnedArray = new Line.Info[vector.size()];

        for (int i = 0; i < returnedArray.length; i++) {
            returnedArray[i] = (Line.Info)vector.get(i);
        }

        return returnedArray;
    }



    public static Line.Info[] getTargetLineInfo(Line.Info info) {

        Vector vector = new Vector();
        Line.Info[] currentInfoArray;

        Mixer mixer;
        Line.Info fullInfo = null;
        Mixer.Info[] infoArray = getMixerInfo();

        for (int i = 0; i < infoArray.length; i++) {

            mixer = getMixer(infoArray[i]);

            currentInfoArray = mixer.getTargetLineInfo(info);
            for (int j = 0; j < currentInfoArray.length; j++) {
                vector.addElement(currentInfoArray[j]);
            }
        }

        Line.Info[] returnedArray = new Line.Info[vector.size()];

        for (int i = 0; i < returnedArray.length; i++) {
            returnedArray[i] = (Line.Info)vector.get(i);
        }

        return returnedArray;
    }


    public static boolean isLineSupported(Line.Info info) {

        Mixer mixer;
        Mixer.Info[] infoArray = getMixerInfo();

        for (int i = 0; i < infoArray.length; i++) {

            if( infoArray[i] != null ) {
                mixer = getMixer(infoArray[i]);
                if (mixer.isLineSupported(info)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static Line getLine(Line.Info info) throws LineUnavailableException {
        LineUnavailableException lue = null;
        List providers = getMixerProviders();


        // 1: try from default mixer for this line class
        try {
            Mixer mixer = getDefaultMixer(providers, info);
            if (mixer != null && mixer.isLineSupported(info)) {
                return mixer.getLine(info);
            }
        } catch (LineUnavailableException e) {
            lue = e;
        } catch (IllegalArgumentException iae) {
            // must not happen... but better to catch it here,
            // if plug-ins are badly written
        }


        // 2: if that doesn't work, try to find any mixing mixer
        for(int i = 0; i < providers.size(); i++) {
            MixerProvider provider = (MixerProvider) providers.get(i);
            Mixer.Info[] infos = provider.getMixerInfo();

            for (int j = 0; j < infos.length; j++) {
                try {
                    Mixer mixer = provider.getMixer(infos[j]);
                    // see if this is an appropriate mixer which can mix
                    if (isAppropriateMixer(mixer, info, true)) {
                        return mixer.getLine(info);
                    }
                } catch (LineUnavailableException e) {
                    lue = e;
                } catch (IllegalArgumentException iae) {
                    // must not happen... but better to catch it here,
                    // if plug-ins are badly written
                }
            }
        }


        // 3: if that didn't work, try to find any non-mixing mixer
        for(int i = 0; i < providers.size(); i++) {
            MixerProvider provider = (MixerProvider) providers.get(i);
            Mixer.Info[] infos = provider.getMixerInfo();
            for (int j = 0; j < infos.length; j++) {
                try {
                    Mixer mixer = provider.getMixer(infos[j]);
                    // see if this is an appropriate mixer which can mix
                    if (isAppropriateMixer(mixer, info, false)) {
                        return mixer.getLine(info);
                    }
                } catch (LineUnavailableException e) {
                    lue = e;
                } catch (IllegalArgumentException iae) {
                    // must not happen... but better to catch it here,
                    // if plug-ins are badly written
                }
            }
        }

        // if this line was supported but was not available, throw the last
        // LineUnavailableException we got (??).
        if (lue != null) {
            throw lue;
        }

        // otherwise, the requested line was not supported, so throw
        // an Illegal argument exception
        throw new IllegalArgumentException("No line matching " +
                                           info.toString() + " is supported.");
    }

    public static Clip getClip() throws LineUnavailableException{
        AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                                             AudioSystem.NOT_SPECIFIED,
                                             16, 2, 4,
                                             AudioSystem.NOT_SPECIFIED, true);
        DataLine.Info info = new DataLine.Info(Clip.class, format);
        return (Clip) AudioSystem.getLine(info);
    }


    public static Clip getClip(Mixer.Info mixerInfo) throws LineUnavailableException{
        AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                                             AudioSystem.NOT_SPECIFIED,
                                             16, 2, 4,
                                             AudioSystem.NOT_SPECIFIED, true);
        DataLine.Info info = new DataLine.Info(Clip.class, format);
        Mixer mixer = AudioSystem.getMixer(mixerInfo);
        return (Clip) mixer.getLine(info);
    }


    public static SourceDataLine getSourceDataLine(AudioFormat format)
        throws LineUnavailableException{
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        return (SourceDataLine) AudioSystem.getLine(info);
    }


    public static SourceDataLine getSourceDataLine(AudioFormat format,
                                                   Mixer.Info mixerinfo)
        throws LineUnavailableException{
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        Mixer mixer = AudioSystem.getMixer(mixerinfo);
        return (SourceDataLine) mixer.getLine(info);
  }

    public static TargetDataLine getTargetDataLine(AudioFormat format)
        throws LineUnavailableException{

        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        return (TargetDataLine) AudioSystem.getLine(info);
    }

    public static TargetDataLine getTargetDataLine(AudioFormat format,
                                                   Mixer.Info mixerinfo)
        throws LineUnavailableException {

        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        Mixer mixer = AudioSystem.getMixer(mixerinfo);
        return (TargetDataLine) mixer.getLine(info);
    }


    // $$fb 2002-04-12: fix for 4662082: behavior of AudioSystem.getTargetEncodings() methods doesn't match the spec
    /**
     * Obtains the encodings that the system can obtain from an
     * audio input stream with the specified encoding using the set
     * of installed format converters.
     * @param sourceEncoding the encoding for which conversion support
     * is queried
     * @return array of encodings.  If <code>sourceEncoding</code>is not supported,
     * an array of length 0 is returned. Otherwise, the array will have a length
     * of at least 1, representing <code>sourceEncoding</code> (no conversion).
     */
    public static AudioFormat.Encoding[] getTargetEncodings(AudioFormat.Encoding sourceEncoding) {

        List codecs = getFormatConversionProviders();
        Vector encodings = new Vector();

        AudioFormat.Encoding encs[] = null;

        // gather from all the codecs
        for(int i=0; i<codecs.size(); i++ ) {
            FormatConversionProvider codec = (FormatConversionProvider) codecs.get(i);
            if( codec.isSourceEncodingSupported( sourceEncoding ) ) {
                encs = codec.getTargetEncodings();
                for (int j = 0; j < encs.length; j++) {
                    encodings.addElement( encs[j] );
                }
            }
        }
        AudioFormat.Encoding encs2[] = (AudioFormat.Encoding[]) encodings.toArray(new AudioFormat.Encoding[0]);
        return encs2;
    }



    // $$fb 2002-04-12: fix for 4662082: behavior of AudioSystem.getTargetEncodings() methods doesn't match the spec
    /**
     * Obtains the encodings that the system can obtain from an
     * audio input stream with the specified format using the set
     * of installed format converters.
     * @param sourceFormat the audio format for which conversion
     * is queried
     * @return array of encodings. If <code>sourceFormat</code>is not supported,
     * an array of length 0 is returned. Otherwise, the array will have a length
     * of at least 1, representing the encoding of <code>sourceFormat</code> (no conversion).
     */
    public static AudioFormat.Encoding[] getTargetEncodings(AudioFormat sourceFormat) {


        List codecs = getFormatConversionProviders();
        Vector encodings = new Vector();

        int size = 0;
        int index = 0;
        AudioFormat.Encoding encs[] = null;

        // gather from all the codecs

        for(int i=0; i<codecs.size(); i++ ) {
            encs = ((FormatConversionProvider) codecs.get(i)).getTargetEncodings(sourceFormat);
            size += encs.length;
            encodings.addElement( encs );
        }

        // now build a new array

        AudioFormat.Encoding encs2[] = new AudioFormat.Encoding[size];
        for(int i=0; i<encodings.size(); i++ ) {
            encs = (AudioFormat.Encoding [])(encodings.get(i));
            for(int j=0; j<encs.length; j++ ) {
                encs2[index++] = encs[j];
            }
        }
        return encs2;
    }


    /**
     * Indicates whether an audio input stream of the specified encoding
     * can be obtained from an audio input stream that has the specified
     * format.
     * @param targetEncoding the desired encoding after conversion
     * @param sourceFormat the audio format before conversion
     * @return <code>true</code> if the conversion is supported,
     * otherwise <code>false</code>
     */
    public static boolean isConversionSupported(AudioFormat.Encoding targetEncoding, AudioFormat sourceFormat) {


        List codecs = getFormatConversionProviders();

        for(int i=0; i<codecs.size(); i++ ) {
            FormatConversionProvider codec = (FormatConversionProvider) codecs.get(i);
            if(codec.isConversionSupported(targetEncoding,sourceFormat) ) {
                return true;
            }
        }
        return false;
    }


    /**
     * Obtains an audio input stream of the indicated encoding, by converting the
     * provided audio input stream.
     * @param targetEncoding the desired encoding after conversion
     * @param sourceStream the stream to be converted
     * @return an audio input stream of the indicated encoding
     * @throws IllegalArgumentException if the conversion is not supported
     * @see #getTargetEncodings(AudioFormat.Encoding)
     * @see #getTargetEncodings(AudioFormat)
     * @see #isConversionSupported(AudioFormat.Encoding, AudioFormat)
     * @see #getAudioInputStream(AudioFormat, AudioInputStream)
     */
    public static AudioInputStream getAudioInputStream(AudioFormat.Encoding targetEncoding,
                                                       AudioInputStream sourceStream) {

        List codecs = getFormatConversionProviders();

        for(int i = 0; i < codecs.size(); i++) {
            FormatConversionProvider codec = (FormatConversionProvider) codecs.get(i);
            if( codec.isConversionSupported( targetEncoding, sourceStream.getFormat() ) ) {
                return codec.getAudioInputStream( targetEncoding, sourceStream );
            }
        }
        // we ran out of options, throw an exception
        throw new IllegalArgumentException("Unsupported conversion: " + targetEncoding + " from " + sourceStream.getFormat());
    }


    /**
     * Obtains the formats that have a particular encoding and that the system can
     * obtain from a stream of the specified format using the set of
     * installed format converters.
     * @param targetEncoding the desired encoding after conversion
     * @param sourceFormat the audio format before conversion
     * @return array of formats.  If no formats of the specified
     * encoding are supported, an array of length 0 is returned.
     */
    public static AudioFormat[] getTargetFormats(AudioFormat.Encoding targetEncoding, AudioFormat sourceFormat) {

        List codecs = getFormatConversionProviders();
        Vector formats = new Vector();

        int size = 0;
        int index = 0;
        AudioFormat fmts[] = null;

        // gather from all the codecs

        for(int i=0; i<codecs.size(); i++ ) {
            FormatConversionProvider codec = (FormatConversionProvider) codecs.get(i);
            fmts = codec.getTargetFormats(targetEncoding, sourceFormat);
            size += fmts.length;
            formats.addElement( fmts );
        }

        // now build a new array

        AudioFormat fmts2[] = new AudioFormat[size];
        for(int i=0; i<formats.size(); i++ ) {
            fmts = (AudioFormat [])(formats.get(i));
            for(int j=0; j<fmts.length; j++ ) {
                fmts2[index++] = fmts[j];
            }
        }
        return fmts2;
    }


    /**
     * Indicates whether an audio input stream of a specified format
     * can be obtained from an audio input stream of another specified format.
     * @param targetFormat the desired audio format after conversion
     * @param sourceFormat the audio format before conversion
     * @return <code>true</code> if the conversion is supported,
     * otherwise <code>false</code>
     */

    public static boolean isConversionSupported(AudioFormat targetFormat, AudioFormat sourceFormat) {

        List codecs = getFormatConversionProviders();

        for(int i=0; i<codecs.size(); i++ ) {
            FormatConversionProvider codec = (FormatConversionProvider) codecs.get(i);
            if(codec.isConversionSupported(targetFormat, sourceFormat) ) {
                return true;
            }
        }
        return false;
    }


    /**
     * Obtains an audio input stream of the indicated format, by converting the
     * provided audio input stream.
     * @param targetFormat the desired audio format after conversion
     * @param sourceStream the stream to be converted
     * @return an audio input stream of the indicated format
     * @throws IllegalArgumentException if the conversion is not supported
     * #see #getTargetEncodings(AudioFormat)
     * @see #getTargetFormats(AudioFormat.Encoding, AudioFormat)
     * @see #isConversionSupported(AudioFormat, AudioFormat)
     * @see #getAudioInputStream(AudioFormat.Encoding, AudioInputStream)
     */
    public static AudioInputStream getAudioInputStream(AudioFormat targetFormat,
                                                       AudioInputStream sourceStream) {

        if (sourceStream.getFormat().matches(targetFormat)) {
            return sourceStream;
        }

        List codecs = getFormatConversionProviders();

        for(int i = 0; i < codecs.size(); i++) {
            FormatConversionProvider codec = (FormatConversionProvider) codecs.get(i);
            if(codec.isConversionSupported(targetFormat,sourceStream.getFormat()) ) {
                return codec.getAudioInputStream(targetFormat,sourceStream);
            }
        }

        // we ran out of options...
        throw new IllegalArgumentException("Unsupported conversion: " + targetFormat + " from " + sourceStream.getFormat());
    }


    /**
     * Obtains the audio file format of the provided input stream.  The stream must
     * point to valid audio file data.  The implementation of this method may require
     * multiple parsers to examine the stream to determine whether they support it.
     * These parsers must be able to mark the stream, read enough data to determine whether they
     * support the stream, and, if not, reset the stream's read pointer to its original
     * position.  If the input stream does not support these operations, this method may fail
     * with an <code>IOException</code>.
     * @param stream the input stream from which file format information should be
     * extracted
     * @return an <code>AudioFileFormat</code> object describing the stream's audio file format
     * @throws UnsupportedAudioFileException if the stream does not point to valid audio
     * file data recognized by the system
     * @throws IOException if an input/output exception occurs
     * @see InputStream#markSupported
     * @see InputStream#mark
     */
    public static AudioFileFormat getAudioFileFormat(InputStream stream)
        throws UnsupportedAudioFileException, IOException {

        List providers = getAudioFileReaders();
        AudioFileFormat format = null;

        for(int i = 0; i < providers.size(); i++ ) {
            AudioFileReader reader = (AudioFileReader) providers.get(i);
            try {
                format = reader.getAudioFileFormat( stream ); // throws IOException
                break;
            } catch (UnsupportedAudioFileException e) {
                continue;
            }
        }

        if( format==null ) {
            throw new UnsupportedAudioFileException("file is not a supported file type");
        } else {
            return format;
        }
    }

    public static AudioFileFormat getAudioFileFormat(URL url)
        throws UnsupportedAudioFileException, IOException {

        List providers = getAudioFileReaders();
        AudioFileFormat format = null;

        for(int i = 0; i < providers.size(); i++ ) {
            AudioFileReader reader = (AudioFileReader) providers.get(i);
            try {
                format = reader.getAudioFileFormat( url ); // throws IOException
                break;
            } catch (UnsupportedAudioFileException e) {
                continue;
            }
        }

        if( format==null ) {
            throw new UnsupportedAudioFileException("file is not a supported file type");
        } else {
            return format;
        }
    }

    public static AudioFileFormat getAudioFileFormat(File file)
        throws UnsupportedAudioFileException, IOException {

        List providers = getAudioFileReaders();
        AudioFileFormat format = null;

        for(int i = 0; i < providers.size(); i++ ) {
            AudioFileReader reader = (AudioFileReader) providers.get(i);
            try {
                format = reader.getAudioFileFormat( file ); // throws IOException
                break;
            } catch (UnsupportedAudioFileException e) {
                continue;
            }
        }

        if( format==null ) {
            throw new UnsupportedAudioFileException("file is not a supported file type");
        } else {
            return format;
        }
    }


    /**
     * Obtains an audio input stream from the provided input stream.  The stream must
     * point to valid audio file data.  The implementation of this method may
     * require multiple parsers to
     * examine the stream to determine whether they support it.  These parsers must
     * be able to mark the stream, read enough data to determine whether they
     * support the stream, and, if not, reset the stream's read pointer to its original
     * position.  If the input stream does not support these operation, this method may fail
     * with an <code>IOException</code>.
     * @param stream the input stream from which the <code>AudioInputStream</code> should be
     * constructed
     * @return an <code>AudioInputStream</code> object based on the audio file data contained
     * in the input stream.
     * @throws UnsupportedAudioFileException if the stream does not point to valid audio
     * file data recognized by the system
     * @throws IOException if an I/O exception occurs
     * @see InputStream#markSupported
     * @see InputStream#mark
     */
    public static AudioInputStream getAudioInputStream(InputStream stream)
        throws UnsupportedAudioFileException, IOException {
        List providers = getAudioFileReaders();
        //List providers = getAudioFileReaders();
        //todo getAudioFileReaders()
        System.out.println(providers+"  "+providers.size());

        AudioInputStream audioStream = null;

        for(int i = 0; i < providers.size(); i++ ) {
            AudioFileReader reader = (AudioFileReader) providers.get(i);

            try {
                audioStream = reader.getAudioInputStream( stream ); // throws IOException
                break;
            } catch (UnsupportedAudioFileException e) {
                continue;
            }
        }

        if( audioStream==null ) {
            throw new UnsupportedAudioFileException("could not get audio input stream from input stream");
        } else {
            return audioStream;
        }
    }

    /**
     * Obtains an audio input stream from the URL provided.  The URL must
     * point to valid audio file data.
     * @param url the URL for which the <code>AudioInputStream</code> should be
     * constructed
     * @return an <code>AudioInputStream</code> object based on the audio file data pointed
     * to by the URL
     * @throws UnsupportedAudioFileException if the URL does not point to valid audio
     * file data recognized by the system
     * @throws IOException if an I/O exception occurs
     */
    public static AudioInputStream getAudioInputStream(URL url)
        throws UnsupportedAudioFileException, IOException {

        List providers = getAudioFileReaders();
        AudioInputStream audioStream = null;

        for(int i = 0; i < providers.size(); i++ ) {
            AudioFileReader reader = (AudioFileReader) providers.get(i);
            try {
                audioStream = reader.getAudioInputStream( url ); // throws IOException
                break;
            } catch (UnsupportedAudioFileException e) {
                continue;
            }
        }

        if( audioStream==null ) {
            throw new UnsupportedAudioFileException("could not get audio input stream from input URL");
        } else {
            return audioStream;
        }
    }

    /**
     * Obtains an audio input stream from the provided <code>File</code>.  The <code>File</code> must
     * point to valid audio file data.
     * @param file the <code>File</code> for which the <code>AudioInputStream</code> should be
     * constructed
     * @return an <code>AudioInputStream</code> object based on the audio file data pointed
     * to by the <code>File</code>
     * @throws UnsupportedAudioFileException if the <code>File</code> does not point to valid audio
     * file data recognized by the system
     * @throws IOException if an I/O exception occurs
     */
    public static AudioInputStream getAudioInputStream(File file)
        throws UnsupportedAudioFileException, IOException {

        List providers = getAudioFileReaders();
        AudioInputStream audioStream = null;

        for(int i = 0; i < providers.size(); i++ ) {
            AudioFileReader reader = (AudioFileReader) providers.get(i);
            try {
                audioStream = reader.getAudioInputStream( file ); // throws IOException
                break;
            } catch (UnsupportedAudioFileException e) {
                continue;
            }
        }

        if( audioStream==null ) {
            throw new UnsupportedAudioFileException("could not get audio input stream from input file");
        } else {
            return audioStream;
        }
    }


    /**
     * Obtains the file types for which file writing support is provided by the system.
     * @return array of unique file types.  If no file types are supported,
     * an array of length 0 is returned.
     */
    public static AudioFileFormat.Type[] getAudioFileTypes() {
        List providers = getAudioFileWriters();
        Set returnTypesSet = new HashSet();

        for(int i=0; i < providers.size(); i++) {
            AudioFileWriter writer = (AudioFileWriter) providers.get(i);
            AudioFileFormat.Type[] fileTypes = writer.getAudioFileTypes();
            for(int j=0; j < fileTypes.length; j++) {
                returnTypesSet.add(fileTypes[j]);
            }
        }
        AudioFileFormat.Type returnTypes[] = (AudioFileFormat.Type[])
            returnTypesSet.toArray(new AudioFileFormat.Type[0]);
        return returnTypes;
    }


    /**
     * Indicates whether file writing support for the specified file type is provided
     * by the system.
     * @param fileType the file type for which write capabilities are queried
     * @return <code>true</code> if the file type is supported,
     * otherwise <code>false</code>
     */
    public static boolean isFileTypeSupported(AudioFileFormat.Type fileType) {

        List providers = getAudioFileWriters();

        for(int i=0; i < providers.size(); i++) {
            AudioFileWriter writer = (AudioFileWriter) providers.get(i);
            if (writer.isFileTypeSupported(fileType)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Obtains the file types that the system can write from the
     * audio input stream specified.
     * @param stream the audio input stream for which audio file type support
     * is queried
     * @return array of file types.  If no file types are supported,
     * an array of length 0 is returned.
     */
    public static AudioFileFormat.Type[] getAudioFileTypes(AudioInputStream stream) {
        List providers = getAudioFileWriters();
        Set returnTypesSet = new HashSet();

        for(int i=0; i < providers.size(); i++) {
            AudioFileWriter writer = (AudioFileWriter) providers.get(i);
            AudioFileFormat.Type[] fileTypes = writer.getAudioFileTypes(stream);
            for(int j=0; j < fileTypes.length; j++) {
                returnTypesSet.add(fileTypes[j]);
            }
        }
        AudioFileFormat.Type returnTypes[] = (AudioFileFormat.Type[])
            returnTypesSet.toArray(new AudioFileFormat.Type[0]);
        return returnTypes;
    }


    /**
     * Indicates whether an audio file of the specified file type can be written
     * from the indicated audio input stream.
     * @param fileType the file type for which write capabilities are queried
     * @param stream the stream for which file-writing support is queried
     * @return <code>true</code> if the file type is supported for this audio input stream,
     * otherwise <code>false</code>
     */
    public static boolean isFileTypeSupported(AudioFileFormat.Type fileType,
                                              AudioInputStream stream) {

        List providers = getAudioFileWriters();

        for(int i=0; i < providers.size(); i++) {
            AudioFileWriter writer = (AudioFileWriter) providers.get(i);
            if(writer.isFileTypeSupported(fileType, stream)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Writes a stream of bytes representing an audio file of the specified file type
     * to the output stream provided.  Some file types require that
     * the length be written into the file header; such files cannot be written from
     * start to finish unless the length is known in advance.  An attempt
     * to write a file of such a type will fail with an IOException if the length in
     * the audio file type is <code>AudioSystem.NOT_SPECIFIED</code>.
     *
     * @param stream the audio input stream containing audio data to be
     * written to the file
     * @param fileType the kind of audio file to write
     * @param out the stream to which the file data should be written
     * @return the number of bytes written to the output stream
     * @throws IOException if an input/output exception occurs
     * @throws IllegalArgumentException if the file type is not supported by
     * the system
     * @see #isFileTypeSupported
     * @see     #getAudioFileTypes
     */
    public static int write(AudioInputStream stream, AudioFileFormat.Type fileType,
                            OutputStream out) throws IOException {

        List providers = getAudioFileWriters();
        int bytesWritten = 0;
        boolean flag = false;

        for(int i=0; i < providers.size(); i++) {
            AudioFileWriter writer = (AudioFileWriter) providers.get(i);
            try {
                bytesWritten = writer.write( stream, fileType, out ); // throws IOException
                flag = true;
                break;
            } catch (IllegalArgumentException e) {
                // thrown if this provider cannot write the sequence, try the next
                continue;
            }
        }
        if(!flag) {
            throw new IllegalArgumentException("could not write audio file: file type not supported: " + fileType);
        } else {
            return bytesWritten;
        }
    }


    /**
     * Writes a stream of bytes representing an audio file of the specified file type
     * to the external file provided.
     * @param stream the audio input stream containing audio data to be
     * written to the file
     * @param fileType the kind of audio file to write
     * @param out the external file to which the file data should be written
     * @return the number of bytes written to the file
     * @throws IOException if an I/O exception occurs
     * @throws IllegalArgumentException if the file type is not supported by
     * the system
     * @see #isFileTypeSupported
     * @see     #getAudioFileTypes
     */
    public static int write(AudioInputStream stream, AudioFileFormat.Type fileType,
                            File out) throws IOException {

        List providers = getAudioFileWriters();
        int bytesWritten = 0;
        boolean flag = false;

        for(int i=0; i < providers.size(); i++) {
            AudioFileWriter writer = (AudioFileWriter) providers.get(i);
            try {
                bytesWritten = writer.write( stream, fileType, out ); // throws IOException
                flag = true;
                break;
            } catch (IllegalArgumentException e) {
                // thrown if this provider cannot write the sequence, try the next
                continue;
            }
        }
        if (!flag) {
            throw new IllegalArgumentException("could not write audio file: file type not supported: " + fileType);
        } else {
            return bytesWritten;
        }
    }


    // METHODS FOR INTERNAL IMPLEMENTATION USE

    /**
     * Obtains the set of MixerProviders on the system.
     */
    private static List getMixerProviders() {
        return getProviders(MixerProvider.class);
    }


    private static List getFormatConversionProviders() {
        return getProviders(FormatConversionProvider.class);
    }

    private static List getAudioFileReaders() {
        return getProviders(AudioFileReader.class);
    }


    /**
     * Obtains the set of audio file writers that are currently installed on the system.
     * @return a List of
     * objects representing the available audio file writers.  If no audio file
     * writers are available on the system, an empty List is returned.
     */
    private static List getAudioFileWriters() {
        return getProviders(AudioFileWriter.class);
    }



    /** Attempts to locate and return a default Mixer that provides lines
     * of the specified type.
     *
     * @param providers the installed mixer providers
     * @param info The requested line type
     * TargetDataLine.class, Clip.class or Port.class.
     * @return a Mixer that matches the requirements, or null if no default mixer found
     */
    private static Mixer getDefaultMixer(List providers, Line.Info info) {
        Class lineClass = info.getLineClass();
        String providerClassName = JDK13Services.getDefaultProviderClassName(lineClass);
        String instanceName = JDK13Services.getDefaultInstanceName(lineClass);
        Mixer mixer;

        if (providerClassName != null) {
            MixerProvider defaultProvider = getNamedProvider(providerClassName, providers);
            if (defaultProvider != null) {
                if (instanceName != null) {
                    mixer = getNamedMixer(instanceName, defaultProvider, info);
                    if (mixer != null) {
                        return mixer;
                    }
                } else {
                    mixer = getFirstMixer(defaultProvider, info,
                                          false /* mixing not required*/);
                    if (mixer != null) {
                        return mixer;
                    }
                }

            }
        }

        /* Provider class not specified or
           provider class cannot be found, or
           provider class and instance specified and instance cannot be found or is not appropriate */
        if (instanceName != null) {
            mixer = getNamedMixer(instanceName, providers, info);
            if (mixer != null) {
                return mixer;
            }
        }


        /* No default are specified, or if something is specified, everything
           failed. */
        return null;
    }



    /** Return a MixerProvider of a given class from the list of
        MixerProviders.

        This method never requires the returned Mixer to do mixing.
        @param providerClassName The class name of the provider to be returned.
        @param providers The list of MixerProviders that is searched.
        @return A MixerProvider of the requested class, or null if none is
        found.
     */
    private static MixerProvider getNamedProvider(String providerClassName,
                                                  List providers) {
        for(int i = 0; i < providers.size(); i++) {
            MixerProvider provider = (MixerProvider) providers.get(i);
            if (provider.getClass().getName().equals(providerClassName)) {
                return provider;
            }
        }
        return null;
    }


    /** Return a Mixer with a given name from a given MixerProvider.
      This method never requires the returned Mixer to do mixing.
      @param mixerName The name of the Mixer to be returned.
      @param provider The MixerProvider to check for Mixers.
      @param info The type of line the returned Mixer is required to
      support.

      @return A Mixer matching the requirements, or null if none is found.
     */
    private static Mixer getNamedMixer(String mixerName,
                                       MixerProvider provider,
                                       Line.Info info) {
        Mixer.Info[] infos = provider.getMixerInfo();
        for (int i = 0; i < infos.length; i++) {
            if (infos[i].getName().equals(mixerName)) {
                Mixer mixer = provider.getMixer(infos[i]);
                if (isAppropriateMixer(mixer, info, false)) {
                    return mixer;
                }
            }
        }
        return null;
    }


    /** From a List of MixerProviders, return a Mixer with a given name.
        This method never requires the returned Mixer to do mixing.
        @param mixerName The name of the Mixer to be returned.
        @param providers The List of MixerProviders to check for Mixers.
        @param info The type of line the returned Mixer is required to
        support.
        @return A Mixer matching the requirements, or null if none is found.
     */
    private static Mixer getNamedMixer(String mixerName,
                                       List providers,
                                       Line.Info info) {
        for(int i = 0; i < providers.size(); i++) {
            MixerProvider provider = (MixerProvider) providers.get(i);
            Mixer mixer = getNamedMixer(mixerName, provider, info);
            if (mixer != null) {
                return mixer;
            }
        }
        return null;
    }


    /** From a given MixerProvider, return the first appropriate Mixer.
        @param provider The MixerProvider to check for Mixers.
        @param info The type of line the returned Mixer is required to
        support.
        @param isMixingRequired If true, only Mixers that support mixing are
        returned for line types of SourceDataLine and Clip.

        @return A Mixer that is considered appropriate, or null
        if none is found.
     */
    private static Mixer getFirstMixer(MixerProvider provider,
                                       Line.Info info,
                                       boolean isMixingRequired) {
        Mixer.Info[] infos = provider.getMixerInfo();
        for (int j = 0; j < infos.length; j++) {
            Mixer mixer = provider.getMixer(infos[j]);
            if (isAppropriateMixer(mixer, info, isMixingRequired)) {
                return mixer;
            }
        }
        return null;
    }


    /** Checks if a Mixer is appropriate.
        A Mixer is considered appropriate if it support the given line type.
        If isMixingRequired is true and the line type is an output one
        (SourceDataLine, Clip), the mixer is appropriate if it supports
        at least 2 (concurrent) lines of the given type.

        @return true if the mixer is considered appropriate according to the
        rules given above, false otherwise.
     */
    private static boolean isAppropriateMixer(Mixer mixer,
                                              Line.Info lineInfo,
                                              boolean isMixingRequired) {
        if (! mixer.isLineSupported(lineInfo)) {
            return false;
        }
        Class lineClass = lineInfo.getLineClass();
        if (isMixingRequired
            && (SourceDataLine.class.isAssignableFrom(lineClass) ||
                Clip.class.isAssignableFrom(lineClass))) {
            int maxLines = mixer.getMaxLines(lineInfo);
            return ((maxLines == NOT_SPECIFIED) || (maxLines > 1));
        }
        return true;
    }



    /**
     * Like getMixerInfo, but return List
     */
    private static List getMixerInfoList() {
        List providers = getMixerProviders();
        return getMixerInfoList(providers);
    }


    /**
     * Like getMixerInfo, but return List
     */
    private static List getMixerInfoList(List providers) {
        List infos = new ArrayList();

        Mixer.Info[] someInfos; // per-mixer
        Mixer.Info[] allInfos;  // for all mixers

        for(int i = 0; i < providers.size(); i++ ) {
            someInfos = (Mixer.Info[])
                ((MixerProvider)providers.get(i)).getMixerInfo();

            for (int j = 0; j < someInfos.length; j++) {
                infos.add(someInfos[j]);
            }
        }

        return infos;
    }


    /**
     * Obtains the set of services currently installed on the system
     * using sun.misc.Service, the SPI mechanism in 1.3.
     * @return a List of instances of providers for the requested service.
     * If no providers are available, a vector of length 0 will be returned.
     */
    private static List getProviders(Class providerClass) {
        //todo getProviders
       // System.out.println(JDK13Services.getProviders(providerClass)+"providerClass");
        return JDK13Services.getProviders(providerClass);

    }
}
