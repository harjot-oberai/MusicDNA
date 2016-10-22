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
package org.jaudiotagger.audio.wav.util;

public class WavFormatHeader
{
    private static final int WAVE_FORMAT_PCM = 0x0001;
    private static final int WAVE_FORMAT_EXTENSIBLE = 0xFFFE;

    private boolean isValid = false;

    private int format, channels, sampleRate, bytesPerSecond, bitsPerSample, validBitsPerSample, channelMask, subFormat;

    public WavFormatHeader(byte[] b)
    {
        String fmt = new String(b, 0, 3);
        format = u(b[9]) * 256 + u(b[8]);
        //System.err.println("format : "+ format);
        //System.err.println(fmt);
        if (fmt.equals("fmt") && (format == WAVE_FORMAT_PCM || format == WAVE_FORMAT_EXTENSIBLE))
        {
            channels = b[10];
            //System.err.println(channels);
            sampleRate = u(b[15]) * 16777216 + u(b[14]) * 65536 + u(b[13]) * 256 + u(b[12]);
            //System.err.println(sampleRate);
            bytesPerSecond = u(b[19]) * 16777216 + u(b[18]) * 65536 + u(b[17]) * 256 + u(b[16]);
            //System.err.println(bytesPerSecond);
            bitsPerSample = u(b[22]);

            if (format == WAVE_FORMAT_EXTENSIBLE && u(b[24]) == 22) {
                validBitsPerSample = u(b[26]);
                channelMask = u(b[31]) * 16777216 + u(b[20]) * 65536 + u(b[29]) * 256 + u(b[28]);
                subFormat = u(b[33]) * 256 + u(b[32]);
            }

            isValid = true;
        }

    }

    public boolean isExtensible() {
        return format == WAVE_FORMAT_EXTENSIBLE;
    }
    
    public int getFormat() {
        return format;
    }

    public boolean isValid()
    {
        return isValid;
    }

    public int getChannelMask()
    {
        return channelMask;
    }

    public int getSubFormat()
    {
        return subFormat;
    }

    public int getValidBitsPerSample()
    {
        return validBitsPerSample;
    }

    public int getChannelNumber()
    {
        return channels;
    }

    public int getSamplingRate()
    {
        return sampleRate;
    }

    public int getBytesPerSecond()
    {
        return bytesPerSecond;
    }

    public int getBitsPerSample()
    {
        return bitsPerSample;
    }

    private int u(int n)
    {
        return n & 0xff;
    }

    public String toString()
    {
        String out = "RIFF-WAVE Header:\n";
        out += "Is valid?: " + isValid;
        return out;
    }
}
