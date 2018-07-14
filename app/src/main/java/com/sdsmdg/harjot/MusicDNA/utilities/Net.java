package com.sdsmdg.harjot.MusicDNA.utilities;

import java.io.IOException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
//import java.util.concurrent.TimeUnit;

public class Net {

    public static String USER_AGENT =
            "Mozilla/5.0 (Linux; U; Android 6.0.1; ko-kr; Build/IML74K) AppleWebkit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30";

    public static String getUrlAsString(String paramURL) throws IOException {
        return getUrlAsString(new URL(paramURL));
    }

    public static String getUrlAsString(URL paramURL) throws IOException {
        Request request = new Request.Builder().header("User-Agent", USER_AGENT).url(paramURL).build();
        OkHttpClient client = new OkHttpClient();
        //client.connectTimeout(10, TimeUnit.SECONDS);
        Response response = client.newCall(request).execute();

        return response.body().string();
    }
}