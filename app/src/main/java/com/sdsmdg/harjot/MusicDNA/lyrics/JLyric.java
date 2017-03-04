package com.sdsmdg.harjot.MusicDNA.lyrics;

import com.sdsmdg.harjot.MusicDNA.annotations.Reflection;
import com.sdsmdg.harjot.MusicDNA.utilities.Net;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

@Reflection
public class JLyric {

    public static final String domain = "j-lyric.net";
    private static final String baseUrl = "http://search.j-lyric.net/index.php?ct=0&ca=0&kl=&cl=0&ka=%1s&kt=%1s";

    public static ArrayList<Lyrics> search(String query) {
        ArrayList<Lyrics> results = new ArrayList<>();
        Elements artistBlocks = null;

        try {
            Document searchPage = Jsoup.connect(String.format("http://search.j-lyric.net/index.php?ct=0&ka=&ca=0&kl=&cl=0&kt=%s", URLEncoder.encode(query, "UTF-8")))
                    .userAgent(Net.USER_AGENT).get();
            artistBlocks = searchPage.body().select("div#lyricList .body");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (artistBlocks == null || artistBlocks.first() == null)
            return results;


        for(Element result: artistBlocks) {
            Lyrics l = new Lyrics(Lyrics.SEARCH_ITEM);
            String title = result.select("div.title > a").text();
            String artist = result.select("div.status > a").text();
            String url = result.select("div.title > a").attr("href");
            l.setTitle(title);
            l.setArtist(artist);
            l.setURL(url);
            l.setSource("J-Lyric");
            results.add(l);
        }
        return results;
    }

    @Reflection
    public static Lyrics fromMetaData(String artist, String song) {
        if ((artist == null) || (song == null))
            return new Lyrics(Lyrics.ERROR);

        String encodedArtist;
        String encodedSong;
        String url;

        try {
            encodedArtist = URLEncoder.encode(artist, "UTF-8");
            encodedSong = URLEncoder.encode(song, "UTF-8");
            Document searchPage = Jsoup.connect(String.format(baseUrl, encodedArtist, encodedSong))
                    .userAgent(Net.USER_AGENT).get();
            if (!searchPage.location().startsWith("http://search.j-lyric.net/"))
                throw new IOException("Redirected to wrong domain " + searchPage.location());
            Elements artistBlocks = searchPage.body().select("div#lyricList");

            //@todo give all results
            if (artistBlocks.first() == null) {
                Lyrics lyrics = new Lyrics(Lyrics.NO_RESULT);
                lyrics.setArtist(artist);
                lyrics.setTitle(song);
                return lyrics;
            }
            url = artistBlocks.first().select("div.title a").attr("href");

        } catch (IOException e) {
            e.printStackTrace();
            return new Lyrics(Lyrics.ERROR);
        }

        return fromURL(url, artist, song);
    }

    public static Lyrics fromURL(String url, String artist, String song) {
        Lyrics lyrics;
        String text = null;

        try {
            Document lyricsPage = Jsoup.connect(url).userAgent(Net.USER_AGENT).get();
            if (!lyricsPage.location().contains(domain))
                throw new IOException("Redirected to wrong domain " + lyricsPage.location());
            text = lyricsPage.select("p#lyricBody").html();
            if (artist == null)
                artist = lyricsPage.select("div.body")
                        .get(0).child(0).child(0).child(0).child(0).child(0).text();
            if (song == null)
                song = lyricsPage.select("div.caption").get(0).child(0).text();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (text == null)
            lyrics = new Lyrics(Lyrics.ERROR);
        else
            lyrics = new Lyrics(Lyrics.POSITIVE_RESULT);
        lyrics.setArtist(artist);
        lyrics.setTitle(song);
        lyrics.setText(text);
        lyrics.setSource("J-Lyric");
        lyrics.setURL(url);
        return lyrics;

    }

}