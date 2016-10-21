package org.jaudiotagger.tag.id3;

import org.jaudiotagger.audio.generic.Utils;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.TagTextField;

import java.io.UnsupportedEncodingException;

/**
 * This class encapsulates the name and content of a tag entry in id3 fields
 * <br>
 *
 * @author @author Raphael Slinckx (KiKiDonK)
 * @author Christian Laireiter (liree)
 */
public class ID3v1TagField implements TagTextField
{

    /**
     * If <code>true</code>, the id of the current encapsulated tag field is
     * specified as a common field. <br>
     * Example is "ARTIST" which should be interpreted by any application as the
     * artist of the media content. <br>
     * Will be set during construction with {@link #checkCommon()}.
     */
    private boolean common;

    /**
     * Stores the content of the tag field. <br>
     */
    private String content;

    /**
     * Stores the id (name) of the tag field. <br>
     */
    private String id;

    /**
     * Creates an instance.
     *
     * @param raw Raw byte data of the tagfield.
     * @throws UnsupportedEncodingException If the data doesn't conform "UTF-8" specification.
     */
    public ID3v1TagField(byte[] raw) throws UnsupportedEncodingException
    {
        String field = new String(raw, "ISO-8859-1");

        int i = field.indexOf("=");
        if (i == -1)
        {
            //Beware that ogg ID, must be capitalized and contain no space..
            this.id = "ERRONEOUS";
            this.content = field;
        }
        else
        {
            this.id = field.substring(0, i).toUpperCase();
            if (field.length() > i)
            {
                this.content = field.substring(i + 1);
            }
            else
            {
                //We have "XXXXXX=" with nothing after the "="
                this.content = "";
            }
        }
        checkCommon();
    }

    /**
     * Creates an instance.
     *
     * @param fieldId      ID (name) of the field.
     * @param fieldContent Content of the field.
     */
    public ID3v1TagField(String fieldId, String fieldContent)
    {
        this.id = fieldId.toUpperCase();
        this.content = fieldContent;
        checkCommon();
    }

    /**
     * This method examines the ID of the current field and modifies
     * {@link #common}in order to reflect if the tag id is a commonly used one.
     * <br>
     */
    private void checkCommon()
    {
        this.common = id.equals(ID3v1FieldKey.TITLE.name()) || id.equals(ID3v1FieldKey.ALBUM.name()) || id.equals(ID3v1FieldKey.ARTIST.name()) || id.equals(ID3v1FieldKey.GENRE.name()) || id.equals(ID3v1FieldKey.YEAR.name()) || id.equals(ID3v1FieldKey.COMMENT.name()) || id.equals(ID3v1FieldKey.TRACK.name());
    }

    /**
     * This method will copy all bytes of <code>src</code> to <code>dst</code>
     * at the specified location.
     *
     * @param src       bytes to copy.
     * @param dst       where to copy to.
     * @param dstOffset at which position of <code>dst</code> the data should be
     *                  copied.
     */
    protected void copy(byte[] src, byte[] dst, int dstOffset)
    {
        //        for (int i = 0; i < src.length; i++)
        //            dst[i + dstOffset] = src[i];
        /*
         * Heared that this method is optimized and does its job very near of
         * the system.
         */
        System.arraycopy(src, 0, dst, dstOffset, src.length);
    }

    /**
     * @see TagField#copyContent(TagField)
     */
    public void copyContent(TagField field)
    {
        if (field instanceof TagTextField)
        {
            this.content = ((TagTextField) field).getContent();
        }
    }

    /**
     * @see TagTextField#getContent()
     */
    public String getContent()
    {
        return content;
    }

    /**
     * @see TagTextField#getEncoding()
     */
    public String getEncoding()
    {
        return "ISO-8859-1";
    }

    /**
     * @see TagField#getId()
     */
    public String getId()
    {
        return this.id;
    }

    /**
     * @see TagField#getRawContent()
     */
    public byte[] getRawContent() throws UnsupportedEncodingException
    {
        byte[] size = new byte[4];
        byte[] idBytes = this.id.getBytes("ISO-8859-1");
        byte[] contentBytes = Utils.getDefaultBytes(this.content, "ISO-8859-1");
        byte[] b = new byte[4 + idBytes.length + 1 + contentBytes.length];

        int length = idBytes.length + 1 + contentBytes.length;
        size[3] = (byte) ((length & 0xFF000000) >> 24);
        size[2] = (byte) ((length & 0x00FF0000) >> 16);
        size[1] = (byte) ((length & 0x0000FF00) >> 8);
        size[0] = (byte) (length & 0x000000FF);

        int offset = 0;
        copy(size, b, offset);
        offset += 4;
        copy(idBytes, b, offset);
        offset += idBytes.length;
        b[offset] = (byte) 0x3D;
        offset++;// "="
        copy(contentBytes, b, offset);

        return b;
    }

    /**
     * @see TagField#isBinary()
     */
    public boolean isBinary()
    {
        return false;
    }

    /**
     * @see TagField#isBinary(boolean)
     */
    public void isBinary(boolean b)
    {
        //Do nothing, always false
    }

    /**
     * @see TagField#isCommon()
     */
    public boolean isCommon()
    {
        return common;
    }

    /**
     * @see TagField#isEmpty()
     */
    public boolean isEmpty()
    {
        return this.content.equals("");
    }

    /**
     * @see TagTextField#setContent(String)
     */
    public void setContent(String s)
    {
        this.content = s;
    }

    /**
     * @see TagTextField#setEncoding(String)
     */
    public void setEncoding(String s)
    {
        //Do nothing, encoding is always ISO-8859-1 for this tag
    }

    public String toString()
    {
        return getContent();
    }
}
