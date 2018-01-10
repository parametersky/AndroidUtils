package com.kcg.myapplication.network;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by kyle on 2018/1/3.
 */

public class GitHubManager {
    private static GitHubManager instance = null;
    private GitHubService service = null;
    private GitHubManager(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        service = retrofit.create(GitHubService.class);
    }

    public static GitHubManager getInstance(){
        if(instance == null){
            instance = new GitHubManager();
        }
        return instance;
    }
    public GitHubService getService(){
        return service;
    }

}
