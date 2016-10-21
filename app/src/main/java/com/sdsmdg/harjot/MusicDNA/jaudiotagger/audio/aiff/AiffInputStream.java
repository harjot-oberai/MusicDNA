package org.jaudiotagger.audio.aiff;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 *  This class implements an InputStream over a RandomAccessFile for the
 *  sake of efficiency. The AIFF reader uses a RandomAccessFile for
 *  consistency with the other modules, but really just reads the file
 *  sequentially. This class permits reasonable buffering.
 */
public class AiffInputStream extends InputStream {

    private final static int BUFSIZE = 2048;
    /** The underlying file */
    private RandomAccessFile raf;
    
    /** The input buffer */
    private byte[] fileBuf;
    
    /** Number of valid bytes in the input buffer */
    private int fileBufSize;
    
    /** Current position in the input buffer */
    private int fileBufOffset;
    
    /** End of file flag */
    private boolean eof;
    
    public AiffInputStream (RandomAccessFile raf) {
        this.raf = raf;
        eof = false;
        fileBuf = new byte[BUFSIZE];
        fileBufSize = 0;
        fileBufOffset = 0;
    }
    
    
    @Override
    public int read() throws IOException {
        for (;;) {
            if (eof) {
                return -1;
            }
            if (fileBufOffset < fileBufSize) {
                return ((int) fileBuf[fileBufOffset++]) & 0XFF;
            }
            else {
                fillBuf ();
            }
        }
    }
    

    
    /* Refill the buffer */
    private void fillBuf () throws IOException  {
        int bytesRead = raf.read (fileBuf, 0, BUFSIZE);
        fileBufOffset = 0;
        fileBufSize = bytesRead;
        if (fileBufSize == 0) {
            eof = true;
        }
    }

}
