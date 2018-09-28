package com.randall.proxyclient.client;

import com.randall.proxyclient.entity.Proxy;
import retrofit2.Call;
import retrofit2.http.GET;

import java.util.List;

/**
 * Description:
 *
 * @author damon4u
 * @version 2018-09-14 11:01
 */
public interface GitProxyClient {

    @GET("/stamparm/aux/master/fetch-some-list.txt")
    Call<List<Proxy>> loadProxy();
}
