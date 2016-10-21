package org.jaudiotagger.tag.vorbiscomment;

import org.jaudiotagger.tag.reference.Tagger;
import org.jaudiotagger.tag.mp4.field.Mp4FieldType;

import java.util.List;
import java.util.EnumSet;

/**
 * Vorbis Comment Field Names
 *
 *
 *
 * This partial list is derived fom the following sources:
 * <ul>
 * <li>http://xiph.org/vorbis/doc/v-comment.html</li>
 * <li>http://wiki.musicbrainz.org/PicardQt/TagMapping</li>
 * <li>http://legroom.net/2009/05/09/ogg-vorbis-and-flac-comment-field-recommendations</li>
 * </ul>
 */
public enum VorbisCommentFieldKey
{
    ALBUM("ALBUM", EnumSet.of(Tagger.XIPH,Tagger.PICARD,Tagger.JAIKOZ)),
    ALBUMARTIST("ALBUMARTIST",EnumSet.of(Tagger.PICARD,Tagger.JAIKOZ)),
    ALBUM_ARTIST("ALBUM_ARTIST",EnumSet.of(Tagger.MEDIA_MONKEY)),
    ALBUMARTISTSORT("ALBUMARTISTSORT",EnumSet.of(Tagger.PICARD,Tagger.JAIKOZ)),
    ALBUMSORT("ALBUMSORT",EnumSet.of(Tagger.PICARD,Tagger.JAIKOZ)),
    ARTIST("ARTIST", EnumSet.of(Tagger.XIPH,Tagger.PICARD,Tagger.JAIKOZ)),
    ARTISTS("ARTISTS", EnumSet.of(Tagger.JAIKOZ)),
    ARTISTSORT("ARTISTSORT",EnumSet.of(Tagger.PICARD,Tagger.JAIKOZ)),
    ASIN("ASIN",EnumSet.of(Tagger.PICARD,Tagger.JAIKOZ)),
    BARCODE("BARCODE",EnumSet.of(Tagger.JAIKOZ)),
    BPM("BPM",EnumSet.of(Tagger.PICARD,Tagger.JAIKOZ)),
    CATALOGNUMBER("CATALOGNUMBER",EnumSet.of(Tagger.PICARD,Tagger.JAIKOZ)),
    COMMENT("COMMENT",EnumSet.of(Tagger.PICARD)),
    COMPILATION("COMPILATION",EnumSet.of(Tagger.PICARD,Tagger.JAIKOZ)),
    COMPOSER("COMPOSER",EnumSet.of(Tagger.PICARD,Tagger.JAIKOZ)),
    COMPOSERSORT("COMPOSERSORT",EnumSet.of(Tagger.JAIKOZ)),
    CONDUCTOR("CONDUCTOR",EnumSet.of(Tagger.PICARD,Tagger.JAIKOZ)),
    CONTACT("CONTACT",EnumSet.of(Tagger.XIPH)),
    COPYRIGHT("COPYRIGHT",EnumSet.of(Tagger.XIPH,Tagger.PICARD,Tagger.JAIKOZ)),
    COVERART("COVERART",EnumSet.of(Tagger.JAIKOZ)),
    COVERARTMIME("COVERARTMIME",EnumSet.of(Tagger.JAIKOZ)),
    CUSTOM1("CUSTOM1",EnumSet.of(Tagger.MEDIA_MONKEY)),
    CUSTOM2("CUSTOM2",EnumSet.of(Tagger.MEDIA_MONKEY)),
    CUSTOM3("CUSTOM3",EnumSet.of(Tagger.MEDIA_MONKEY)),
    CUSTOM4("CUSTOM4",EnumSet.of(Tagger.MEDIA_MONKEY)),
    CUSTOM5("CUSTOM5",EnumSet.of(Tagger.MEDIA_MONKEY)),
    DATE("DATE",EnumSet.of(Tagger.XIPH,Tagger.PICARD,Tagger.JAIKOZ)),
    DESCRIPTION("DESCRIPTION",EnumSet.of(Tagger.XIPH)),
    DISCNUMBER("DISCNUMBER",EnumSet.of(Tagger.PICARD,Tagger.JAIKOZ)),
    DISCSUBTITLE("DISCSUBTITLE",EnumSet.of(Tagger.PICARD,Tagger.JAIKOZ)),
    DISCTOTAL("DISCTOTAL",EnumSet.of(Tagger.XIPH,Tagger.PICARD)),
    ENCODEDBY("ENCODEDBY",EnumSet.of(Tagger.PICARD)),
    ENCODER("ENCODER"),
    ENSEMBLE("ENSEMBLE",EnumSet.of(Tagger.MEDIA_MONKEY)),   //Uses this for ALBUM_ARTIST
    FBPM("FBPM",EnumSet.of(Tagger.BEATUNES)),
    GENRE("GENRE",EnumSet.of(Tagger.XIPH,Tagger.PICARD,Tagger.JAIKOZ)),
    GROUPING("GROUPING",EnumSet.of(Tagger.PICARD,Tagger.JAIKOZ)),
    ISRC("ISRC",EnumSet.of(Tagger.XIPH,Tagger.PICARD,Tagger.JAIKOZ)),
    KEY("KEY"),
    LABEL("LABEL",EnumSet.of(Tagger.PICARD,Tagger.JAIKOZ)),
    LANGUAGE("LANGUAGE"),
    LICENSE("LICENSE",EnumSet.of(Tagger.XIPH)),
    LOCATION("LOCATION",EnumSet.of(Tagger.XIPH)),
    LYRICIST("LYRICIST",EnumSet.of(Tagger.PICARD,Tagger.JAIKOZ)),
    LYRICS("LYRICS",EnumSet.of(Tagger.PICARD,Tagger.JAIKOZ)),
    MEDIA("MEDIA",EnumSet.of(Tagger.PICARD,Tagger.JAIKOZ)),
    METADATA_BLOCK_PICTURE("METADATA_BLOCK_PICTURE",EnumSet.of(Tagger.XIPH)),
    MOOD("MOOD",EnumSet.of(Tagger.PICARD,Tagger.JAIKOZ)),
    MUSICBRAINZ_ALBUMARTISTID("MUSICBRAINZ_ALBUMARTISTID",EnumSet.of(Tagger.PICARD,Tagger.JAIKOZ)),
    MUSICBRAINZ_ALBUMID("MUSICBRAINZ_ALBUMID",EnumSet.of(Tagger.PICARD,Tagger.JAIKOZ)),
    MUSICBRAINZ_ALBUMSTATUS("MUSICBRAINZ_ALBUMSTATUS",EnumSet.of(Tagger.PICARD,Tagger.JAIKOZ)),
    MUSICBRAINZ_ALBUMTYPE("MUSICBRAINZ_ALBUMTYPE",EnumSet.of(Tagger.PICARD,Tagger.JAIKOZ)),
    MUSICBRAINZ_ARTISTID("MUSICBRAINZ_ARTISTID",EnumSet.of(Tagger.PICARD,Tagger.JAIKOZ)),
    MUSICBRAINZ_DISCID("MUSICBRAINZ_DISCID",EnumSet.of(Tagger.PICARD,Tagger.JAIKOZ)),
    MUSICBRAINZ_ORIGINAL_ALBUMID("MUSICBRAINZ_ORIGINALALBUMID",EnumSet.of(Tagger.JAIKOZ)),
    MUSICBRAINZ_RELEASEGROUPID("MUSICBRAINZ_RELEASEGROUPID",EnumSet.of(Tagger.PICARD,Tagger.JAIKOZ)),
    MUSICBRAINZ_TRACKID("MUSICBRAINZ_TRACKID",EnumSet.of(Tagger.PICARD,Tagger.JAIKOZ)),
    MUSICBRAINZ_RELEASETRACKID("MUSICBRAINZ_RELEASETRACKID",EnumSet.of(Tagger.JAIKOZ)),
    MUSICBRAINZ_WORKID("MUSICBRAINZ_WORKID",EnumSet.of(Tagger.PICARD,Tagger.JAIKOZ)),
    MUSICIP_PUID("MUSICIP_PUID",EnumSet.of(Tagger.PICARD,Tagger.JAIKOZ)),
    OCCASION("OCCASION",EnumSet.of(Tagger.MEDIA_MONKEY)),
    ORGANIZATION("ORGANIZATION",EnumSet.of(Tagger.XIPH)),  //   Name of the organization producing the track (i.e. the 'record label')
    ORIGINAL_ALBUM("ORIGINAL ALBUM",EnumSet.of(Tagger.JAIKOZ,Tagger.MEDIA_MONKEY)),
    ORIGINAL_ARTIST("ORIGINAL ARTIST",EnumSet.of(Tagger.JAIKOZ,Tagger.MEDIA_MONKEY)),
    ORIGINAL_LYRICIST("ORIGINAL LYRICIST",EnumSet.of(Tagger.MEDIA_MONKEY)),
    ORIGINAL_YEAR("ORIGINAL YEAR",EnumSet.of(Tagger.JAIKOZ,Tagger.MEDIA_MONKEY)),
    PERFORMER("PERFORMER",EnumSet.of(Tagger.XIPH,Tagger.PICARD)),
    PRODUCTNUMBER("PRODUCTNUMBER",EnumSet.of(Tagger.XIPH)),
    QUALITY("QUALITY",EnumSet.of(Tagger.MEDIA_MONKEY)),
    RATING("RATING",EnumSet.of(Tagger.MEDIA_MONKEY)),
    RELEASECOUNTRY("RELEASECOUNTRY",EnumSet.of(Tagger.PICARD,Tagger.JAIKOZ)),
    REMIXER("REMIXER",EnumSet.of(Tagger.PICARD,Tagger.JAIKOZ)),
    SCRIPT("SCRIPT",EnumSet.of(Tagger.JAIKOZ)),
    SOURCEMEDIA("SOURCEMEDIA",EnumSet.of(Tagger.XIPH)),
    SUBTITLE("SUBTITLE",EnumSet.of(Tagger.PICARD,Tagger.JAIKOZ)),
    TAGS("TAGS",EnumSet.of(Tagger.JAIKOZ)),
    TEMPO("TEMPO",EnumSet.of(Tagger.MEDIA_MONKEY)),
    TITLE("TITLE", EnumSet.of(Tagger.XIPH,Tagger.PICARD,Tagger.JAIKOZ)),
    TITLESORT("TITLESORT",EnumSet.of(Tagger.PICARD,Tagger.JAIKOZ)),
    TRACKNUMBER("TRACKNUMBER",EnumSet.of(Tagger.XIPH,Tagger.PICARD,Tagger.JAIKOZ)),
    TRACKTOTAL("TRACKTOTAL",EnumSet.of(Tagger.XIPH,Tagger.PICARD)),
    URL_DISCOGS_ARTIST_SITE("URL_DISCOGS_ARTIST_SITE",EnumSet.of(Tagger.JAIKOZ)),
    URL_DISCOGS_RELEASE_SITE("URL_DISCOGS_RELEASE_SITE",EnumSet.of(Tagger.JAIKOZ)),
    URL_LYRICS_SITE("URL_LYRICS_SITE",EnumSet.of(Tagger.JAIKOZ)),
    URL_OFFICIAL_ARTIST_SITE("URL_OFFICIAL_ARTIST_SITE",EnumSet.of(Tagger.JAIKOZ)),
    URL_OFFICIAL_RELEASE_SITE("URL_OFFICIAL_RELEASE_SITE",EnumSet.of(Tagger.JAIKOZ)),
    URL_WIKIPEDIA_ARTIST_SITE("URL_WIKIPEDIA_ARTIST_SITE",EnumSet.of(Tagger.JAIKOZ)),
    URL_WIKIPEDIA_RELEASE_SITE("URL_WIKIPEDIA_RELEASE_SITE",EnumSet.of(Tagger.JAIKOZ)),
    VENDOR("VENDOR"),
    VERSION("VERSION", EnumSet.of(Tagger.XIPH)),// The version field may be used to differentiate multiple versions of the same track title in a single collection. (e.g. remix info)

    ENGINEER("ENGINEER",EnumSet.of(Tagger.PICARD)),
    PRODUCER("PRODUCER",EnumSet.of(Tagger.PICARD)),
    DJMIXER("DJMIXER",EnumSet.of(Tagger.PICARD)),
    MIXER("MIXER",EnumSet.of(Tagger.PICARD)),
    ARRANGER("ARRANGER",EnumSet.of(Tagger.PICARD)),
    ACOUSTID_FINGERPRINT("ACOUSTID_FINGERPRINT",EnumSet.of(Tagger.PICARD)),
    ACOUSTID_ID("ACOUSTID_ID",EnumSet.of(Tagger.PICARD)),
    COUNTRY("COUNTRY",EnumSet.of(Tagger.PICARD)),
    ;


    private String fieldName;
    private EnumSet<Tagger> taggers;

    VorbisCommentFieldKey(String fieldName)
    {
        this.fieldName = fieldName;
    }

    VorbisCommentFieldKey(String fieldName, EnumSet<Tagger> taggers)
    {
        this.fieldName = fieldName;
        this.taggers = taggers;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    /**
     * List of taggers using this field, concentrates primarily on the original tagger to start using a field.
     * Tagger.XIPH means the field is either part  of the Vorbis Standard or a Vorbis proposed extension to the
     * standard
     *
     * @return
     */
    public EnumSet<Tagger> getTaggers()
    {
        return taggers;
    }
}
