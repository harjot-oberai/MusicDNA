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
package org.jaudiotagger.audio;

import org.jaudiotagger.audio.aiff.AiffFileReader;
import org.jaudiotagger.audio.asf.AsfFileReader;
import org.jaudiotagger.audio.asf.AsfFileWriter;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.flac.FlacFileReader;
import org.jaudiotagger.audio.flac.FlacFileWriter;
import org.jaudiotagger.audio.generic.*;
import org.jaudiotagger.audio.mp3.MP3FileReader;
import org.jaudiotagger.audio.mp3.MP3FileWriter;
import org.jaudiotagger.audio.mp4.Mp4FileReader;
import org.jaudiotagger.audio.mp4.Mp4FileWriter;
import org.jaudiotagger.audio.ogg.OggFileReader;
import org.jaudiotagger.audio.ogg.OggFileWriter;
import org.jaudiotagger.audio.real.RealFileReader;
import org.jaudiotagger.audio.wav.WavFileReader;
import org.jaudiotagger.audio.wav.WavFileWriter;
import org.jaudiotagger.logging.ErrorMessage;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

/**
 *
 * The main entry point for the Tag Reading/Writing operations, this class will
 * select the appropriate reader/writer for the given file.
 * 
 *
 * It selects the appropriate reader/writer based on the file extension (case
 * ignored).
 * 
 *
 * Here is an simple example of use:
 * 
 *
 * <code>
 * AudioFile audioFile = AudioFileIO.read(new File("audiofile.mp3")); //Reads the given file.
 * int bitrate = audioFile.getBitrate(); //Retreives the bitrate of the file.
 * String artist = audioFile.getTag().getFirst(TagFieldKey.ARTIST); //Retreive the artist name.
 * audioFile.getTag().setGenre("Progressive Rock"); //Sets the genre to Prog. Rock, note the file on disk is still unmodified.
 * AudioFileIO.write(audioFile); //Write the modifications in the file on disk.
 * </code>
 * 
 *
 * You can also use the <code>commit()</code> method defined for
 * <code>AudioFile</code>s to achieve the same goal as
 * <code>AudioFileIO.write(File)</code>, like this:
 * 
 *
 * <code>
 * AudioFile audioFile = AudioFileIO.read(new File("audiofile.mp3"));
 * audioFile.getTag().setGenre("Progressive Rock");
 * audioFile.commit(); //Write the modifications in the file on disk.
 * </code>
 * 
 *
 * @author Raphael Slinckx
 * @version $Id$
 * @see AudioFile
 * @see org.jaudiotagger.tag.Tag
 * @since v0.01
 */
public class AudioFileIO
{

    //Logger
    public static Logger logger = Logger.getLogger("org.jaudiotagger.audio");

    // !! Do not forget to also add new supported extensions to AudioFileFilter
    // !!

    /**
     * This field contains the default instance for static use.
     */
    private static AudioFileIO defaultInstance;

    /**
     *
     * Delete the tag, if any, contained in the given file.
     * 
     *
     * @param f The file where the tag will be deleted
     * @throws org.jaudiotagger.audio.exceptions.CannotWriteException If the file could not be written/accessed, the extension
     *                              wasn't recognized, or other IO error occurred.
     * @throws org.jaudiotagger.audio.exceptions.CannotReadException
     */
    public static void delete(AudioFile f) throws CannotReadException, CannotWriteException
    {
        getDefaultAudioFileIO().deleteTag(f);
    }

    /**
     * This method returns the default instance for static use.<br>
     *
     * @return The default instance.
     */
    public static AudioFileIO getDefaultAudioFileIO()
    {
        if (defaultInstance == null)
        {
            defaultInstance = new AudioFileIO();
        }
        return defaultInstance;
    }

    /**
     *
     * Read the tag contained in the given file.
     * 
     *
     * @param f The file to read.
     * @return The AudioFile with the file tag and the file encoding info.
     * @throws org.jaudiotagger.audio.exceptions.CannotReadException If the file could not be read, the extension wasn't
     *                             recognized, or an IO error occurred during the read.
     * @throws org.jaudiotagger.tag.TagException
     * @throws org.jaudiotagger.audio.exceptions.ReadOnlyFileException
     * @throws java.io.IOException
     * @throws org.jaudiotagger.audio.exceptions.InvalidAudioFrameException
     */
    public static AudioFile read(File f)
            throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException
    {
        return getDefaultAudioFileIO().readFile(f);
    }

    /**
     *
     * Write the tag contained in the audioFile in the actual file on the disk.
     * 
     *
     * @param f The AudioFile to be written
     * @throws CannotWriteException If the file could not be written/accessed, the extension
     *                              wasn't recognized, or other IO error occurred.
     */
    public static void write(AudioFile f) throws CannotWriteException
    {
        getDefaultAudioFileIO().writeFile(f);
    }

    /**
     * This member is used to broadcast modification events to registered
     */
    private final ModificationHandler modificationHandler;

    // These tables contains all the readers/writers associated with extension
    // as a key
    private Map<String, AudioFileReader> readers = new HashMap<String, AudioFileReader>();
    private Map<String, AudioFileWriter> writers = new HashMap<String, AudioFileWriter>();


    /**
     * Creates an instance.
     */
    public AudioFileIO()
    {
        this.modificationHandler = new ModificationHandler();
        prepareReadersAndWriters();
    }

    /**
     * Adds an listener for all file formats.
     *
     * @param listener listener
     */
    public void addAudioFileModificationListener(
            AudioFileModificationListener listener)
    {
        this.modificationHandler.addAudioFileModificationListener(listener);
    }

    /**
     *
     * Delete the tag, if any, contained in the given file.
     * 
     *
     * @param f The file where the tag will be deleted
     * @throws org.jaudiotagger.audio.exceptions.CannotWriteException If the file could not be written/accessed, the extension
     *                              wasn't recognized, or other IO error occurred.
     * @throws org.jaudiotagger.audio.exceptions.CannotReadException
     */
    public void deleteTag(AudioFile f) throws CannotReadException, CannotWriteException
    {
        String ext = Utils.getExtension(f.getFile());

        Object afw = writers.get(ext);
        if (afw == null)
        {
            throw new CannotWriteException(ErrorMessage.NO_DELETER_FOR_THIS_FORMAT.getMsg(ext));
        }

        ((AudioFileWriter) afw).delete(f);
    }

    /**
     * Creates the readers and writers.
     */
    private void prepareReadersAndWriters()
    {

        // Tag Readers
        readers.put(SupportedFileFormat.OGG.getFilesuffix(), new OggFileReader());
        readers.put(SupportedFileFormat.FLAC.getFilesuffix(),new FlacFileReader());
        readers.put(SupportedFileFormat.MP3.getFilesuffix(), new MP3FileReader());
        readers.put(SupportedFileFormat.MP4.getFilesuffix(), new Mp4FileReader());
        readers.put(SupportedFileFormat.M4A.getFilesuffix(), new Mp4FileReader());
        readers.put(SupportedFileFormat.M4P.getFilesuffix(), new Mp4FileReader());
        readers.put(SupportedFileFormat.M4B.getFilesuffix(), new Mp4FileReader());
        readers.put(SupportedFileFormat.WAV.getFilesuffix(), new WavFileReader());
        readers.put(SupportedFileFormat.WMA.getFilesuffix(), new AsfFileReader());
        readers.put(SupportedFileFormat.AIF.getFilesuffix(), new AiffFileReader());
        final RealFileReader realReader = new RealFileReader();
        readers.put(SupportedFileFormat.RA.getFilesuffix(), realReader);
        readers.put(SupportedFileFormat.RM.getFilesuffix(), realReader);

        // Tag Writers
        writers.put(SupportedFileFormat.OGG.getFilesuffix(), new OggFileWriter());
        writers.put(SupportedFileFormat.FLAC.getFilesuffix(), new FlacFileWriter());
        writers.put(SupportedFileFormat.MP3.getFilesuffix(), new MP3FileWriter());
        writers.put(SupportedFileFormat.MP4.getFilesuffix(), new Mp4FileWriter());
        writers.put(SupportedFileFormat.M4A.getFilesuffix(), new Mp4FileWriter());
        writers.put(SupportedFileFormat.M4P.getFilesuffix(), new Mp4FileWriter());
        writers.put(SupportedFileFormat.M4B.getFilesuffix(), new Mp4FileWriter());                
        writers.put(SupportedFileFormat.WAV.getFilesuffix(), new WavFileWriter());
        writers.put(SupportedFileFormat.WMA.getFilesuffix(), new AsfFileWriter());

        // Register modificationHandler
        Iterator<AudioFileWriter> it = writers.values().iterator();
        for (AudioFileWriter curr : writers.values())
        {
            curr.setAudioFileModificationListener(this.modificationHandler);
        }
    }

    /**
     *
     * Read the tag contained in the given file.
     * 
     *
     * @param f The file to read.
     * @return The AudioFile with the file tag and the file encoding info.
     * @throws org.jaudiotagger.audio.exceptions.CannotReadException If the file could not be read, the extension wasn't
     *                             recognized, or an IO error occurred during the read.
     * @throws org.jaudiotagger.tag.TagException
     * @throws org.jaudiotagger.audio.exceptions.ReadOnlyFileException
     * @throws java.io.IOException
     * @throws org.jaudiotagger.audio.exceptions.InvalidAudioFrameException
     */
    public AudioFile readFile(File f)
            throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException
    {
        checkFileExists(f);
        String ext = Utils.getExtension(f);

        AudioFileReader afr = readers.get(ext);
        if (afr == null)
        {
            throw new CannotReadException(ErrorMessage.NO_READER_FOR_THIS_FORMAT.getMsg(ext));
        }

        return afr.read(f);
    }

    /**
     * Check does file exist
     *
     * @param file
     * @throws java.io.FileNotFoundException
     */
    public void checkFileExists(File file)throws FileNotFoundException
    {
        logger.config("Reading file:" + "path" + file.getPath() + ":abs:" + file.getAbsolutePath());
        if (!file.exists())
        {
            logger.severe("Unable to find:" + file.getPath());
            throw new FileNotFoundException(ErrorMessage.UNABLE_TO_FIND_FILE.getMsg(file.getPath()));
        }
    }
    /**
     * Removes a listener for all file formats.
     *
     * @param listener listener
     */
    public void removeAudioFileModificationListener(
            AudioFileModificationListener listener)
    {
        this.modificationHandler.removeAudioFileModificationListener(listener);
    }

    /**
     *
     * Write the tag contained in the audioFile in the actual file on the disk.
     * 
     *
     * @param f The AudioFile to be written
     * @throws CannotWriteException If the file could not be written/accessed, the extension
     *                              wasn't recognized, or other IO error occurred.
     */
    public void writeFile(AudioFile f) throws CannotWriteException
    {
        String ext = Utils.getExtension(f.getFile());

        AudioFileWriter afw = writers.get(ext);
        if (afw == null)
        {
            throw new CannotWriteException(ErrorMessage.NO_WRITER_FOR_THIS_FORMAT.getMsg(ext));
        }

        afw.write(f);
    }
}
