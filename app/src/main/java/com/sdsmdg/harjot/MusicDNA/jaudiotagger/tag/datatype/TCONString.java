package org.jaudiotagger.tag.datatype;


import org.jaudiotagger.tag.InvalidDataTypeException;
import org.jaudiotagger.tag.TagOptionSingleton;
import org.jaudiotagger.tag.id3.AbstractTagFrameBody;
import org.jaudiotagger.tag.id3.valuepair.TextEncoding;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Overrides in order to properly support the ID3v23 implemenation of TCON
 */
public class TCONString extends TextEncodedStringSizeTerminated
{
    private boolean isNullSeperateMultipleValues = true;


    /**
     * Creates a new empty TextEncodedStringSizeTerminated datatype.
     *
     * @param identifier identifies the frame type
     * @param frameBody
     */
    public TCONString(String identifier, AbstractTagFrameBody frameBody)
    {
        super(identifier, frameBody);
    }

    /**
     * Copy constructor
     *
     * @param object
     */
    public TCONString(TCONString object)
    {
        super(object);
    }

    public boolean equals(Object obj)
    {
        if(this==obj)
        {
            return true;
        }
        return obj instanceof TCONString && super.equals(obj);
    }

    /**
     * if this field is used with ID3v24 then it is usual to null separate values. Within ID3v23 not many
     * frames officially support mutiple values, so in absense of better solution we use the v24 method, however
     * some frames such as TCON have there own method and should not null separate values. This can be controlled
     * by this field.
     */
    public boolean isNullSeperateMultipleValues()
    {
        return isNullSeperateMultipleValues;
    }

    public void setNullSeperateMultipleValues(boolean nullSeperateMultipleValues)
    {
        isNullSeperateMultipleValues = nullSeperateMultipleValues;
    }

    /**
     * Write String using specified encoding
     *
     * When this is called multiple times, all but the last value has a trailing null
     *
     * @param encoder
     * @param next
     * @param i
     * @param noOfValues
     * @return
     * @throws java.nio.charset.CharacterCodingException
     */
    private ByteBuffer writeString( CharsetEncoder encoder, String next, int i, int noOfValues)
            throws CharacterCodingException
    {

        ByteBuffer bb;
        if(( i + 1) == noOfValues )
        {
            bb = encoder.encode(CharBuffer.wrap(next));
        }
        else
        {

            if(isNullSeperateMultipleValues())
            {
                bb = encoder.encode(CharBuffer.wrap(next + '\0'));
            }
            else
            {
                bb = encoder.encode(CharBuffer.wrap(next));
            }
        }
        bb.rewind();
        return bb;
    }

    /**
     * Write String in UTF-LEBOM format
     *
     * When this is called multiple times, all but the last value has a trailing null
     *
     * Remember we are using this charset because the charset that writes BOM does it the wrong way for us
     * so we use this none and then manually add the BOM ourselves.
     *
     * @param next
     * @param i
     * @param noOfValues
     * @return
     * @throws CharacterCodingException
     */
    private ByteBuffer writeStringUTF16LEBOM( String next, int i, int noOfValues)
            throws CharacterCodingException
    {
        CharsetEncoder encoder = Charset.forName(TextEncoding.CHARSET_UTF_16_LE_ENCODING_FORMAT).newEncoder();
        encoder.onMalformedInput(CodingErrorAction.IGNORE);
        encoder.onUnmappableCharacter(CodingErrorAction.IGNORE);

        ByteBuffer bb = null;
        //Note remember LE BOM is ff fe but this is handled by encoder Unicode char is fe ff
        if(( i + 1)==noOfValues)
        {
            bb = encoder.encode(CharBuffer.wrap('\ufeff' + next ));
        }
        else
        {
            if(isNullSeperateMultipleValues())
            {
                bb = encoder.encode(CharBuffer.wrap('\ufeff' + next + '\0'));
            }
            else
            {
                bb = encoder.encode(CharBuffer.wrap('\ufeff' + next));
            }
        }
        bb.rewind();
        return bb;
    }

    /**
     * Write String in UTF-BEBOM format
     *
     * When this is called multiple times, all but the last value has a trailing null
     *
     * @param next
     * @param i
     * @param noOfValues
     * @return
     * @throws CharacterCodingException
     */
    private ByteBuffer writeStringUTF16BEBOM( String next, int i, int noOfValues)
            throws CharacterCodingException
    {
        CharsetEncoder encoder = Charset.forName(TextEncoding.CHARSET_UTF_16_BE_ENCODING_FORMAT).newEncoder();
        encoder.onMalformedInput(CodingErrorAction.IGNORE);
        encoder.onUnmappableCharacter(CodingErrorAction.IGNORE);

        ByteBuffer bb = null;
        //Add BOM
        if(( i + 1)==noOfValues)
        {
            bb = encoder.encode(CharBuffer.wrap('\ufeff' + next ));
        }
        else
        {
            if(isNullSeperateMultipleValues())
            {
                bb = encoder.encode(CharBuffer.wrap('\ufeff' + next + '\0'));
            }
            else
            {
                bb = encoder.encode(CharBuffer.wrap('\ufeff' + next));
            }
        }
        bb.rewind();
        return bb;
    }

    /**
     * Add an additional String to the current String value
     *
     * @param value
     */
    public void addValue(String value)
    {
        if(isNullSeperateMultipleValues())
        {
            setValue(this.value + "\u0000" + value);
        }
        else
        {
            setValue(this.value + value);
        }
    }

    /**
     * How many values are held, each value is separated by a null terminator
     *
     * @return number of values held, usually this will be one.
     */
    public int getNumberOfValues()
    {
        return getValues().size();
    }

    /**
     * Get the nth value
     *
     * @param index
     * @return the nth value
     * @throws IndexOutOfBoundsException if value does not exist
     */
    public String getValueAtIndex(int index)
    {
        //Split String into separate components
        List values = getValues();
        return (String) values.get(index);
    }

    public static List<String> splitV23(String value)
    {
        String[] valuesarray = value.replaceAll("(\\(\\d+\\)|\\(RX\\)|\\(CR\\)\\w*)", "$1\u0000").split("\u0000");
        List<String> values = Arrays.asList(valuesarray);
        //Read only list so if empty have to create new list
        if (values.size() == 0)
        {
            values = new ArrayList<String>(1);
            values.add("");
        }
        return values;
    }

    /**
     *
     * @return list of all values
     */
    public List<String> getValues()
    {
        if(isNullSeperateMultipleValues())
        {
            return splitByNullSeperator((String) value);
        }
        else
        {
            return splitV23((String)value);
        }
    }

    /**
     * Get value(s) whilst removing any trailing nulls
     *
     * @return
     */
    public String getValueWithoutTrailingNull()
    {
        List<String> values = getValues();
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<values.size();i++)
        {
            if(i!=0)
            {
                sb.append("\u0000");
            }
            sb.append(values.get(i));
        }
        return sb.toString();
    }
}
