package org.jaudiotagger.audio.mp4.atom;

import org.jaudiotagger.audio.generic.Utils;

import java.nio.ByteBuffer;

/**
 * MdhdBox ( media (stream) header), holds the Sampling Rate used.
 */
public class Mp4MdhdBox extends AbstractMp4Box
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

    private int samplingRate;

    /**
     * @param header     header info
     * @param dataBuffer data of box (doesnt include header data)
     */
    public Mp4MdhdBox(Mp4BoxHeader header, ByteBuffer dataBuffer)
    {
        this.header = header;

        byte version = dataBuffer.get(VERSION_FLAG_POS);

        long timeLength;
        if (version == LONG_FORMAT)
        {
            this.samplingRate = Utils.getIntBE(dataBuffer, TIMESCALE_LONG_POS, (TIMESCALE_LONG_POS + TIMESCALE_LENGTH - 1));
            timeLength = Utils.getLongBE(dataBuffer, DURATION_LONG_POS, (DURATION_LONG_POS + DURATION_LONG_LENGTH - 1));
        }
        else
        {
            this.samplingRate = Utils.getIntBE(dataBuffer, TIMESCALE_SHORT_POS, (TIMESCALE_SHORT_POS + TIMESCALE_LENGTH - 1));
            timeLength = Utils.getIntBE(dataBuffer, DURATION_SHORT_POS, (DURATION_SHORT_POS + DURATION_SHORT_LENGTH - 1));
        }
    }

    public int getSampleRate()
    {
        return samplingRate;
    }

}
