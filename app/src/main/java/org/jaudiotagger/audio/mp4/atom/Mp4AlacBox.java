package org.jaudiotagger.audio.mp4.atom;

import org.jaudiotagger.audio.generic.Utils;
import org.jaudiotagger.audio.exceptions.CannotReadException;

import java.nio.ByteBuffer;

/**
 * AlacBox ( Apple Lossless Codec information description box),
 *
 * Normally occurs twice, the first ALAC contaisn the default  values, the second ALAC within contains the real
 * values for this audio.
 */
public class Mp4AlacBox extends AbstractMp4Box
{
    public static final int OTHER_FLAG_LENGTH = 4;

    private int maxSamplePerFrame; // 32bit
    private int unknown1; // 8bit
    private int sampleSize; // 8bit
    private int historyMult; // 8bit
    private int initialHistory; // 8bit
    private int kModifier; // 8bit
    private int channels; // 8bit
    private int unknown2; // 16bit
    private int maxCodedFrameSize; // 32bit
    private int bitRate; // 32bit
    private int sampleRate; // 32bit


    /**
     * DataBuffer must start from from the start of the body
     *
     * @param header     header info
     * @param dataBuffer data of box (doesnt include header data)
     */
    public Mp4AlacBox(Mp4BoxHeader header, ByteBuffer dataBuffer)
    {
        this.header     = header;
        this.dataBuffer = dataBuffer;
    }

    public void processData() throws CannotReadException
    {
        //Skip version/other flags
        dataBuffer.position(dataBuffer.position() + OTHER_FLAG_LENGTH);

        maxSamplePerFrame   = Utils.readUBEInt32(dataBuffer);
        unknown1            = Utils.readUInt8(dataBuffer);
        sampleSize          = Utils.readUInt8(dataBuffer);
        historyMult         = Utils.readUInt8(dataBuffer);
        initialHistory      = Utils.readUInt8(dataBuffer);
        kModifier           = Utils.readUInt8(dataBuffer);
        channels            = Utils.readUInt8(dataBuffer);
        unknown2            = Utils.readUBEInt16(dataBuffer);
        maxCodedFrameSize   = Utils.readUBEInt32(dataBuffer);
        bitRate             = Utils.readUBEInt32(dataBuffer);
        sampleRate          = Utils.readUBEInt32(dataBuffer);                 
    }

    public int getMaxSamplePerFrame()
    {
        return maxSamplePerFrame;
    }

    public int getUnknown1()
    {
        return unknown1;
    }

    public int getSampleSize()
    {
        return sampleSize;
    }

    public int getHistoryMult()
    {
        return historyMult;
    }

    public int getInitialHistory()
    {
        return initialHistory;
    }

    public int getKModifier()
    {
        return kModifier;
    }

    public int getChannels()
    {
        return channels;
    }

    public int getUnknown2()
    {
        return unknown2;
    }

    public int getMaxCodedFrameSize()
    {
        return maxCodedFrameSize;
    }

    public int getBitRate()
    {
        return bitRate;
    }

    public int getSampleRate()
    {
        return sampleRate;
    }

    public String toString()
    {
        String s = "maxSamplePerFrame:" + maxSamplePerFrame
                    + "unknown1:"+ unknown1
                    + "sampleSize:"+sampleSize
                    + "historyMult:"+historyMult
                    + "initialHistory:"+initialHistory
                    + "kModifier:"+kModifier
                    + "channels:"+channels
                    + "unknown2 :"+unknown2
                    + "maxCodedFrameSize:"+maxCodedFrameSize
                    + "bitRate:"+bitRate
                    + "sampleRate:"+sampleRate;
        return s;
    }
}
