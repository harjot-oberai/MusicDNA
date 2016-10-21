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

public class WavRIFFHeader
{

    private boolean isValid = false;

    public WavRIFFHeader(byte[] b)
    {
        //System.err.println(b.length);
        String RIFF = new String(b, 0, 4);
        //System.err.println(RIFF);
        String WAVE = new String(b, 8, 4);
        //System.err.println(WAVE);
        if (RIFF.equals("RIFF") && WAVE.equals("WAVE"))
        {
            isValid = true;
        }

    }

    public boolean isValid()
    {
        return isValid;
    }

    public String toString()
    {
        String out = "RIFF-WAVE Header:\n";
        out += "Is valid?: " + isValid;
        return out;
    }
}