package com.kcg.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.kcg.myapplication.data.MovieSubject;
import com.kcg.myapplication.network.Config;
import com.kcg.myapplication.network.GitHubManager;
import com.kcg.myapplication.network.GitHubService;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView textview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textview = (TextView)findViewById(R.id.textview);
        GitHubService service = GitHubManager.getInstance().getService();
        service.getTop250(0,1).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<MovieSubject>() {
            @Override
            public void call(MovieSubject movieSubject) {
                textview.setText(movieSubject.toString());
            }
        });//"kyle");
//        call.enqueue(new Callback<MovieSubject>() {
//            @Override
//            public void onResponse(Call<MovieSubject> call, Response<MovieSubject> response) {
//                if( response.isSuccessful()){
////                    try {
//                        Log.i(TAG, "onResponse: " + response.body().toString());
////                    }catch (IOException e){
////                        Log.e(TAG, "onResponse: " );
////                        e.printStackTrace();
////                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<MovieSubject> call, Throwable t) {
//                t.printStackTrace();
//            }
//        });

    }





}
