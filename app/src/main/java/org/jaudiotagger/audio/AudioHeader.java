package org.jaudiotagger.audio;

/**
 * Representation of AudioHeader
 * <p>Contains info about the Audio Header
 */
public interface AudioHeader {
    /**
     * @return the audio file type
     */
    String getEncodingType();

    /**
     * @return the BitRate of the Audio
     */
    String getBitRate();

    /**
     * @return birate as a number
     */
    long getBitRateAsNumber();

    /**
     * @return the Sampling rate
     */
    String getSampleRate();

    /**
     * @return
     */
    int getSampleRateAsNumber();

    /**
     * @return the format
     */
    String getFormat();

    /**
     * @return the Channel Mode such as Stereo or Mono
     */
    String getChannels();

    /**
     * @return if the bitRate is variable
     */
    boolean isVariableBitRate();

    /**
     * @return track length
     */
    int getTrackLength();
    
    /**
     * @return the number of bits per sample
     */
    int getBitsPerSample();

    /**
     * @return
     */
    boolean isLossless();
}