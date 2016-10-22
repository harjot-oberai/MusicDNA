/*
 * Entagged Audio Tag library
 * Copyright (c) 2003-2005 Raphaël Slinckx <raphael@slinckx.net>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jaudiotagger.audio.flac;

import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.flac.metadatablock.*;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagOptionSingleton;
import org.jaudiotagger.tag.flac.FlacTag;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


/**
 * Write Flac Tag
 */
public class FlacTagWriter
{
    // Logger Object
    public static Logger logger = Logger.getLogger("org.jaudiotagger.audio.flac");

    private FlacTagCreator tc = new FlacTagCreator();

    /**
     * Delete Tag from file
     *
     * @param raf
     * @param tempRaf
     * @throws IOException
     * @throws CannotWriteException
     */
    public void delete(RandomAccessFile raf, RandomAccessFile tempRaf) throws IOException, CannotWriteException
    {
        //This will save the file without any Comment or PictureData blocks  
        FlacTag emptyTag = new FlacTag(null, new ArrayList<MetadataBlockDataPicture>());
        raf.seek(0);
        tempRaf.seek(0);
        write(emptyTag, raf, tempRaf);
    }

    private static class MetadataBlockInfo {
	    private MetadataBlock       streamInfoBlock;
	    private List<MetadataBlock> metadataBlockPadding = new ArrayList<MetadataBlock>(1);
	    private List<MetadataBlock> metadataBlockApplication = new ArrayList<MetadataBlock>(1);
	    private List<MetadataBlock> metadataBlockSeekTable = new ArrayList<MetadataBlock>(1);
	    private List<MetadataBlock> metadataBlockCueSheet = new ArrayList<MetadataBlock>(1);
    }

    /**
     * Write tag to file
     *
     * @param tag
     * @param raf
     * @param rafTemp
     * @throws CannotWriteException
     * @throws IOException
     */
    public void write(Tag tag, RandomAccessFile raf, RandomAccessFile rafTemp) throws CannotWriteException, IOException
    {
        logger.config("Writing tag");

        MetadataBlockInfo blockInfo = new MetadataBlockInfo();
        
        //Read existing data
        FlacStreamReader flacStream = new FlacStreamReader(raf);
        try
        {
            flacStream.findStream();
        }
        catch (CannotReadException cre)
        {
            throw new CannotWriteException(cre.getMessage());
        }

        boolean isLastBlock = false;
        while (!isLastBlock)
        {
            try
            {
                MetadataBlockHeader mbh = MetadataBlockHeader.readHeader(raf);
                if(mbh.getBlockType()!=null)
                {
                    switch (mbh.getBlockType())
                    {
                        case STREAMINFO:
                        {
                            blockInfo.streamInfoBlock = new MetadataBlock(mbh,new MetadataBlockDataStreamInfo(mbh, raf));
                            break;
                        }

                        case VORBIS_COMMENT:
                        case PADDING:
                        case PICTURE:
                        {
                            //All these will be replaced by the new metadata so we just treat as padding in order
                            //to determine how much space is already allocated in the file
                            raf.seek(raf.getFilePointer() + mbh.getDataLength());
                            MetadataBlockData mbd = new MetadataBlockDataPadding(mbh.getDataLength());
                            blockInfo.metadataBlockPadding.add(new MetadataBlock(mbh, mbd));
                            break;
                        }
                        case APPLICATION:
                        {
                            MetadataBlockData mbd = new MetadataBlockDataApplication(mbh, raf);
                            blockInfo.metadataBlockApplication.add(new MetadataBlock(mbh, mbd));
                            break;
                        }
                        case SEEKTABLE:
                        {
                            MetadataBlockData mbd = new MetadataBlockDataSeekTable(mbh, raf);
                            blockInfo.metadataBlockSeekTable.add(new MetadataBlock(mbh, mbd));
                            break;
                        }
                        case CUESHEET:
                        {
                            MetadataBlockData mbd = new MetadataBlockDataCueSheet(mbh, raf);
                            blockInfo.metadataBlockCueSheet.add(new MetadataBlock(mbh, mbd));
                            break;
                        }
                        default:
                        {
                            //What are the consequences of doing this
                            raf.seek(raf.getFilePointer() + mbh.getDataLength());
                            break;
                        }
                    }
                }
                isLastBlock = mbh.isLastBlock();
            }
            catch(CannotReadException cre)
            {
                throw new CannotWriteException(cre.getMessage());
            }
        }

        //Number of bytes in the existing file available before audio data
        int availableRoom = computeAvailableRoom(blockInfo);

        //Minimum Size of the New tag data without padding         
        int newTagSize = tc.convert(tag).limit();

        //Number of bytes required for new tagdata and other metadata blocks
        int neededRoom = newTagSize + computeNeededRoom(blockInfo);

        //Go to start of Flac within file
        raf.seek(flacStream.getStartOfFlacInFile());

        logger.config("Writing tag available bytes:" + availableRoom + ":needed bytes:" + neededRoom);

        //There is enough room to fit the tag without moving the audio just need to
        //adjust padding accordingly need to allow space for padding header if padding required
        if ((availableRoom == neededRoom) || (availableRoom > neededRoom + MetadataBlockHeader.HEADER_LENGTH))
        {
            //Jump over Id3 (if exists) Flac and StreamInfoBlock
            raf.seek(flacStream.getStartOfFlacInFile() + FlacStreamReader.FLAC_STREAM_IDENTIFIER_LENGTH);

            //Write StreamInfo, we always write this first even if wasn't first in original spec
            raf.write(blockInfo.streamInfoBlock.getHeader().getBytesWithoutIsLastBlockFlag());
            raf.write(blockInfo.streamInfoBlock.getData().getBytes());

            //Write Application Blocks
            for (MetadataBlock aMetadataBlockApplication : blockInfo.metadataBlockApplication)
            {
                raf.write(aMetadataBlockApplication.getHeader().getBytesWithoutIsLastBlockFlag());
                raf.write(aMetadataBlockApplication.getData().getBytes());
            }

            //Write Seek Table Blocks
            for (MetadataBlock aMetadataBlockSeekTable : blockInfo.metadataBlockSeekTable)
            {
                raf.write(aMetadataBlockSeekTable.getHeader().getBytesWithoutIsLastBlockFlag());
                raf.write(aMetadataBlockSeekTable.getData().getBytes());
            }

            //Write Cue sheet Blocks
            for (MetadataBlock aMetadataBlockCueSheet : blockInfo.metadataBlockCueSheet)
            {
                raf.write(aMetadataBlockCueSheet.getHeader().getBytesWithoutIsLastBlockFlag());
                raf.write(aMetadataBlockCueSheet.getData().getBytes());
            }

            //Write tag (and padding)
            raf.getChannel().write(tc.convert(tag, availableRoom - neededRoom));
        }
        //Need to move audio
        else
        {
            //Skip to start of Audio

            //If Flac tag contains ID3header or something before start of official Flac header copy it over
            if(flacStream.getStartOfFlacInFile()>0)
            {
                raf.seek(0);
                rafTemp.getChannel().transferFrom(raf.getChannel(), 0, flacStream.getStartOfFlacInFile());
                rafTemp.seek(flacStream.getStartOfFlacInFile());
            }
            rafTemp.writeBytes(FlacStreamReader.FLAC_STREAM_IDENTIFIER);
            rafTemp.writeByte(0);  //To ensure never set Last-metadata-block flag even if was before

            int uptoStreamHeaderSize = flacStream.getStartOfFlacInFile()
                    + FlacStreamReader.FLAC_STREAM_IDENTIFIER_LENGTH
                    + MetadataBlockHeader.BLOCK_TYPE_LENGTH;
            rafTemp.seek(uptoStreamHeaderSize);
            raf.seek(uptoStreamHeaderSize);

            rafTemp.getChannel().transferFrom(
                    raf.getChannel(),
                    uptoStreamHeaderSize,
                    MetadataBlockHeader.BLOCK_LENGTH + MetadataBlockDataStreamInfo.STREAM_INFO_DATA_LENGTH);

            int dataStartSize = flacStream.getStartOfFlacInFile()
                    + FlacStreamReader.FLAC_STREAM_IDENTIFIER_LENGTH
                    + MetadataBlockHeader.HEADER_LENGTH
                    + MetadataBlockDataStreamInfo.STREAM_INFO_DATA_LENGTH;
            rafTemp.seek(dataStartSize);

            //Write all the metadatablocks
            for (MetadataBlock aMetadataBlockApplication : blockInfo.metadataBlockApplication)
            {
                rafTemp.write(aMetadataBlockApplication.getHeader().getBytesWithoutIsLastBlockFlag());
                rafTemp.write(aMetadataBlockApplication.getData().getBytes());
            }

            for (MetadataBlock aMetadataBlockSeekTable : blockInfo.metadataBlockSeekTable)
            {
                rafTemp.write(aMetadataBlockSeekTable.getHeader().getBytesWithoutIsLastBlockFlag());
                rafTemp.write(aMetadataBlockSeekTable.getData().getBytes());
            }

            for (MetadataBlock aMetadataBlockCueSheet : blockInfo.metadataBlockCueSheet)
            {
                rafTemp.write(aMetadataBlockCueSheet.getHeader().getBytesWithoutIsLastBlockFlag());
                rafTemp.write(aMetadataBlockCueSheet.getData().getBytes());
            }

            //Write tag data use default padding
            rafTemp.write(tc.convert(tag, FlacTagCreator.DEFAULT_PADDING).array());
            //Write audio to new file
            raf.seek(dataStartSize + availableRoom);

            //Issue #385
            //Transfer 'size' bytes from raf at its current position to rafTemp at position but do it in batches
            //to prevent OutOfMemory exceptions
            long amountToBeWritten=raf.getChannel().size() - raf.getChannel().position();
            long written   = 0;
            long chunksize = TagOptionSingleton.getInstance().getWriteChunkSize();
            long count = amountToBeWritten / chunksize;
            long mod   = amountToBeWritten % chunksize;
            for(int i = 0; i<count; i++)
            {
                written+=rafTemp.getChannel().transferFrom(raf.getChannel(), rafTemp.getChannel().position(), chunksize);
                rafTemp.getChannel().position(rafTemp.getChannel().position() + chunksize);
            }
            written+=rafTemp.getChannel().transferFrom(raf.getChannel(), rafTemp.getChannel().position(), mod);
            if(written!=amountToBeWritten)
            {
                throw new CannotWriteException("Was meant to write "+amountToBeWritten+" bytes but only written "+written+" bytes");
            }
        }
    }

    /**
     * @param blockInfo 
     * @return space currently available for writing all Flac metadatablocks except for StreamInfo which is fixed size
     */
    private int computeAvailableRoom(MetadataBlockInfo blockInfo)
    {
        int length = 0;

        for (MetadataBlock aMetadataBlockApplication : blockInfo.metadataBlockApplication)
        {
            length += aMetadataBlockApplication.getLength();
        }

        for (MetadataBlock aMetadataBlockSeekTable : blockInfo.metadataBlockSeekTable)
        {
            length += aMetadataBlockSeekTable.getLength();
        }

        for (MetadataBlock aMetadataBlockCueSheet : blockInfo.metadataBlockCueSheet)
        {
            length += aMetadataBlockCueSheet.getLength();
        }

        for (MetadataBlock aMetadataBlockPadding : blockInfo.metadataBlockPadding)
        {
            length += aMetadataBlockPadding.getLength();
        }

        return length;
    }

    /**
     * @param blockInfo 
     * @return space required to write the metadata blocks that are part of Flac but are not part of tagdata
     *         in the normal sense.
     */
    private int computeNeededRoom(MetadataBlockInfo blockInfo)
    {
        int length = 0;

        for (MetadataBlock aMetadataBlockApplication : blockInfo.metadataBlockApplication)
        {
            length += aMetadataBlockApplication.getLength();
        }

        for (MetadataBlock aMetadataBlockSeekTable : blockInfo.metadataBlockSeekTable)
        {
            length += aMetadataBlockSeekTable.getLength();
        }

        for (MetadataBlock aMetadataBlockCueSheet : blockInfo.metadataBlockCueSheet)
        {
            length += aMetadataBlockCueSheet.getLength();
        }

        return length;
    }
}

