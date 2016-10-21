package org.jaudiotagger.audio.aiff;

import java.io.IOException;
import java.io.RandomAccessFile;

import org.jaudiotagger.audio.exceptions.CannotReadException;


/** Functions for reading an AIFF file */
public class AiffInfoReader {

    public AiffAudioHeader read(RandomAccessFile raf) throws CannotReadException, IOException {
        if (raf.length() < 4)
        {
            //Empty File
            throw new CannotReadException("Not an AIFF file; too short");
        }
        return null;     // TODO stub
    }

}
