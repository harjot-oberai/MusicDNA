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

import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.generic.GenericAudioHeader;

import java.io.IOException;
import java.io.RandomAccessFile;

public class WavInfoReader
{
    public GenericAudioHeader read(RandomAccessFile raf) throws CannotReadException, IOException
    {
        // Reads wav header----------------------------------------
        GenericAudioHeader info = new GenericAudioHeader();

        if (raf.length() < 12)
        {
            throw new CannotReadException("This is not a WAV File (<12 bytes)");
        }
        byte[] b = new byte[12];
        raf.read(b);

        WavRIFFHeader wh = new WavRIFFHeader(b);
        if (wh.isValid())
        {
            b = new byte[34];
            raf.read(b);

            WavFormatHeader wfh = new WavFormatHeader(b);
            if (wfh.isValid())
            {
                // Populates
                // encodingInfo----------------------------------------------------
                info.setPreciseLength(((float) raf.length() - (float) 36) / wfh.getBytesPerSecond());
                info.setChannelNumber(wfh.getChannelNumber());
                info.setSamplingRate(wfh.getSamplingRate());
                info.setBitsPerSample(wfh.getBitsPerSample());
                info.setEncodingType("WAV-RIFF " + wfh.getBitsPerSample() + " bits");
                info.setExtraEncodingInfos("");
                info.setBitrate(wfh.getBytesPerSecond() * 8 / 1000);
                info.setVariableBitRate(false);
            }
            else
            {
                throw new CannotReadException("Wav Format Header not valid");
            }
        }
        else
        {
            throw new CannotReadException("Wav RIFF Header not valid");
        }

        return info;
    }
}
