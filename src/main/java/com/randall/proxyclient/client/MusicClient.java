package com.randall.proxyclient.client;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * Description:
 *
 * @author damon4u
 * @version 2018-09-14 11:01
 */
public interface MusicClient {

    @GET("song")
    @Headers("Referer: http://music.163.com/")
    Call<String> songInfo(@Query("id") long songId);

}
