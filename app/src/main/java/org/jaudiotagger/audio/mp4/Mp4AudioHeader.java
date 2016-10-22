package org.jaudiotagger.audio.mp4;

import org.jaudiotagger.audio.generic.GenericAudioHeader;
import org.jaudiotagger.audio.mp4.atom.Mp4EsdsBox;

/**
 * Store some additional attributes not available for all audio types
 */
public class Mp4AudioHeader extends GenericAudioHeader
{
    /**
     * The key for the kind field<br>
     *
     * @see #content
     */
    public final static String FIELD_KIND = "KIND";

    /**
     * The key for the profile<br>
     *
     * @see #content
     */
    public final static String FIELD_PROFILE = "PROFILE";


    /**
     * The key for the ftyp brand<br>
     *
     * @see #content
     */
    public final static String FIELD_BRAND = "BRAND";

    public void setKind(Mp4EsdsBox.Kind kind)
    {
        content.put(FIELD_KIND, kind);
    }

    /**
     * @return kind
     */
    public Mp4EsdsBox.Kind getKind()
    {
        return (Mp4EsdsBox.Kind) content.get(FIELD_KIND);
    }

    /**
     * The key for the profile
     *
     * @param profile
     */
    public void setProfile(Mp4EsdsBox.AudioProfile profile)
    {
        content.put(FIELD_PROFILE, profile);
    }

    /**
     * @return audio profile
     */
    public Mp4EsdsBox.AudioProfile getProfile()
    {
        return (Mp4EsdsBox.AudioProfile) content.get(FIELD_PROFILE);
    }

    /**
     * @param brand
     */
    public void setBrand(String brand)
    {
        content.put(FIELD_BRAND, brand);
    }


    /**
     * @return brand
     */
    public String getBrand()
    {
        return (String) content.get(FIELD_BRAND);
    }


}
