package org.jaudiotagger.audio.aiff;

import java.io.IOException;
import java.io.RandomAccessFile;

import org.jaudiotagger.audio.generic.Utils;

public class CommonChunk extends Chunk {

    private AiffAudioHeader aiffHeader;
    
    /**
     * Constructor.
     * 
     * @param hdr      The header for this chunk
     * @param raf      The file from which the AIFF data are being read
     * @param aHdr     The AiffTag into which information is stored
     */
    public CommonChunk (
            ChunkHeader hdr, 
            RandomAccessFile raf,
            AiffAudioHeader aHdr)
    {
        super (raf, hdr);
        aiffHeader = aHdr;
    }
    
    
    @Override
    public boolean readChunk() throws IOException {
        int numChannels = Utils.readUint16(raf);
        long numSampleFrames = Utils.readUint32(raf);
        int sampleSize = Utils.readUint16(raf);
        bytesLeft -= 8;
        
        String compressionType = null;
        String compressionName = null;
        
        double sampleRate = AiffUtil.read80BitDouble (raf);
        bytesLeft -= 10;
         
        if (aiffHeader.getFileType () == AiffAudioHeader.FileType.AIFCTYPE) {
            if (bytesLeft == 0) {
                // This is a rather special case, but testing did turn up
                // a file that misbehaved in this way.
                return false;
            }
            compressionType = AiffUtil.read4Chars (raf);
            // According to David Ackerman, the compression type can
            // change the endianness of the document.
            if (compressionType.equals ("sowt")) {
               aiffHeader.setEndian (AiffAudioHeader.Endian.LITTLE_ENDIAN);
            }
            bytesLeft -= 4;
            compressionName = AiffUtil.readPascalString (raf);
            bytesLeft -= compressionName.length () + 1;
        }
        
        aiffHeader.setBitsPerSample (sampleSize);
        aiffHeader.setSamplingRate ((int) sampleRate);
        aiffHeader.setChannelNumber (numChannels);
        aiffHeader.setLength ((int)( numSampleFrames / sampleRate));
        aiffHeader.setPreciseLength((float) (numSampleFrames / sampleRate));
        aiffHeader.setLossless(true);   // for all known compression types
        // Proper handling of compression type should depend
        // on whether raw output is set
        if (compressionType != null) {
            if (compressionType.equals ("NONE")) {
            }
            else if (compressionType.equals ("raw ")) {
                compressionName = "PCM 8-bit offset-binary";
            }
            else if (compressionType.equals ("twos")) {
                compressionName = "PCM 16-bit twos-complement big-endian";
            }
            else if (compressionType.equals ("sowt")) {
                compressionName = "PCM 16-bit twos-complement little-endian";
            }
            else if (compressionType.equals ("fl32")) {
                compressionName = "PCM 32-bit integer";
            }
            else if (compressionType.equals ("fl64")) {
                compressionName = "PCM 64-bit floating point";
            }
            else if (compressionType.equals ("in24")) {
                compressionName = "PCM 24-bit integer";
            }
            else if (compressionType.equals ("in32")) {
                compressionName = "PCM 32-bit integer";
            }
            else {
                aiffHeader.setLossless(false);    // We don't know, so we have to assume lossy
            }
            aiffHeader.setAudioEncoding (compressionName);

                // The size of the data after compression isn't available
                // from the Common chunk, so we mark it as "unknown."
                // With a bit more sophistication, we could combine the
                // information from here and the Sound Data chunk to get
                // the effective byte rate, but we're about to release. 
            String name = compressionName;
            if (name == null || name.length () == 0) {
                name = compressionType;
            }
        }
        return true;
        
    }

}
