package com.sdsmdg.harjot.MusicDNA.interfaces;

import com.sdsmdg.harjot.MusicDNA.Config;
import com.sdsmdg.harjot.MusicDNA.models.Track;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Harjot on 30-Apr-16.
 */
public interface StreamService {
    @GET("/tracks?client_id=" + Config.CLIENT_ID)
    Call<List<Track>> getTracks(@Query("q") String query, @Query("limit") int limit);
}
