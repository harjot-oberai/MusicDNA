package org.jaudiotagger.audio;

/**
 * Representation of AudioHeader
 *
 * <p>Contains info about the Audio Header
 */
public interface AudioHeader
{
    /**
     * @return the audio file type
     */
    public abstract String getEncodingType();

    /**
     * @return the BitRate of the Audio
     */
    public String getBitRate();

    /**
     * @return birate as a number
     */
    public long getBitRateAsNumber();


    /**
     * @return the Sampling rate
     */
    public String getSampleRate();

    /**
     * @return
     */
    public int getSampleRateAsNumber();

    /**
     * @return the format
     */
    public String getFormat();

    /**
     * @return the Channel Mode such as Stereo or Mono
     */
    public String getChannels();

    /**
     * @return if the bitRate is variable
     */
    public boolean isVariableBitRate();

    /**
     * @return track length
     */
    public int getTrackLength();
    
    /**
     * @return the number of bits per sample
     */
    public int getBitsPerSample();

    /**
     *
     * @return
     */
    public boolean isLossless();
}
