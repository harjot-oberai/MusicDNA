package org.jaudiotagger.audio.aiff;

import java.io.IOException;
import java.io.RandomAccessFile;

public class ChunkHeader {

    private long _size;              // This does not include the 8 bytes of header
    private String _chunkID;         // 4-character ID of the chunk
    
    /**
     *  Constructor.
     * 
     */
    public ChunkHeader ()
    {
    }
    
    
    /**
     *  Reads the header of a chunk.  If _chunkID is non-null,
     *  it's assumed to have already been read.
     */
    public boolean readHeader (RandomAccessFile raf) throws IOException
    {
        StringBuffer id = new StringBuffer(4);
        for (int i = 0; i < 4; i++) {
            int ch = raf.read();
            if (ch < 32) {
                String hx = Integer.toHexString (ch);
                if (hx.length () < 2) {
                    hx = "0" + hx;
                }
                return false;
            }
            id.append((char) ch);
        }
        _chunkID = id.toString ();
        _size = AiffUtil.readUINT32 (raf); 
        return true;
    }
    
    
    /** Sets the chunk type, which is a 4-character code, directly. */
    public void setID (String id)
    {
        _chunkID = id;
    }
    
    /** Returns the chunk type, which is a 4-character code */
    public String getID ()
    {
        return _chunkID;
    }
    
    /** Returns the chunk size (excluding the first 8 bytes) */
    public long getSize ()
    {
        return _size;
    }
}
