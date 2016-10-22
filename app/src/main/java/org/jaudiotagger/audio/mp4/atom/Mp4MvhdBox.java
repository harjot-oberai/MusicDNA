/*
 * Entagged Audio Tag library
 * Copyright (c) 2003-2005 Raphaël Slinckx <raphael@slinckx.net>
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
package org.jaudiotagger.audio.mp4.atom;

import org.jaudiotagger.audio.generic.Utils;

import java.nio.ByteBuffer;

/**
 * MvhdBox (movie (presentation) header box)
 *
 * <p>This MP4Box contains important audio information we need. It can be used to calculate track length,
 * depending on the version field this can be in either short or long format
 */
public class Mp4MvhdBox extends AbstractMp4Box
{
    public static final int VERSION_FLAG_POS = 0;
    public static final int OTHER_FLAG_POS = 1;
    public static final int CREATED_DATE_SHORT_POS = 4;
    public static final int MODIFIED_DATE_SHORT_POS = 8;
    public static final int TIMESCALE_SHORT_POS = 12;
    public static final int DURATION_SHORT_POS = 16;

    public static final int CREATED_DATE_LONG_POS = 4;
    public static final int MODIFIED_DATE_LONG_POS = 12;
    public static final int TIMESCALE_LONG_POS = 20;
    public static final int DURATION_LONG_POS = 24;

    public static final int VERSION_FLAG_LENGTH = 1;
    public static final int OTHER_FLAG_LENGTH = 3;
    public static final int CREATED_DATE_SHORT_LENGTH = 4;
    public static final int MODIFIED_DATE_SHORT_LENGTH = 4;
    public static final int CREATED_DATE_LONG_LENGTH = 8;
    public static final int MODIFIED_DATE_LONG_LENGTH = 8;
    public static final int TIMESCALE_LENGTH = 4;
    public static final int DURATION_SHORT_LENGTH = 4;
    public static final int DURATION_LONG_LENGTH = 8;

    private static final int LONG_FORMAT = 1;

    private int timeScale;
    private long timeLength;

    /**
     * @param header     header info
     * @param dataBuffer data of box (doesnt include header data)
     */
    public Mp4MvhdBox(Mp4BoxHeader header, ByteBuffer dataBuffer)
    {
        this.header = header;
        byte version = dataBuffer.get(VERSION_FLAG_POS);

        if (version == LONG_FORMAT)
        {
            this.timeScale = Utils.getIntBE(dataBuffer, TIMESCALE_LONG_POS, (TIMESCALE_LONG_POS + TIMESCALE_LENGTH - 1));
            this.timeLength = Utils.getLongBE(dataBuffer, DURATION_LONG_POS, (DURATION_LONG_POS + DURATION_LONG_LENGTH - 1));
        }
        else
        {
            this.timeScale = Utils.getIntBE(dataBuffer, TIMESCALE_SHORT_POS, (TIMESCALE_SHORT_POS + TIMESCALE_LENGTH - 1));
            this.timeLength = Utils.getIntBE(dataBuffer, DURATION_SHORT_POS, (DURATION_SHORT_POS + DURATION_SHORT_LENGTH - 1));
        }
    }

    public int getLength()
    {
        return (int) (this.timeLength / this.timeScale);
    }
}
