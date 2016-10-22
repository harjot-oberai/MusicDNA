/*
 * Entagged Audio Tag library
 * Copyright (c) 2003-2005 Rapha�l Slinckx <raphael@slinckx.net>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jaudiotagger.audio.generic;

import org.jaudiotagger.audio.AudioHeader;

import java.util.HashMap;
import java.util.Set;

/**
 * This class represents a structure for storing and retrieving information
 * about the codec respectively the encoding parameters.<br>
 * Most of the parameters are available for nearly each audio format. Some
 * others would result in standard values.<br>
 * <b>Consider:</b> None of the setter methods will actually affect the audio
 * file. This is just a structure for retrieving information, not manipulating
 * the audio file.<br>
 *
 * @author Raphael Slinckx
 */
public class GenericAudioHeader implements AudioHeader
{

    /**
     * The key for the Bitrate.({@link Integer})<br>
     *
     * @see #content
     */
    public final static String FIELD_BITRATE = "BITRATE";

    /**
     * The key for the number of audio channels.({@link Integer})<br>
     *
     * @see #content
     */
    public final static String FIELD_CHANNEL = "CHANNB";

    /**
     * The key for the extra encoding information.({@link String})<br>
     *
     * @see #content
     */
    public final static String FIELD_INFOS = "INFOS";

    /**
     * The key for the audio clip duration in seconds. ({@link Float})<br>
     *
     * @see #content
     */
    public final static String FIELD_LENGTH = "LENGTH";

    /**
     * The key for the audio sample rate in &quot;Hz&quot;. ({@link Integer})<br>
     *
     * @see #content
     */
    public final static String FIELD_SAMPLERATE = "SAMPLING";
    
    /**
     * The key for the audio bits per sample. ({@link Integer})<br>
     * 
     * @see #content
     */
    public final static String FIELD_BITSPERSAMPLE = "BITSPERSAMPLE";

    /**
     * The key for the audio type.({@link String})<br>
     *
     * @see #content
     */
    public final static String FIELD_TYPE = "TYPE";

    /**
     * The key for the VBR flag. ({@link Boolean})<br>
     *
     * @see #content
     */
    public final static String FIELD_VBR = "VBR";

    /**
     * Used for WMA files
     */
    private boolean isLossless = false;

    /**
     * This table containts the parameters.<br>
     */
    protected HashMap<String, Object> content;

    /**
     * Creates an instance with emtpy values.<br>
     */
    public GenericAudioHeader()
    {
        content = new HashMap<String, Object>(6);
        content.put(FIELD_BITRATE, -1);
        content.put(FIELD_CHANNEL, -1);
        content.put(FIELD_TYPE, "");
        content.put(FIELD_INFOS, "");
        content.put(FIELD_SAMPLERATE, -1);
        content.put(FIELD_BITSPERSAMPLE, -1);
        content.put(FIELD_LENGTH, (float) -1);
        content.put(FIELD_VBR, true);
    }

    public String getBitRate()
    {
        return content.get(FIELD_BITRATE).toString();
    }


    /**
     * This method returns the bitrate of the represented audio clip in
     * &quot;Kbps&quot;.<br>
     *
     * @return The bitrate in Kbps.
     */
    public long getBitRateAsNumber()
    {
        return ((Integer) content.get(FIELD_BITRATE)).longValue();
    }

    /**
     * This method returns the number of audio channels the clip contains.<br>
     * (The stereo, mono thing).
     *
     * @return The number of channels. (2 for stereo, 1 for mono)
     */
    public int getChannelNumber()
    {
        return (Integer) content.get(FIELD_CHANNEL);
    }

    /**
     * @return
     */
    public String getChannels()
    {
        return String.valueOf(getChannelNumber());
    }

    /**
     * Returns the encoding type.
     *
     * @return The encoding type
     */
    public String getEncodingType()
    {
        return (String) content.get(FIELD_TYPE);
    }

    /**
     * Returns the format, same as encoding type
     *
     * @return The encoding type
     */
    public String getFormat()
    {
        return (String) content.get(FIELD_TYPE);
    }

    /**
     * This method returns some extra information about the encoding.<br>
     * This may not contain anything for some audio formats.<br>
     *
     * @return Some extra information.
     */
    public String getExtraEncodingInfos()
    {
        return (String) content.get(FIELD_INFOS);
    }

    /**
     * This method returns the duration of the represented audio clip in
     * seconds.<br>
     *
     * @return The duration in seconds.
     * @see #getPreciseLength()
     */
    public int getTrackLength()
    {
        return (int) getPreciseLength();
    }

    /**
     * This method returns the duration of the represented audio clip in seconds
     * (single-precision).<br>
     *
     * @return The duration in seconds.
     * @see #getTrackLength()
     */
    public float getPreciseLength()
    {
        return (Float) content.get(FIELD_LENGTH);
    }

    /**
     * This method returns the sample rate, the audio clip was encoded with.<br>
     *
     * @return Sample rate of the audio clip in &quot;Hz&quot;.
     */
    public String getSampleRate()
    {
        return content.get(FIELD_SAMPLERATE).toString();
    }

    public int getSampleRateAsNumber()
    {
        return (Integer) content.get(FIELD_SAMPLERATE);
    }
    
    /**
     * @return The number of bits per sample
     */
    public int getBitsPerSample()
    {
    	return (Integer) content.get(FIELD_BITSPERSAMPLE);
    }

    /**
     * This method returns <code>true</code>, if the audio file is encoded
     * with &quot;Variable Bitrate&quot;.<br>
     *
     * @return <code>true</code> if audio clip is encoded with VBR.
     */
    public boolean isVariableBitRate()
    {
        return (Boolean) content.get(FIELD_VBR);
    }

    /**
     * This method returns <code>true</code>, if the audio file is encoded
     * with &quot;Lossless&quot;.<br>
     *
     * @return <code>true</code> if audio clip is encoded with VBR.
     */
    public boolean isLossless()
    {
        return isLossless;
    }

    /**
     * This Method sets the bitrate in &quot;Kbps&quot;.<br>
     *
     * @param bitrate bitrate in kbps.
     */
    public void setBitrate(int bitrate)
    {
        content.put(FIELD_BITRATE, bitrate);
    }

    /**
     * Sets the number of channels.
     *
     * @param chanNb number of channels (2 for stereo, 1 for mono).
     */
    public void setChannelNumber(int chanNb)
    {
        content.put(FIELD_CHANNEL, chanNb);
    }

    /**
     * Sets the type of the encoding.<br>
     * This is a bit format specific.<br>
     * eg:Layer I/II/III
     *
     * @param encodingType Encoding type.
     */
    public void setEncodingType(String encodingType)
    {
        content.put(FIELD_TYPE, encodingType);
    }

    /**
     * A string containing anything else that might be interesting
     *
     * @param infos Extra information.
     */
    public void setExtraEncodingInfos(String infos)
    {
        content.put(FIELD_INFOS, infos);
    }

    /**
     * This method sets the audio duration of the represented clip.<br>
     *
     * @param length The duration of the audio clip in seconds.
     */
    public void setLength(int length)
    {
        content.put(FIELD_LENGTH, (float) length);
    }

    /**
     * This method sets the audio duration of the represented clip.<br>
     *
     * @param seconds The duration of the audio clip in seconds (single-precision).
     */
    public void setPreciseLength(float seconds)
    {
        content.put(FIELD_LENGTH, seconds);
    }

    /**
     * Sets the Sampling rate in &quot;Hz&quot;<br>
     *
     * @param samplingRate Sample rate.
     */
    public void setSamplingRate(int samplingRate)
    {
        content.put(FIELD_SAMPLERATE, samplingRate);
    }
    
    /*
     * Sets the Bits per Sample <br>
     * 
     * @params bitsPerSample Bits Per Sample
     */
    public void setBitsPerSample(int bitsPerSample)
    {
    	content.put(FIELD_BITSPERSAMPLE, bitsPerSample);
    }

    /**
     * Sets the VBR flag for the represented audio clip.<br>
     *
     * @param b <code>true</code> if VBR.
     */
    public void setVariableBitRate(boolean b)
    {
        content.put(FIELD_VBR, b);
    }

    /**
     * Sets the Lossless flag for the represented audio clip.<br>
     *
     * @param b <code>true</code> if Lossless.
     */
    public void setLossless(boolean b)
    {
        isLossless = b;
    }

    /**
     * Can be used to add additional information
     *
     * @param key
     * @param value
     */
    public void setExtra(String key, Object value)
    {
        content.put(key, value);
    }

    /**
     * Pretty prints this encoding info
     *
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuffer out = new StringBuffer(50);
        out.append("Encoding infos content:\n");
        Set<String> set = content.keySet();
        for (String key : set)
        {
            Object val = content.get(key);
            out.append("\t");
            out.append(key);
            out.append(" : ");
            out.append(val);
            out.append("\n");
        }
        return out.toString().substring(0, out.length() - 1);
    }
}
