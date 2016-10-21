package org.jaudiotagger.tag.mp4;

import org.jaudiotagger.tag.mp4.field.Mp4FieldType;
import org.jaudiotagger.tag.mp4.field.Mp4TagReverseDnsField;
import org.jaudiotagger.tag.reference.Tagger;

import static org.jaudiotagger.tag.mp4.field.Mp4FieldType.*;

/**
 * Starting list of known mp4 metadata fields that follow the Parent,Data or ---,issuer,name,data
 * convention. Atoms that contain metadata in other formats are not listed here because they need to be processed
 * specially.
 *
 *
 * <p>Simple metaitems use the parent atom id as their identifier whereas reverse dns (----) atoms use
 * the reversedns,issuer and name fields as their identifier. When the atom is non-0standard but follws the rules
 * we list it here with an additional Tagger field to indicate where the field was originally designed.
 *
 * From:
 * http://www.hydrogenaudio.org/forums/index.php?showtopic=29120&st=0&p=251686&#entry251686
 * http://wiki.musicbrainz.org/PicardQt/TagMapping
 * http://atomicparsley.sourceforge.net/mpeg-4files.html
 *
 *
 */
public enum Mp4FieldKey
{
    ACOUSTID_FINGERPRINT("com.apple.iTunes", "Acoustid Fingerprint", TEXT, Tagger.PICARD),
    ACOUSTID_FINGERPRINT_OLD("com.apple.iTunes", "AcoustId Fingerprint", TEXT, Tagger.PICARD),
    ACOUSTID_ID("com.apple.iTunes", "Acoustid Id", TEXT, Tagger.PICARD),
    ALBUM("©alb",Mp4TagFieldSubType.TEXT, TEXT),
    ALBUM_ARTIST("aART",Mp4TagFieldSubType.TEXT, TEXT),
    ALBUM_ARTIST_SORT("soaa",Mp4TagFieldSubType.TEXT, TEXT),
    ALBUM_SORT("soal",Mp4TagFieldSubType.TEXT, TEXT),
    ARTIST_SORT("soar",Mp4TagFieldSubType.TEXT, TEXT),
    ARTIST("©ART",Mp4TagFieldSubType.TEXT, TEXT),
    ARTWORK("covr",Mp4TagFieldSubType.ARTWORK, COVERART_JPEG),
    ASIN("com.apple.iTunes", "ASIN", TEXT, Tagger.PICARD),
    BPM("tmpo",Mp4TagFieldSubType.BYTE, INTEGER, 2),
    CATEGORY("catg",Mp4TagFieldSubType.TEXT, TEXT),
    CDDB_1("com.apple.iTunes", "iTunes_CDDB_1", TEXT),
    CDDB_IDS("com.apple.iTunes", "iTunes_CDDB_IDs", TEXT),
    CDDB_TRACKNUMBER("com.apple.iTunes", "iTunes_CDDB_TrackNumber", TEXT),
    COMMENT("©cmt",Mp4TagFieldSubType.TEXT, TEXT),
    COMPILATION("cpil",Mp4TagFieldSubType.BYTE, INTEGER, 1),
    COMPOSER("©wrt",Mp4TagFieldSubType.TEXT, TEXT),
    COMPOSER_SORT("soco",Mp4TagFieldSubType.TEXT, TEXT),
    COPYRIGHT("cprt",Mp4TagFieldSubType.TEXT, TEXT),
    COUNTRY("com.apple.iTunes", "Country", TEXT, Tagger.PICARD),
    DAY("©day",Mp4TagFieldSubType.TEXT, TEXT),
    DESCRIPTION("desc",Mp4TagFieldSubType.TEXT, TEXT),
    DISCNUMBER("disk",Mp4TagFieldSubType.DISC_NO, IMPLICIT),
    DISC_SUBTITLE("com.apple.iTunes", "DISCSUBTITLE", TEXT, Tagger.PICARD),
    ENCODER("©too",Mp4TagFieldSubType.TEXT, TEXT),
    FBPM("com.apple.iTunes", "fBPM", TEXT, Tagger.JAIKOZ),
    GENRE("gnre",Mp4TagFieldSubType.GENRE, IMPLICIT),
    GENRE_CUSTOM("©gen",Mp4TagFieldSubType.TEXT, TEXT),
    GROUPING("©grp",Mp4TagFieldSubType.TEXT, TEXT),
    ITUNES_NORM("com.apple.iTunes", "iTunNORM", TEXT),
    ITUNES_SMPB("com.apple.iTunes", "iTunSMPB", TEXT),
    KEY_OLD("com.apple.iTunes", "KEY", TEXT, Tagger.JAIKOZ),
    KEY("com.apple.iTunes", "initialkey", TEXT),
    KEYWORD("keyw",Mp4TagFieldSubType.TEXT, TEXT),
    LANGUAGE("com.apple.iTunes", "LANGUAGE", TEXT, Tagger.JAIKOZ),
    LYRICS("©lyr",Mp4TagFieldSubType.TEXT, TEXT),
    MUSICBRAINZ_ALBUMARTISTID("com.apple.iTunes", "MusicBrainz Album Artist Id", TEXT, Tagger.PICARD),
    MUSICBRAINZ_ALBUMID("com.apple.iTunes", "MusicBrainz Album Id", TEXT, Tagger.PICARD),
    MUSICBRAINZ_ALBUM_STATUS("com.apple.iTunes", "MusicBrainz Album Status", TEXT, Tagger.PICARD),
    MUSICBRAINZ_ALBUM_TYPE("com.apple.iTunes", "MusicBrainz Album Type", TEXT, Tagger.PICARD),
    MUSICBRAINZ_ARTISTID("com.apple.iTunes", "MusicBrainz Artist Id", TEXT, Tagger.PICARD),
    MUSICBRAINZ_DISCID("com.apple.iTunes", "MusicBrainz Disc Id", TEXT, Tagger.PICARD),
    MUSICBRAINZ_ORIGINALALBUMID("com.apple.iTunes", "MusicBrainz Original Album Id", TEXT, Tagger.JAIKOZ),
    MUSICBRAINZ_RELEASE_GROUPID("com.apple.iTunes", "MusicBrainz Release Group Id", TEXT, Tagger.PICARD),
    MUSICBRAINZ_RELEASE_TRACKID("com.apple.iTunes", "MusicBrainz Release Track Id", TEXT, Tagger.PICARD),
    MUSICBRAINZ_TRACKID("com.apple.iTunes", "MusicBrainz Track Id", TEXT, Tagger.PICARD),
    MUSICBRAINZ_WORKID("com.apple.iTunes", "MusicBrainz Work Id", TEXT, Tagger.PICARD),
    MUSICIP_PUID("com.apple.iTunes", "MusicIP PUID", TEXT, Tagger.PICARD),
    PART_OF_GAPLESS_ALBUM("pgap",Mp4TagFieldSubType.BYTE, INTEGER),
    PURCHASE_DATE("purd",Mp4TagFieldSubType.TEXT, TEXT),
    RATING("rtng",Mp4TagFieldSubType.BYTE, INTEGER,1),   //AFAIK Cant be setField in itunes, but if setField to explicit itunes will show as explicit
    RELEASECOUNTRY("com.apple.iTunes", "MusicBrainz Album Release Country", TEXT, Tagger.PICARD),
    SHOW("tvsh",Mp4TagFieldSubType.TEXT, TEXT),      //tv show but also used just as show
    SHOW_SORT("sosn",Mp4TagFieldSubType.TEXT, TEXT),
    SUBTITLE("com.apple.iTunes", "SUBTITLE", TEXT, Tagger.PICARD),
    TITLE("©nam",Mp4TagFieldSubType.TEXT, TEXT),
    TITLE_SORT("sonm",Mp4TagFieldSubType.TEXT, TEXT),
    TRACK("trkn",Mp4TagFieldSubType.TRACK_NO, IMPLICIT),

    //AFAIK These arent actually used by Audio Only files, but there is nothing to prevent them being used
    CONTENT_TYPE("stik",Mp4TagFieldSubType.BYTE, INTEGER, 1),
    TOOL("tool",Mp4TagFieldSubType.BYTE, INTEGER, 4),
    PODCAST_KEYWORD("keyw",Mp4TagFieldSubType.TEXT, TEXT),
    PODCAST_URL("purl",Mp4TagFieldSubType.NUMBER, IMPLICIT),   //TODO Actually seems to store text but is marked as numeric!
    EPISODE_GLOBAL_ID("egid",Mp4TagFieldSubType.NUMBER, IMPLICIT),   //TODO Actually seems to store text but is marked as numeric!
    TV_NETWORK("tvnn",Mp4TagFieldSubType.TEXT, TEXT),
    TV_EPISODE_NUMBER("tven",Mp4TagFieldSubType.TEXT, TEXT),
    TV_SEASON("tvsn",Mp4TagFieldSubType.BYTE, INTEGER, 1),
    TV_EPISODE("tves",Mp4TagFieldSubType.BYTE, INTEGER, 1),

    //These seem to be used in DRM Files, of type byte , we need to know the byte length to allow them to be written
    //back correctly on saving them, we don't provides options to modify them as may break drm
    AP_ID("apID",Mp4TagFieldSubType.UNKNOWN, TEXT),
    AT_ID("atID",Mp4TagFieldSubType.UNKNOWN, INTEGER, 4),
    CN_ID("cnID",Mp4TagFieldSubType.UNKNOWN, INTEGER, 4),
    PL_ID("plID",Mp4TagFieldSubType.UNKNOWN, INTEGER, 8),
    GE_ID("geID",Mp4TagFieldSubType.UNKNOWN, INTEGER, 4),
    SF_ID("sfID",Mp4TagFieldSubType.UNKNOWN, INTEGER, 4),
    AK_ID("akID",Mp4TagFieldSubType.UNKNOWN, INTEGER, 1),

    //Media Monkey3 beta
    LYRICIST_MM3BETA("lyrc",Mp4TagFieldSubType.TEXT, TEXT, Tagger.MEDIA_MONKEY),
    CONDUCTOR_MM3BETA("cond",Mp4TagFieldSubType.TEXT, TEXT, Tagger.MEDIA_MONKEY),
    ISRC_MMBETA("isrc",Mp4TagFieldSubType.TEXT, TEXT, Tagger.MEDIA_MONKEY),
    MOOD_MM3BETA("mood",Mp4TagFieldSubType.TEXT, TEXT, Tagger.MEDIA_MONKEY),
    SCORE("rate",Mp4TagFieldSubType.TEXT, TEXT, Tagger.MEDIA_MONKEY),    //As in mark out of 100
    ORIGINAL_ARTIST("oart",Mp4TagFieldSubType.TEXT, TEXT, Tagger.MEDIA_MONKEY),
    ORIGINAL_ALBUM_TITLE("otit",Mp4TagFieldSubType.TEXT, TEXT, Tagger.MEDIA_MONKEY),
    ORIGINAL_LYRICIST("olyr",Mp4TagFieldSubType.TEXT, TEXT, Tagger.MEDIA_MONKEY),
    INVOLVED_PEOPLE("peop",Mp4TagFieldSubType.TEXT, TEXT, Tagger.MEDIA_MONKEY),
    TEMPO("empo",Mp4TagFieldSubType.TEXT, TEXT, Tagger.MEDIA_MONKEY),
    OCCASION("occa",Mp4TagFieldSubType.TEXT, TEXT, Tagger.MEDIA_MONKEY),
    QUALITY("qual",Mp4TagFieldSubType.TEXT, TEXT, Tagger.MEDIA_MONKEY),
    CUSTOM_1("cus1",Mp4TagFieldSubType.TEXT, TEXT, Tagger.MEDIA_MONKEY),
    CUSTOM_2("cus2",Mp4TagFieldSubType.TEXT, TEXT, Tagger.MEDIA_MONKEY),
    CUSTOM_3("cus3",Mp4TagFieldSubType.TEXT, TEXT, Tagger.MEDIA_MONKEY),
    CUSTOM_4("cus4",Mp4TagFieldSubType.TEXT, TEXT, Tagger.MEDIA_MONKEY),
    CUSTOM_5("cus5",Mp4TagFieldSubType.TEXT, TEXT, Tagger.MEDIA_MONKEY),

    //Media Monkey 3.0.6 Onwards
    MM_PUBLISHER("com.apple.iTunes", "ORGANIZATION", TEXT, Tagger.MEDIA_MONKEY),
    MM_ORIGINAL_ARTIST("com.apple.iTunes", "ORIGINAL ARTIST", TEXT, Tagger.MEDIA_MONKEY),
    MM_ORIGINAL_ALBUM_TITLE("com.apple.iTunes", "ORIGINAL ALBUM", TEXT, Tagger.MEDIA_MONKEY),
    MM_ORIGINAL_LYRICIST("com.apple.iTunes", "ORIGINAL LYRICIST", TEXT, Tagger.MEDIA_MONKEY),
    MM_INVOLVED_PEOPLE("com.apple.iTunes", "INVOLVED PEOPLE", TEXT, Tagger.MEDIA_MONKEY),
    MM_ORIGINAL_YEAR("com.apple.iTunes", "ORIGINAL YEAR", TEXT, Tagger.MEDIA_MONKEY),
    MM_TEMPO("com.apple.iTunes", "TEMPO", TEXT, Tagger.MEDIA_MONKEY),
    MM_OCCASION("com.apple.iTunes", "OCCASION", TEXT, Tagger.MEDIA_MONKEY),
    MM_QUALITY("com.apple.iTunes", "QUALITY", TEXT, Tagger.MEDIA_MONKEY),
    MM_CUSTOM_1("com.apple.iTunes", "CUSTOM1", TEXT, Tagger.MEDIA_MONKEY),
    MM_CUSTOM_2("com.apple.iTunes", "CUSTOM2", TEXT, Tagger.MEDIA_MONKEY),
    MM_CUSTOM_3("com.apple.iTunes", "CUSTOM3", TEXT, Tagger.MEDIA_MONKEY),
    MM_CUSTOM_4("com.apple.iTunes", "CUSTOM4", TEXT, Tagger.MEDIA_MONKEY),
    MM_CUSTOM_5("com.apple.iTunes", "CUSTOM5", TEXT, Tagger.MEDIA_MONKEY),

    //Picard Qt
    LYRICIST("com.apple.iTunes", "LYRICIST", TEXT, Tagger.PICARD),
    CONDUCTOR("com.apple.iTunes", "CONDUCTOR", TEXT, Tagger.PICARD),
    REMIXER("com.apple.iTunes", "REMIXER", TEXT, Tagger.PICARD),
    ENGINEER("com.apple.iTunes", "ENGINEER", TEXT, Tagger.PICARD),
    PRODUCER("com.apple.iTunes", "PRODUCER", TEXT, Tagger.PICARD),
    DJMIXER("com.apple.iTunes", "DJMIXER", TEXT, Tagger.PICARD),
    MIXER("com.apple.iTunes", "MIXER", TEXT, Tagger.PICARD),
    ARRANGER("com.apple.iTunes", "ARRANGER", TEXT, Tagger.PICARD),
    MOOD("com.apple.iTunes", "MOOD", TEXT, Tagger.PICARD),
    ISRC("com.apple.iTunes", "ISRC", TEXT, Tagger.PICARD),
    MEDIA("com.apple.iTunes", "MEDIA", TEXT, Tagger.PICARD),
    LABEL("com.apple.iTunes", "LABEL", TEXT, Tagger.PICARD),
    CATALOGNO("com.apple.iTunes", "CATALOGNUMBER", TEXT, Tagger.PICARD),
    BARCODE("com.apple.iTunes", "BARCODE", TEXT, Tagger.PICARD),

    //Jaikoz
    URL_LYRICS_SITE("com.apple.iTunes", "URL_LYRICS_SITE", TEXT, Tagger.JAIKOZ),
    URL_OFFICIAL_RELEASE_SITE("com.apple.iTunes", "URL_OFFICIAL_RELEASE_SITE", TEXT, Tagger.JAIKOZ),
    URL_DISCOGS_RELEASE_SITE("com.apple.iTunes", "URL_DISCOGS_RELEASE_SITE", TEXT, Tagger.JAIKOZ),
    URL_WIKIPEDIA_RELEASE_SITE("com.apple.iTunes", "URL_WIKIPEDIA_RELEASE_SITE", TEXT, Tagger.JAIKOZ),
    URL_OFFICIAL_ARTIST_SITE("com.apple.iTunes", "URL_OFFICIAL_ARTIST_SITE", TEXT, Tagger.JAIKOZ),
    URL_DISCOGS_ARTIST_SITE("com.apple.iTunes", "URL_DISCOGS_ARTIST_SITE", TEXT, Tagger.JAIKOZ),
    URL_WIKIPEDIA_ARTIST_SITE("com.apple.iTunes", "URL_WIKIPEDIA_ARTIST_SITE", TEXT, Tagger.JAIKOZ),
    SCRIPT("com.apple.iTunes", "SCRIPT", TEXT, Tagger.JAIKOZ),
    TAGS("com.apple.iTunes", "TAGS", TEXT, Tagger.JAIKOZ),
    ARTISTS("com.apple.iTunes", "ARTISTS", TEXT, Tagger.JAIKOZ),

    //Winamp
    WINAMP_PUBLISHER("com.nullsoft.winamp", "publisher", TEXT, Tagger.WINAMP),

    //Unknown
    KEYS("keys",Mp4TagFieldSubType.TEXT,TEXT),
    
    ;
    private Tagger tagger;
    private String fieldName;
    private Mp4TagFieldSubType subclassType;
    private String issuer;
    private String identifier;
    private Mp4FieldType fieldType;
    private int fieldLength;

    /**
     * For usual metadata fields that use a data field
     *
     * @param fieldName
     * @param fieldType of data atom
     */
    Mp4FieldKey(String fieldName, Mp4TagFieldSubType subclassType, Mp4FieldType fieldType)
    {
        this.fieldName      = fieldName;
        this.subclassType   = subclassType;
        this.fieldType      = fieldType;
    }

    /**
     * For usual metadata fields that use a data field, but not recognised as standard field
     *
     * @param fieldName
     * @param fieldType of data atom
     * @param tagger
     */
    Mp4FieldKey(String fieldName, Mp4TagFieldSubType subclassType,Mp4FieldType fieldType, Tagger tagger)
    {
        this.fieldName = fieldName;
        this.subclassType   = subclassType;
        this.fieldType = fieldType;
        this.tagger = tagger;
    }

    /**
     * For usual metadata fields that use a data field where the field length is fixed
     * such as Byte fields
     *
     * @param fieldName
     * @param fieldType
     * @param fieldLength
     */
    Mp4FieldKey(String fieldName, Mp4TagFieldSubType subclassType,Mp4FieldType fieldType, int fieldLength)
    {
        this.fieldName = fieldName;
        this.subclassType   = subclassType;
        this.fieldType = fieldType;
        this.fieldLength = fieldLength;
    }

    /**
     * For reverse dns fields that use an internal fieldname of '----' and have  additional issuer
     * and identifier fields, we use all three seperated by a ':' ) to give us a unique key
     *
     * @param issuer
     * @param identifier
     * @param fieldType  of data atom
     */
    Mp4FieldKey(String issuer, String identifier, Mp4FieldType fieldType)
    {

        this.issuer = issuer;
        this.identifier = identifier;
        this.fieldName = Mp4TagReverseDnsField.IDENTIFIER + ":" + issuer + ":" + identifier;
        this.subclassType = Mp4TagFieldSubType.REVERSE_DNS;
        this.fieldType = fieldType;
    }

    /**
     * For reverse dns fields that use an internal fieldname of '----' and have  additional issuer
     * and identifier fields, we use all three seperated by a ':' ) to give us a unique key
     * For non-standard fields
     *
     * @param issuer
     * @param identifier
     * @param fieldType  of data atom
     * @param tagger
     */
    Mp4FieldKey(String issuer, String identifier, Mp4FieldType fieldType, Tagger tagger)
    {

        this.issuer = issuer;
        this.identifier = identifier;
        this.fieldName = Mp4TagReverseDnsField.IDENTIFIER + ":" + issuer + ":" + identifier;
        this.subclassType = Mp4TagFieldSubType.REVERSE_DNS;
        this.fieldType = fieldType;
        this.tagger = tagger;
    }

    /**
     * This is the value of the fieldname that is actually used to write mp4
     *
     * @return
     */
    public String getFieldName()
    {
        return fieldName;
    }

    /**
     * @return fieldtype
     */
    public Mp4FieldType getFieldType()
    {
        return fieldType;
    }

     /**
     * @return subclassType
     */
    public Mp4TagFieldSubType getSubClassFieldType()
    {
        return subclassType;
    }

    /**
     * @return true if this is a reverse dns key
     */
    public boolean isReverseDnsType()
    {
        return identifier.startsWith(Mp4TagReverseDnsField.IDENTIFIER);
    }

    /**
     * @return issuer (Reverse Dns Fields Only)
     */
    public String getIssuer()
    {
        return issuer;
    }

    /**
     * @return identifier (Reverse Dns Fields Only)
     */
    public String getIdentifier()
    {
        return identifier;
    }

    /**
     * @return field length (currently only used by byte fields)
     */
    public int getFieldLength()
    {
        return fieldLength;
    }

    public Tagger getTagger()
    {
        if (tagger != null)
        {
            return tagger;
        }
        return Tagger.ITUNES;
    }
}
