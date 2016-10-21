package org.jaudiotagger.audio.aiff;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.generic.AudioFileReader;
import org.jaudiotagger.audio.generic.GenericAudioHeader;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.aiff.AiffTag;

public class AiffFileReader extends AudioFileReader {

    /* Fixed value for first 4 bytes */
    private static final int[] sigByte =
       { 0X46, 0X4F, 0X52, 0X4D };

    /* AIFF-specific information which isn't "tag" information */
    private AiffAudioHeader aiffHeader;
    
    /* "Tag" information */
    private AiffTag aiffTag;
    
    /* InputStream that reads the file sequentially */
//    private DataInputStream inStream;
    
    public AiffFileReader () {
        aiffHeader = new AiffAudioHeader();
        aiffTag = new AiffTag ();
    }
    
    
    public AiffFileReader (RandomAccessFile raf) {
        aiffHeader = new AiffAudioHeader();
        aiffTag = new AiffTag ();
    }


    /** Reads the file and fills in the audio header and tag information.
     *  Holds the tag information for later and returns the audio header. */
    @Override
    protected GenericAudioHeader getEncodingInfo(RandomAccessFile raf)
            throws CannotReadException, IOException {
        logger.finest("Reading AIFF file ");
        byte sigBuf[] = new byte[4];
        raf.read(sigBuf);
        for (int i = 0; i < 4; i++) {
            if (sigBuf[i] != sigByte[i]) {
                logger.finest ("AIFF file has incorrect signature");
                throw new CannotReadException ("Not an AIFF file: incorrect signature");
            }
        }
        long bytesRemaining = AiffUtil.readUINT32(raf);
        
        // Read the file type.
        if (!readFileType (raf)) {
            throw new CannotReadException ("Invalid AIFF file: Incorrect file type info");
        }
        bytesRemaining -= 4;        
        while (bytesRemaining > 0) {
            if (!readChunk (raf, bytesRemaining)) {
                break;
            }
        }
        return aiffHeader;
    }

    @Override
    protected Tag getTag(RandomAccessFile raf) throws CannotReadException,
            IOException {
        logger.info("getTag called");
        
        // TODO fill out stub code
        return aiffTag;
        
    }
    
    /*  Reads the file type.   
     *  Broken out from parse().
     *  If it is not a valid file type, returns false.
     */
    private boolean readFileType (RandomAccessFile raf) throws IOException
    {
        String typ = AiffUtil.read4Chars (raf);
        if ("AIFF".equals (typ)) {
            aiffHeader.setFileType (AiffAudioHeader.FileType.AIFFTYPE);
            return true;
        }
        else if ("AIFC".equals (typ)) {
            aiffHeader.setFileType (AiffAudioHeader.FileType.AIFCTYPE);
            return true;
        }
        else {
            return false;
        }
    }

    /** Reads an AIFF Chunk.
     * 
     */
     protected boolean readChunk 
           (RandomAccessFile raf, long bytesRemaining) 
             throws IOException
     {
        Chunk chunk = null;
        ChunkHeader chunkh = new ChunkHeader ();
        if (!chunkh.readHeader(raf)) {
            return false;
        }
        int chunkSize = (int) chunkh.getSize ();
        bytesRemaining -= chunkSize + 8;
        
        String id = chunkh.getID ();
        if ("FVER".equals (id)) {
            chunk = new FormatVersionChunk (chunkh, raf, aiffHeader);
        }
        else if ("APPL".equals (id)) {
            chunk = new ApplicationChunk (chunkh, raf, aiffHeader);
            // Any number of application chunks is ok
        }
        else if ("COMM".equals (id)) {
            // There should be no more than one of these
            chunk = new CommonChunk (chunkh, raf, aiffHeader);
        }
        else if ("COMT".equals (id)) {
            chunk = new CommentsChunk (chunkh, raf, aiffHeader);
        }
        else if ("NAME".equals (id)) {
            chunk = new NameChunk (chunkh, raf, aiffHeader);
        }
        else if ("AUTH".equals (id)) {
            chunk = new AuthorChunk (chunkh, raf, aiffHeader);
        }
        else if ("(c) ".equals (id)) {
            chunk = new CopyrightChunk (chunkh, raf, aiffHeader);
        }
        else if ("ANNO".equals (id)) {
            chunk = new AnnotationChunk (chunkh, raf, aiffHeader);
        }
        else if ("ID3 ".equals (id)) {
            chunk = new ID3Chunk (chunkh, raf, aiffTag);
        }

        if (chunk != null) {
            if (!chunk.readChunk ()) {
                return false;
            }
        }
        else {
            // Other chunk types are legal, just skip over them
            raf.skipBytes (chunkSize);
        }
        if ((chunkSize & 1) != 0) {
            // Must come out to an even byte boundary
            raf.skipBytes(1);
            --bytesRemaining;
        }
        return true;   
     }

}
