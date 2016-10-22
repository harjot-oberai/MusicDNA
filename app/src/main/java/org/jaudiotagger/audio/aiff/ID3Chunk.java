package org.jaudiotagger.audio.aiff;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.logging.ErrorMessage;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.aiff.AiffTag;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.ID3v22Tag;
import org.jaudiotagger.tag.id3.ID3v23Tag;
import org.jaudiotagger.tag.id3.ID3v24Tag;

public class ID3Chunk extends Chunk {

    private AiffTag aiffTag;
    
    /**
     * Constructor.
     * 
     * @param hdr      The header for this chunk
     * @param raf      The file from which the AIFF data are being read
     * @param tag      The AiffTag into which information is stored
     */
    public ID3Chunk (
            ChunkHeader hdr, 
            RandomAccessFile raf,
            AiffTag tag)
    {
        super (raf, hdr);
        aiffTag = tag;
    }

    @Override
    public boolean readChunk() throws IOException {
        // TODO Auto-generated method stub
        if (!isId3v2Tag()) {
            return false;    // Bad ID3V2 tag
        }
        int version = raf.read();
        AbstractID3v2Tag id3Tag;
        switch (version) {
        case 2:
            id3Tag = new ID3v22Tag();
            AudioFile.logger.finest("Reading ID3V2.2 tag");
            break;
        case 3:
            id3Tag = new ID3v23Tag();
            AudioFile.logger.finest("Reading ID3V2.3 tag");
            break;
        case 4:
            id3Tag = new ID3v24Tag();
            AudioFile.logger.finest("Reading ID3V2.4 tag");
            break;
        default:
            return false;     // bad or unknown version    
        }
        aiffTag.setID3Tag(id3Tag);
        raf.seek(raf.getFilePointer() - 4);    // back up to start of tag
        byte[] buf = new byte[(int)bytesLeft];
        raf.read(buf);
        ByteBuffer bb = ByteBuffer.allocate((int) bytesLeft);
        bb.put(buf);
        try {
            id3Tag.read(bb);
        }
        catch (TagException e) {
            AudioFile.logger.info("Exception reading ID3 tag: " + e.getClass().getName()
                     + ": " + e.getMessage());
            return false;
        }
        return true;
    }
    
    
    /**
     * @param rawdata
     *
     * @throws IOException
     * @throws CannotReadException
     */
    public void parse(byte[] rawdata, AiffTag aiffTag) throws IOException, CannotReadException
    {



    }

    /** Reads 3 bytes to determine if the tag really looks like ID3 data. */
    private boolean isId3v2Tag() throws IOException
    {
        byte buf[] = new byte[3];
        raf.read(buf);
        String id = new String(buf, "ASCII");
        return "ID3".equals (id);
    }

}
