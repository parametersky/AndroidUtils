package com.kcg.myapplication.network;

import com.kcg.myapplication.data.Movie;
import com.kcg.myapplication.data.MovieSubject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by kyle on 2018/1/3.
 */

public  interface GitHubService{
    @GET("users/{user}/repos")
    Call<ResponseBody> listRepos(@Path("user") String user);
    @GET("top250")
    Observable<MovieSubject> getTop250(@Query("start") int start, @Query("count") int count);
}
