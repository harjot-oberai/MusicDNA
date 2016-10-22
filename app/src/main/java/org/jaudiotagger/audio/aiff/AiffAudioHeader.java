package org.jaudiotagger.audio.aiff;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.generic.GenericAudioHeader;

/**
 *   Non-"tag" metadata from the AIFF file. In general, read-only.
 */
public class AiffAudioHeader extends GenericAudioHeader {

    public enum FileType {
        AIFFTYPE,
        AIFCTYPE
    }
    
    public enum Endian {
        BIG_ENDIAN,
        LITTLE_ENDIAN
    }
    
    private FileType fileType;
    private Date timestamp;
    private Endian endian;
    private String audioEncoding;
    private String name;
    private String author;
    private String copyright;
    
    private List<String> applicationIdentifiers;
    private List<String> comments;
    
    public AiffAudioHeader() {
        applicationIdentifiers = new ArrayList<String> ();
        comments = new ArrayList<String> ();
        endian = Endian.BIG_ENDIAN;
    }

    /** 
     *  Return the timestamp of the file.
     */
    public Date getTimestamp () {
        return timestamp;
    }
    
    /**
     *  Set the timestamp.
     */
    public void setTimestamp (Date d) {
        timestamp = d;
    }
    
    /** 
     * Return the file type (AIFF or AIFC)
     */
    public FileType getFileType () {
        return fileType;
    }
    
    /** 
     * Set the file type (AIFF or AIFC)
     */
    public void setFileType (FileType typ) {
        fileType = typ;
    }
    
    /**
     *  Return the author 
     */
    public String getAuthor () {
        return author;
    }
    
    /** Set the author */
    public void setAuthor (String a) {
        author = a;
    }
    
    /**
     *  Return the name. May be null.
     */
    public String getName () {
        return name;
    }
    
    /** Set the name */
    public void setName (String n) {
        name = n;
    }
    
    /**
     *  Return the copyright. May be null. 
     */
    public String getCopyright () {
        return copyright;
    }
    
    /**
     *  Set the copyright 
     */
    public void setCopyright (String c) {
        copyright = c;
    }

    
    
    /** 
     * Return endian status (big or little)
     */
    public Endian getEndian () {
        return endian;
    }
    
    /**
     *  Set endian status (big or little) 
     *  
     */
    public void setEndian (Endian e) {
        endian = e;
    }
    
    /** Return list of all application identifiers */
    public List<String> getApplicationIdentifiers () {
        return applicationIdentifiers;
    }
    
    /** 
     *  Add an application identifier. There can be any number of these.
     */
    public void addApplicationIdentifier (String id) {
        applicationIdentifiers.add (id);
    }

    /** Return list of all annotations */
    public List<String> getAnnotations () {
        return applicationIdentifiers;
    }
    
    /** 
     *  Add an annotation. There can be any number of these.
     */
    public void addAnnotation (String a) {
        applicationIdentifiers.add (a);
    }

    /** Return list of all comments */
    public List<String> getComments () {
        return comments;
    }
    
    /** 
     *  Add a comment. There can be any number of these.
     */
    public void addComment (String c) {
        comments.add (c);
    }

    /** Return the audio encoding as a descriptive string */
    public String getAudioEncoding () {
        return audioEncoding;
    }
    
    /** Set the audio encoding as a descriptive string */
    public void setAudioEncoding (String s) {
        audioEncoding = s;
    }
}
