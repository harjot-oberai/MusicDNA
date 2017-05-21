package com.sdsmdg.harjot.MusicDNA.utilities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

import com.sdsmdg.harjot.MusicDNA.lyrics.AZLyrics;
import com.sdsmdg.harjot.MusicDNA.lyrics.Bollywood;
import com.sdsmdg.harjot.MusicDNA.lyrics.Genius;
import com.sdsmdg.harjot.MusicDNA.lyrics.JLyric;
import com.sdsmdg.harjot.MusicDNA.lyrics.Lololyrics;
import com.sdsmdg.harjot.MusicDNA.lyrics.LyricWiki;
import com.sdsmdg.harjot.MusicDNA.lyrics.Lyrics;
import com.sdsmdg.harjot.MusicDNA.lyrics.LyricsMania;
import com.sdsmdg.harjot.MusicDNA.lyrics.MetalArchives;
import com.sdsmdg.harjot.MusicDNA.lyrics.PLyrics;
import com.sdsmdg.harjot.MusicDNA.lyrics.UrbanLyrics;
import com.sdsmdg.harjot.MusicDNA.lyrics.ViewLyrics;

import java.util.ArrayList;
import java.util.Arrays;

public class DownloadThread extends Thread {

    private static final String[] mainProviders =
            {

                    "AZLyrics",
                    "Genius",
                    "LyricWiki",
                    "LyricsMania",
                    "Bollywood"
            };

    private static ArrayList<String> providers = new ArrayList<>(Arrays.asList(mainProviders));

    public DownloadThread(final Lyrics.Callback callback, boolean positionAvailable, final String... params) {
        super(DownloadThread.getRunnable(callback, positionAvailable, params));
    }

    public static void setProviders(Iterable<String> providers) {
        DownloadThread.providers = new ArrayList<>(Arrays.asList(mainProviders));
        for (String provider : providers) {
            if (provider.equals("ViewLyrics"))
                DownloadThread.providers.add(0, provider);
            else
                DownloadThread.providers.add(provider);
        }
    }

    public static Runnable getRunnable(final Lyrics.Callback callback, final boolean positionAvailable, final String... params) {
        return new Runnable() {

            @SuppressWarnings("unchecked")
            public Lyrics download(String url, String artist, String title) {
                Lyrics lyrics = null;
                for (String provider : providers) {
                    switch (provider) {
                        case "AZLyrics":
                            lyrics = AZLyrics.fromURL(url, artist, title);
                            break;
                        case "Bollywood":
                            lyrics = Bollywood.fromURL(url, artist, title);
                            break;
                        case "Genius":
                            lyrics = Genius.fromURL(url, artist, title);
                            break;
                        case "JLyric":
                            lyrics = JLyric.fromURL(url, artist, title);
                            break;
                        case "Lololyrics":
                            lyrics = Lololyrics.fromURL(url, artist, title);
                            break;
                        case "LyricsMania":
                            lyrics = LyricsMania.fromURL(url, artist, title);
                            break;
                        case "LyricWiki":
                            lyrics = LyricWiki.fromURL(url, artist, title);
                            break;
                        case "MetalArchives":
                            lyrics = MetalArchives.fromURL(url, artist, title);
                            break;
                        case "PLyrics":
                            lyrics = PLyrics.fromURL(url, artist, title);
                            break;
                        case "UrbanLyrics":
                            lyrics = UrbanLyrics.fromURL(url, artist, title);
                            break;
                        case "ViewLyrics":
                            lyrics = ViewLyrics.fromURL(url, artist, title);
                            break;
                    }
                    if (lyrics.isLRC() && !positionAvailable)
                        continue;
                    if (lyrics != null && lyrics.getFlag() == Lyrics.POSITIVE_RESULT)
                        return lyrics;
                }
                return new Lyrics(Lyrics.NO_RESULT);
            }

            @SuppressWarnings("unchecked")
            public Lyrics download(String artist, String title) {
                Lyrics lyrics = new Lyrics(Lyrics.NO_RESULT);
                for (String provider : providers) {
                    switch (provider) {
                        case "AZLyrics":
                            lyrics = AZLyrics.fromMetaData(artist, title);
                            break;
                        case "Bollywood":
                            lyrics = Bollywood.fromMetaData(artist, title);
                            break;
                        case "Genius":
                            lyrics = Genius.fromMetaData(artist, title);
                            break;
                        case "JLyric":
                            lyrics = JLyric.fromMetaData(artist, title);
                            break;
                        case "Lololyrics":
                            lyrics = Lololyrics.fromMetaData(artist, title);
                            break;
                        case "LyricsMania":
                            lyrics = LyricsMania.fromMetaData(artist, title);
                            break;
                        case "LyricWiki":
                            lyrics = LyricWiki.fromMetaData(artist, title);
                            break;
                        case "MetalArchives":
                            lyrics = MetalArchives.fromMetaData(artist, title);
                            break;
                        case "PLyrics":
                            lyrics = PLyrics.fromMetaData(artist, title);
                            break;
                        case "UrbanLyrics":
                            lyrics = UrbanLyrics.fromMetaData(artist, title);
                            break;
                        case "ViewLyrics":
                            try {
                                lyrics = ViewLyrics.fromMetaData(artist, title);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                    if (lyrics.isLRC() && !positionAvailable)
                        continue;
                    if (lyrics != null && lyrics.getFlag() == Lyrics.POSITIVE_RESULT)
                        return lyrics;
                }
                return lyrics;
            }


            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

                Lyrics lyrics;
                String artist = null;
                String title = null;
                String url = null;
                switch (params.length) {
                    case 3: // URL + tags
                        artist = params[1];
                        title = params[2];
                    case 1: // URL
                        url = params[0];
                        lyrics = download(url, artist, title);
                        break;
                    default: // just tags
                        artist = params[0];
                        title = params[1];
                        lyrics = download(params[0], params[1]);
                }
                if (lyrics.getFlag() != Lyrics.POSITIVE_RESULT) {
                    String[] correction = correctTags(artist, title);
                    if (!(correction[0].equals(artist) && correction[1].equals(title)) || url != null) {
                        lyrics = download(correction[0], correction[1]);
                        lyrics.setOriginalArtist(artist);
                        lyrics.setOriginalTitle(title);
                    }
                }
                if (lyrics.getArtist() == null) {
                    if (artist != null) {
                        lyrics.setArtist(artist);
                        lyrics.setTitle(title);
                    } else {
                        lyrics.setArtist("");
                        lyrics.setTitle("");
                    }
                }
                threadMsg(lyrics);
            }

            private void threadMsg(Lyrics lyrics) {
                if (lyrics != null) {
                    Message msgObj = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putSerializable("lyrics", lyrics);
                    msgObj.setData(b);
                    handler.sendMessage(msgObj);
                }
            }

            // Define the Handler that receives messages from the thread and update the progress
            private final Handler handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    Lyrics result = (Lyrics) msg.getData().getSerializable("lyrics");
                    if (result != null)
                        callback.onLyricsDownloaded(result);
                }
            };
        };
    }

    public static String[] correctTags(String artist, String title) {
        if (artist == null || title == null)
            return new String[]{"", ""};
        String correctedArtist = artist.replaceAll("\\(.*\\)", "")
                .replaceAll(" \\- .*", "").trim();
        String correctedTrack = title.replaceAll("\\(.*\\)", "")
                .replaceAll("\\[.*\\]", "").replaceAll(" \\- .*", "").trim();
        String[] separatedArtists = correctedArtist.split(", ");
        correctedArtist = separatedArtists[separatedArtists.length - 1];
        return new String[]{correctedArtist, correctedTrack};
    }
}