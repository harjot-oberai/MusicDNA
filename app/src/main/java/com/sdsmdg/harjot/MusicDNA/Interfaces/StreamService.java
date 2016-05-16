package com.sdsmdg.harjot.MusicDNA.Interfaces;

import com.sdsmdg.harjot.MusicDNA.Config;
import com.sdsmdg.harjot.MusicDNA.Models.Track;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Harjot on 30-Apr-16.
 */
public interface StreamService {
    @GET("/tracks?client_id=" + Config.CLIENT_ID)
    Call<List<Track>> getTracks(@Query("q") String query, @Query("limit") int limit);
}
