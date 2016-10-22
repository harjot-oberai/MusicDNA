/**
 *  @author : Paul Taylor
 *  @author : Eric Farng
 *
 *  Version @version:$Id$
 *
 *  MusicTag Copyright (C)2003,2004
 *
 *  This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser
 *  General Public  License as published by the Free Software Foundation; either version 2.1 of the License,
 *  or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 *  the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this library; if not,
 *  you can get a copy from http://www.opensource.org/licenses/lgpl-license.php or write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 *
 * Description:
 *
 */
package org.jaudiotagger.tag.id3.framebody;

import org.jaudiotagger.tag.InvalidTagException;
import org.jaudiotagger.tag.datatype.DataTypes;
import org.jaudiotagger.tag.datatype.NumberHashMap;
import org.jaudiotagger.tag.datatype.TextEncodedStringNullTerminated;
import org.jaudiotagger.tag.datatype.TextEncodedStringSizeTerminated;
import org.jaudiotagger.tag.id3.ID3TextEncodingConversion;
import org.jaudiotagger.tag.id3.ID3v24Frames;
import org.jaudiotagger.tag.id3.valuepair.TextEncoding;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;


/**
 * User defined text information frame
 *
 * This frame is intended for one-string text information concerning the
 * audio file in a similar way to the other "T"-frames. The frame body
 * consists of a description of the string, represented as a terminated
 * string, followed by the actual string. There may be more than one
 * "TXXX" frame in each tag, but only one with the same description.
 *
 * <Header for 'User defined text information frame', ID: "TXXX">
 * Text encoding     $xx
 * Description       <text string according to encoding> $00 (00)
 * Value             <text string according to encoding>
 */
public class FrameBodyTXXX extends AbstractFrameBodyTextInfo implements ID3v24FrameBody, ID3v23FrameBody
{
    //Used by Picard and Jaikoz
    public static final String MUSICBRAINZ_ARTISTID         = "MusicBrainz Artist Id";
    public static final String MUSICBRAINZ_ALBUM_ARTISTID   = "MusicBrainz Album Artist Id";
    public static final String MUSICBRAINZ_ALBUMID          = "MusicBrainz Album Id";
    public static final String MUSICBRAINZ_ORIGINAL_ALBUMID = "MusicBrainz Original Album Id";
    public static final String MUSICBRAINZ_RELEASE_GROUPID  = "MusicBrainz Release Group Id";
    public static final String MUSICBRAINZ_RELEASE_TRACKID  = "MusicBrainz Release Track Id";
    public static final String MUSICBRAINZ_DISCID           = "MusicBrainz Disc Id";
    public static final String MUSICBRAINZ_ALBUM_TYPE       = "MusicBrainz Album Type";
    public static final String MUSICBRAINZ_ALBUM_STATUS     = "MusicBrainz Album Status";
    public static final String MUSICBRAINZ_ALBUM_COUNTRY    = "MusicBrainz Album Release Country";
    public static final String MUSICBRAINZ_WORKID           = "MusicBrainz Work Id";
    public static final String AMAZON_ASIN                    = "ASIN";
    public static final String MUSICIP_ID                   = "MusicIP PUID";
    public static final String BARCODE                      = "BARCODE";
    public static final String CATALOG_NO                   = "CATALOGNUMBER";
    public static final String MOOD                         = "MOOD";          //ID3 v23 only
    public static final String TAGS                         = "TAGS";
    public static final String FBPM                         = "FBPM";
    public static final String SCRIPT                       = "Script";
    public static final String ARTISTS                      = "ARTISTS";
    public static final String ACOUSTID_FINGERPRINT         = "Acoustid Fingerprint";
    public static final String ACOUSTID_ID                  = "Acoustid Id";
    public static final String COUNTRY                      = "Country";

    //used by Foobar 20000
    public static final String ALBUM_ARTIST = "ALBUM ARTIST";
    public static final String PERFORMER = "PERFORMER";

    /**
     * Creates a new FrameBodyTXXX datatype.
     */
    public FrameBodyTXXX()
    {
        this.setObjectValue(DataTypes.OBJ_TEXT_ENCODING, TextEncoding.ISO_8859_1);
        this.setObjectValue(DataTypes.OBJ_DESCRIPTION, "");
        this.setObjectValue(DataTypes.OBJ_TEXT, "");

    }

    /**
     * Convert from V4 TMOO Frame to V3 Frame
     * @param body
     */
    public FrameBodyTXXX(FrameBodyTMOO body)
    {
        setObjectValue(DataTypes.OBJ_TEXT_ENCODING, body.getTextEncoding());
        this.setObjectValue(DataTypes.OBJ_TEXT_ENCODING, TextEncoding.ISO_8859_1);
        this.setObjectValue(DataTypes.OBJ_DESCRIPTION, MOOD);
        this.setObjectValue(DataTypes.OBJ_TEXT, body.getText());
    }

    public FrameBodyTXXX(FrameBodyTXXX body)
    {
        super(body);
    }

    /**
     * Creates a new FrameBodyTXXX datatype.
     *
     * @param textEncoding
     * @param description
     * @param text
     */
    public FrameBodyTXXX(byte textEncoding, String description, String text)
    {
        this.setObjectValue(DataTypes.OBJ_TEXT_ENCODING, textEncoding);
        this.setObjectValue(DataTypes.OBJ_DESCRIPTION, description);
        this.setObjectValue(DataTypes.OBJ_TEXT, text);
    }

    /**
     * Creates a new FrameBodyTXXX datatype.
     *
     * @param byteBuffer
     * @param frameSize
     * @throws InvalidTagException
     */
    public FrameBodyTXXX(ByteBuffer byteBuffer, int frameSize) throws InvalidTagException
    {
        super(byteBuffer, frameSize);
    }

    /**
     * Set the description field
     *
     * @param description
     */
    public void setDescription(String description)
    {
        setObjectValue(DataTypes.OBJ_DESCRIPTION, description);
    }

    /**
     * @return the description field
     */
    public String getDescription()
    {
        return (String) getObjectValue(DataTypes.OBJ_DESCRIPTION);
    }

    /**
     * The ID3v2 frame identifier
     *
     * @return the ID3v2 frame identifier  for this frame type
     */
    public String getIdentifier()
    {
        return ID3v24Frames.FRAME_ID_USER_DEFINED_INFO;
    }

    /**
     * Because TXXX frames also have a text encoded description we need to check this as well.     *
     */
    public void write(ByteArrayOutputStream tagBuffer)
    {
        //Ensure valid for type
        setTextEncoding(ID3TextEncodingConversion.getTextEncoding(getHeader(), getTextEncoding()));

        //Ensure valid for description
        if (!((TextEncodedStringNullTerminated) getObject(DataTypes.OBJ_DESCRIPTION)).canBeEncoded())
        {
            this.setTextEncoding(ID3TextEncodingConversion.getUnicodeTextEncoding(getHeader()));
        }
        super.write(tagBuffer);
    }

    /**
     * This is different to other text Frames
     */
    protected void setupObjectList()
    {
        objectList.add(new NumberHashMap(DataTypes.OBJ_TEXT_ENCODING, this, TextEncoding.TEXT_ENCODING_FIELD_SIZE));
        objectList.add(new TextEncodedStringNullTerminated(DataTypes.OBJ_DESCRIPTION, this));
        objectList.add(new TextEncodedStringSizeTerminated(DataTypes.OBJ_TEXT, this));
    }

}
