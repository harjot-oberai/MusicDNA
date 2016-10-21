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
package org.jaudiotagger.tag.mp4.field;

import org.jaudiotagger.audio.mp4.atom.Mp4BoxHeader;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.TagTextField;
import org.jaudiotagger.tag.mp4.Mp4TagField;
import org.jaudiotagger.tag.mp4.atom.Mp4DataBox;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * Represents a single text field
 *
 * <p>Mp4 metadata normally held as follows:
 * <pre>
 * MP4Box Parent contains
 *      :length (includes length of data child)  (4 bytes)
 *      :name         (4 bytes)
 *      :child with
 *          :length          (4 bytes)
 *          :name 'Data'     (4 bytes)
 *          :atom version    (1 byte)
 *          :atom type flags (3 bytes)
 *          :null field      (4 bytes)
 *          :data
 * </pre>
 *
 * <p>Note:This class is initilized with the child data atom only, the parent data has already been processed, this may
 * change as it seems that code should probably be enscapulated into this. Whereas the raw content returned by the
 * getRawContent() contais the byte data for parent and child.
 */
public class Mp4TagTextField extends Mp4TagField implements TagTextField
{
    protected int dataSize;
    protected String content;

    /**
     * Construct from File
     *
     * @param id   parent id
     * @param data atom data
     * @throws UnsupportedEncodingException
     */
    public Mp4TagTextField(String id, ByteBuffer data) throws UnsupportedEncodingException
    {
        super(id, data);
    }

    /**
     * Construct new Field
     *
     * @param id      parent id
     * @param content data atom data
     */
    public Mp4TagTextField(String id, String content)
    {
        super(id);
        this.content = content;
    }

    protected void build(ByteBuffer data) throws UnsupportedEncodingException
    {
        //Data actually contains a 'Data' Box so process data using this
        Mp4BoxHeader header = new Mp4BoxHeader(data);
        Mp4DataBox databox = new Mp4DataBox(header, data);
        dataSize = header.getDataLength();
        content = databox.getContent();
    }

    public void copyContent(TagField field)
    {
        if (field instanceof Mp4TagTextField)
        {
            this.content = ((Mp4TagTextField) field).getContent();
        }
    }

    public String getContent()
    {
        return content;
    }

    protected byte[] getDataBytes() throws UnsupportedEncodingException
    {
        return content.getBytes(getEncoding());
    }

    public Mp4FieldType getFieldType()
    {
        return Mp4FieldType.TEXT;
    }

    public String getEncoding()
    {
        return Mp4BoxHeader.CHARSET_UTF_8;
    }


    public boolean isBinary()
    {
        return false;
    }

    public boolean isEmpty()
    {
        return this.content.trim().equals("");
    }

    public void setContent(String s)
    {
        this.content = s;
    }

    public void setEncoding(String s)
    {
        /* Not allowed */
    }

    public String toString()
    {
        return content;
    }
}
