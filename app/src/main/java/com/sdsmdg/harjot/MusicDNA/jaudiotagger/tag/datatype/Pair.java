package org.jaudiotagger.tag.datatype;

/**
 * A pair
 */
public class Pair
{
    private String key;
    private String value;

    public Pair(String key,String value)
    {
        setKey(key);
        setValue(value);
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }
}
