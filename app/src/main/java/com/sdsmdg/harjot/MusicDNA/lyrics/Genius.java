package com.sdsmdg.harjot.MusicDNA.lyrics;

import com.sdsmdg.harjot.MusicDNA.Config;
import com.sdsmdg.harjot.MusicDNA.annotations.Reflection;
import com.sdsmdg.harjot.MusicDNA.utilities.Net;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Genius {

    @Reflection
    public static final String domain = "genius.com";

    public static ArrayList<Lyrics> search(String query) {
        ArrayList<Lyrics> results = new ArrayList<>();
        query = Normalizer.normalize(query, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        JsonObject response = null;
        try {
            URL queryURL = new URL(String.format("http://api.genius.com/search?q=%s", URLEncoder.encode(query, "UTF-8")));
            Connection connection = Jsoup.connect(queryURL.toExternalForm())
                    .header("Authorization", "Bearer " + Config.GENIUS)
                    .timeout(0)
                    .ignoreContentType(true);
            Document document = connection.userAgent(Net.USER_AGENT).get();
            response = new JsonParser().parse(document.text()).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (response == null || response.getAsJsonObject("meta").get("status").getAsInt() != 200)
            return results;
        JsonArray hits = response.getAsJsonObject("response").getAsJsonArray("hits");

        int processed = 0;
        while (processed < hits.size()) {
            JsonObject song = hits.get(processed).getAsJsonObject().getAsJsonObject("result");
            String artist = song.getAsJsonObject("primary_artist").get("name").getAsString();
            String title = song.get("title").getAsString();
            String url = "http://genius.com/songs/" + song.get("id").getAsString();
            Lyrics l = new Lyrics(Lyrics.SEARCH_ITEM);
            l.setArtist(artist);
            l.setTitle(title);
            l.setURL(url);
            l.setSource("Genius");
            results.add(l);
            processed++;
        }
        return results;
    }

    @Reflection
    public static Lyrics fromMetaData(String originalArtist, String originalTitle) {
        String urlArtist = Normalizer.normalize(originalArtist, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        String urlTitle = Normalizer.normalize(originalTitle, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        urlArtist = urlArtist.replaceAll("[^a-zA-Z0-9\\s+]", "").replaceAll("&", "and")
                .trim().replaceAll("[\\s+]", "-");
        urlTitle = urlTitle.replaceAll("[^a-zA-Z0-9\\s+]", "").replaceAll("&", "and")
                .trim().replaceAll("[\\s+]", "-");
        String url = String.format("http://genius.com/%s-%s-lyrics", urlArtist, urlTitle);
        return fromURL(url, originalArtist, originalTitle);
    }

    public static Lyrics fromURL(String url, String artist, String title) {
        Document lyricsPage;
        String text;
        try {
            lyricsPage = Jsoup.connect(url).userAgent(Net.USER_AGENT).get();
            Elements lyricsDiv = lyricsPage.select(".lyrics");
            if (lyricsDiv.isEmpty())
                throw new StringIndexOutOfBoundsException();
            else
                text = Jsoup.clean(lyricsDiv.html(), Whitelist.none().addTags("br")).trim();
        } catch (HttpStatusException e) {
            return new Lyrics(Lyrics.NO_RESULT);
        } catch (IOException | StringIndexOutOfBoundsException e) {
            e.printStackTrace();
            return new Lyrics(Lyrics.ERROR);
        }
        if (artist == null) {
            title = lyricsPage.getElementsByClass("text_title").get(0).text();
            artist = lyricsPage.getElementsByClass("text_artist").get(0).text();
        }
        Lyrics result = new Lyrics(Lyrics.POSITIVE_RESULT);
        if ("[Instrumental]".equals(text))
            result = new Lyrics(Lyrics.NEGATIVE_RESULT);
        Pattern pattern = Pattern.compile("\\[.+\\]");
        StringBuilder builder = new StringBuilder();
        for (String line : text.split("<br> ")) {
            String strippedLine = line.replaceAll("\\s", "");
            if (!pattern.matcher(strippedLine).matches() && !(strippedLine.isEmpty() && builder.length() == 0))
                builder.append(line.replaceAll("\\P{Print}", "")).append("<br/>");
        }
        if (builder.length() > 5)
            builder.delete(builder.length() - 5, builder.length());
        result.setArtist(artist);
        result.setTitle(title);
        result.setText(Normalizer.normalize(builder.toString(), Normalizer.Form.NFD));
        result.setURL(url);
        result.setSource("Genius");
        return result;
    }

}
