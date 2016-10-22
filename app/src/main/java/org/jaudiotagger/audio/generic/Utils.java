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
package org.jaudiotagger.audio.generic;

import org.jaudiotagger.audio.AudioFile;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Contains various frequently used static functions in the different tag
 * formats
 *
 * @author Raphael Slinckx
 */
public class Utils
{

     // Logger Object
    public static Logger logger = Logger
             .getLogger("org.jaudiotagger.audio.generic.utils");
    private static final int MAX_BASE_TEMP_FILENAME_LENGTH = 20;

    /**
     * Copies the bytes of <code>srd</code> to <code>dst</code> at the
     * specified offset.
     *
     * @param src       The byte to be copied.
     * @param dst       The array to copy to
     * @param dstOffset The start offset for the bytes to be copied.
     */
    public static void copy(byte[] src, byte[] dst, int dstOffset)
    {
        System.arraycopy(src, 0, dst, dstOffset, src.length);
    }


    /**
     * Returns {@link String#getBytes()}.<br>
     *
     * @param s The String to call, decode bytes using the specfied charset
     * @param charSet
     * @return The bytes.
     */
    public static byte[] getDefaultBytes(String s, String charSet)
    {
        try
        {
            return s.getBytes(charSet);
        }
        catch (UnsupportedEncodingException uee)
        {
            throw new RuntimeException(uee);
        }

    }

    /*
      * Returns the extension of the given file.
      * The extension is empty if there is no extension
      * The extension is the string after the last "."
      *
      * @param f The file whose extension is requested
      * @return The extension of the given file
      */
    public static String getExtension(File f)
    {
        String name = f.getName().toLowerCase();
        int i = name.lastIndexOf(".");
        if (i == -1)
        {
            return "";
        }

        return name.substring(i + 1);
    }


    /*
    * Computes a number whereby the 1st byte is the least signifcant and the last
    * byte is the most significant.
    *
    * @param b The byte array @param start The starting offset in b
    * (b[offset]). The less significant byte @param end The end index
    * (included) in b (b[end]). The most significant byte @return a long number
    * represented by the byte sequence.
    *
    * So if storing a number which only requires one byte it will be stored in the first
    * byte.
    */
    public static long getLongLE(ByteBuffer b, int start, int end)
    {
        long number = 0;
        for (int i = 0; i < (end - start + 1); i++)
        {
            number += ((b.get(start + i) & 0xFF) << i * 8);
        }

        return number;
    }

    /*
     * Computes a number whereby the 1st byte is the most significant and the last
     * byte is the least significant.
     *
     * So if storing a number which only requires one byte it will be stored in the last
     * byte.
     * 
     * Will fail if end - start >= 8, due to the limitations of the long type.
     */
    public static long getLongBE(ByteBuffer b, int start, int end)
    {
        long number = 0;
        for (int i = 0; i < (end - start + 1); i++)
        {
            number += ((long)((b.get(end - i) & 0xFF)) << i * 8);
        }

        return number;
    }

    /*
     * Computes a number whereby the 1st byte is the least significant and the last
     * byte is the most significant. This version doesn't take a length,
     * and it returns an int rather than a long.
     * 
     * @param b The byte array. Maximum length for valid results is 4 bytes.
     * 
     */
    public static int getIntLE(byte[] b)
    {
        return (int) getLongLE(ByteBuffer.wrap(b), 0, b.length - 1);
    }

    /*
      * Computes a number whereby the 1st byte is the least significant and the last
      * byte is the most significant. end - start must be no greater than 4.
      * 
      * @param b The byte array 
      * 
      * @param start The starting offset in b (b[offset]). The less
      * significant byte 
      * 
      * @param end The end index (included) in b (b[end])
      * 
      * @return a int number represented by the byte sequence.
      */
    public static int getIntLE(byte[] b, int start, int end)
    {
        return (int) getLongLE(ByteBuffer.wrap(b), start, end);
    }

    /*
     * Computes a number whereby the 1st byte is the most significant and the last
     * byte is the least significant.
     * 
     * @param b The byte array 
     * 
     * @param start The starting offset in b (b[offset]). The less
     * significant byte 
     * 
     * @param end The end index (included) in b (b[end])
     * 
     * @return an int number represented by the byte sequence.
     */
    public static int getIntBE(byte[] b, int start, int end)
    {
        return (int) getLongBE(ByteBuffer.wrap(b), start, end);
    }

    /*
     * Computes a number whereby the 1st byte is the most significant and the last
     * byte is the least significant.
     * 
     * @param b The ByteBuffer
     * 
     * @param start The starting offset in b. The less
     * significant byte 
     * 
     * @param end The end index (included) in b
     * 
     * @return an int number represented by the byte sequence.
     */
    public static int getIntBE(ByteBuffer b, int start, int end)
    {
        return (int) getLongBE(b, start, end);
    }

    /*
     * Computes a number whereby the 1st byte is the most significant and the last
     * byte is the least significant.
     * 
     * @param b The ByteBuffer
     * 
     * @param start The starting offset in b. The less
     * significant byte 
     * 
     * @param end The end index (included) in b
     * 
     * @return a short number represented by the byte sequence.
     */
    public static short getShortBE(ByteBuffer b, int start, int end)
    {
        return (short) getIntBE(b, start, end);
    }

    /**
     * Convert int to byte representation - Big Endian (as used by mp4)
     *
     * @param size
     * @return byte represenetation
     */
    public static byte[] getSizeBEInt32(int size)
    {
        byte[] b = new byte[4];
        b[0] = (byte) ((size >> 24) & 0xFF);
        b[1] = (byte) ((size >> 16) & 0xFF);
        b[2] = (byte) ((size >> 8) & 0xFF);
        b[3] = (byte) (size & 0xFF);
        return b;
    }

    /**
     * Convert short to byte representation - Big Endian (as used by mp4)
     *
     * @param size  number to convert
     * @return byte represenetation
     */
    public static byte[] getSizeBEInt16(short size)
    {
        byte[] b = new byte[2];
        b[0] = (byte) ((size >> 8) & 0xFF);
        b[1] = (byte) (size & 0xFF);
        return b;
    }


    /**
     * Convert int to byte representation - Little Endian (as used by ogg vorbis)
     *
     * @param size   number to convert
     * @return byte represenetation
     */
    public static byte[] getSizeLEInt32(int size)
    {
        byte[] b = new byte[4];
        b[0] = (byte) (size & 0xff);
        b[1] = (byte) ((size >>> 8) & 0xffL);
        b[2] = (byte) ((size >>> 16) & 0xffL);
        b[3] = (byte) ((size >>> 24) & 0xffL);
        return b;
    }

    /**
     * Create String starting from offset upto length using encoding
     *
     * @param b
     * @param offset
     * @param length
     * @param encoding
     * @return String
     * @throws RuntimeException
     */
    public static String getString(byte[] b, int offset, int length, String encoding)
    {
        try
        {
            return new String(b, offset, length, encoding);
        }
        catch (UnsupportedEncodingException ue)
        {
            //Shouldnt have to worry about this exception as should only be calling with well defined charsets
            throw new RuntimeException(ue);
        }
    }

    /**
     * Create String offset from position by offset upto length using encoding, and position of buffer
     * is moved to after position + offset + length
     *
     * @param buffer
     * @param offset
     * @param length
     * @param encoding
     * @return String
     * @throws RuntimeException
     */
    public static String getString(ByteBuffer buffer, int offset, int length, String encoding)
    {
        byte[] b = new byte[length];
        buffer.position(buffer.position() + offset);
        buffer.get(b);
        try
        {
            return new String(b, 0, length, encoding);
        }
        catch (UnsupportedEncodingException uee)
        {
            //TODO, will we ever use unsupported encodings
            throw new RuntimeException(uee);
        }
    }

    /*
      * Tries to convert a string into an UTF8 array of bytes If the conversion
      * fails, return the string converted with the default encoding.
      *
      * @param s The string to convert @return The byte array representation of
      * this string in UTF8 encoding
      */
    public static byte[] getUTF8Bytes(String s) throws UnsupportedEncodingException
    {
        return s.getBytes("UTF-8");
    }

    /**
     * Reads a 32-bit integer and returns it as a (signed) int.
     * Does overflow checking since java can't handle unsigned numbers.
     * @param di  The input source
     * 
     * @throws java.io.IOException
     * 
     * @return int
     */
    public static int readUint32AsInt(DataInput di) throws IOException
    {
        final long l = readUint32(di);
        if (l > Integer.MAX_VALUE)
        {
            throw new IOException("uint32 value read overflows int");
        }
        return (int) l;
    }

    /** Read a 32-bit big-endian unsigned integer using a DataInput. 
     */
    public static long readUint32(DataInput di) throws IOException
    {
        final byte[] buf8 = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        di.readFully(buf8, 4, 4);
        final long l = ByteBuffer.wrap(buf8).getLong();
        return l;
    }

    /** Read a 16-bit big-endian unsigned integer */
    public static int readUint16(DataInput di) throws IOException
    {
        final byte[] buf = {0x00, 0x00, 0x00, 0x00};
        di.readFully(buf, 2, 2);
        final int i = ByteBuffer.wrap(buf).getInt();
        return i;
    }

    /** Read a 16-bit big-endian signed integer */
    public static int readInt16(DataInput di) throws IOException
    {
        final byte[] buf = {0x00, 0x00};
        di.readFully(buf, 0, 2);
        final int i = ByteBuffer.wrap(buf).getShort();
        return i;
    }

    /** Read a string of a specified number of ASCII bytes */
    public static String readString(DataInput di, int charsToRead) throws IOException
    {
        final byte[] buf = new byte[charsToRead];
        di.readFully(buf);
        return new String(buf);
    }

    /** Read a 64-bit unsigned integer using an nio ByteBuffer */
    public static long readUInt64(ByteBuffer b)
    {
        long result = 0;
        result += (readUBEInt32(b) << 32);
        result += readUBEInt32(b);
        return result;
    }

    /** Read an unsigned big-endian 32-bit integer using an nio ByteBuffer */
    public static int readUBEInt32(ByteBuffer b)
    {
        int result = 0;
        result += readUBEInt16(b) << 16;
        result += readUBEInt16(b);
        return result;
    }

    /** Read an unsigned big-endian 24-bit integer using an
     *  nio ByteBuffer */
    public static int readUBEInt24(ByteBuffer b)
    {
        int result = 0;
        result += readUBEInt16(b) << 16;
        result += readUInt8(b);
        return result;
    }

    /** Read an unsigned big-endian 16-bit integer using an
     *  nio ByteBuffer */
    public static int readUBEInt16(ByteBuffer b)
    {
        int result = 0;
        result += readUInt8(b) << 8;
        result += readUInt8(b);
        return result;
    }

    /** Read an unsigned (endian-neutral) 8-bit integer using an
     *  nio ByteBuffer */
    public static int readUInt8(ByteBuffer b)
    {
        return read(b);
    }


    public static int read(ByteBuffer b)
    {
        int result = (b.get() & 0xFF);
        return result;
    }

    /**
     * Get a base for temp file, this should be long enough so that it easy to work out later what file the temp file
     * was created for if it is left lying round, but not ridiculously long as this can cause problems with max filename
     * limits and is not very useful
     *
     * @param file
     * @return
     */
    public static String getBaseFilenameForTempFile(File file)
    {
        String filename = getMinBaseFilenameAllowedForTempFile(file);
        if(filename.length()<= MAX_BASE_TEMP_FILENAME_LENGTH)
        {
           return filename;
        }
        return filename.substring(0,MAX_BASE_TEMP_FILENAME_LENGTH);
    }

    /**
     * @param file
     * @return filename with audioformat separator stripped of, lengthened to ensure not too small for valid tempfile
     *         creation.
     */
    public static String getMinBaseFilenameAllowedForTempFile(File file)
    {
        String s = AudioFile.getBaseFilename(file);
        if (s.length() >= 3)
        {
            return s;
        }
        if (s.length() == 1)
        {
            return s + "000";
        }
        else if (s.length() == 1)
        {
            return s + "00";
        }
        else if (s.length() == 2)
        {
            return s + "0";
        }
        return s;
    }

    /**
     * Rename file, and if normal rename fails, try copy and delete instead
     *
     * @param fromFile
     * @param toFile
     * @return
     */
    public static boolean rename(File fromFile, File toFile)
    {
        logger.log(Level.CONFIG,"Renaming From:"+fromFile.getAbsolutePath() + " to "+toFile.getAbsolutePath());

        if(toFile.exists())
        {
            logger.log(Level.SEVERE,"Destination File:"+toFile + " already exists");
            return false;
        }

        //Rename File, could fail because being  used or because trying to rename over filesystems
        final boolean result = fromFile.renameTo(toFile);
        if (!result)
        {
            // Might be trying to rename over filesystem, so try copy and delete instead
            if (copy(fromFile, toFile))
            {
                //If copy works but deletion of original file fails then it is because the file is being used
                //so we need to delete the file we have just created
                boolean deleteResult=fromFile.delete();
                if(!deleteResult)
                {
                    logger.log(Level.SEVERE,"Unable to delete File:"+fromFile);
                    toFile.delete();
                    return false;
                }
                return true;
            }
            else
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Copy a File
     *
     * @param fromFile The existing File
     * @param toFile   The new File
     * @return <code>true</code> if and only if the renaming succeeded;
     *         <code>false</code> otherwise
     */
    public static boolean copy(File fromFile, File toFile)
    {
        try
        {
            FileInputStream in = new FileInputStream(fromFile);
            FileOutputStream out = new FileOutputStream(toFile);
            byte[] buf = new byte[8192];

            int len;

            while ((len = in.read(buf)) > -1)
            {
                out.write(buf, 0, len);
            }

            in.close();
            out.close();

            // cleanup if files are not the same length
            if (fromFile.length() != toFile.length())
            {
                toFile.delete();

                return false;
            }

            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }
}
