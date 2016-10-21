package org.jaudiotagger.audio.aiff;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jaudiotagger.audio.generic.Utils;

public class CommentsChunk extends Chunk {

    private AiffAudioHeader aiffHeader;
    
    /**
     * Constructor.
     * 
     * @param hdr      The header for this chunk
     * @param raf      The file from which the AIFF data are being read
     */
    public CommentsChunk (
            ChunkHeader hdr, 
            RandomAccessFile raf,
            AiffAudioHeader aHdr)
    {
        super (raf, hdr);
        aiffHeader = aHdr;
    }
    
    /** Reads a chunk and extracts information. 
     * 
     *  @return   <code>false</code> if the chunk is structurally
     *            invalid, otherwise <code>true</code>
     */
    public boolean readChunk () throws IOException
    {
        int numComments = Utils.readUint16(raf);
        // Create a List of comments
        for (int i = 0; i < numComments; i++) {
            long timestamp = Utils.readUint32(raf);
            Date jTimestamp = AiffUtil.timestampToDate (timestamp);
            int marker = Utils.readInt16 (raf);
            int count = Utils.readUint16 (raf);
            bytesLeft -= 8;
            byte[] buf = new byte[count];
            raf.read(buf);
            bytesLeft -= count;
            String cmt = new String(buf);
            
            // Append a timestamp to the comment
            cmt += " " + AiffUtil.formatDate(jTimestamp);
            aiffHeader.addComment (cmt);
        }
        return true;
    }

}
