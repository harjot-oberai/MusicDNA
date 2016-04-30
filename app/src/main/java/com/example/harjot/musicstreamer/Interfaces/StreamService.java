package com.example.harjot.musicstreamer.Interfaces;

import com.example.harjot.musicstreamer.Config;
import com.example.harjot.musicstreamer.MainActivity;
import com.example.harjot.musicstreamer.Models.Track;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Harjot on 30-Apr-16.
 */
public interface StreamService {
    @GET("/tracks?client_id=" + Config.CLIENT_ID)
    Call<List<Track>> getTracks(@Query("q") String query, @Query("limit") int limit);
}
