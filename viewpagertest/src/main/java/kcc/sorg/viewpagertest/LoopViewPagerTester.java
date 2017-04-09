package kcc.sorg.viewpagertest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import kcc.sorg.viewpagertest.view.LoopViewPager;

/**
 * Author: Kyle Cheng
 * Email: kyle.cheng@live.com
 * Date: 16/10/13
 */
public class LoopViewPagerTester extends AppCompatActivity {
    private static final String TAG = "LoopViewPagerTester";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: ");
        setContentView(R.layout.activity_main3);
        LoopViewPager pager = (LoopViewPager)findViewById(R.id.viewpager);
        ArrayList<SongInfo> info = new ArrayList<>();
        info.add(new SongInfo("song1","name1"));
        info.add(new SongInfo("song2","name2"));
        info.add(new SongInfo("song3","name3"));
        info.add(new SongInfo("song4","name4"));
        info.add(new SongInfo("song5","name5"));
        info.add(new SongInfo("song6","name6"));
        info.add(new SongInfo("song7","name7"));
        info.add(new SongInfo("song8","name8"));
        pager.setLoop(false);
        pager.setContentAndViewUpdater(info,3,new LoopViewPager.ViewUpdater() {
            @Override
            public void updateView(View view, Object content) {
                TextView name = (TextView) view.findViewById(R.id.song_name);
                TextView singer = (TextView) view.findViewById(R.id.song_singer);
                SongInfo info = (SongInfo) content;
//                Log.i(TAG, "updateView: "+info.toString());
                name.setText(info.songName);
                singer.setText(info.songSinger);
            }

            @Override
            public void onItemChanged(int index) {
                Log.i(TAG, "onItemChanged: "+index);
            }
        });
    }
    public class SongInfo{
        String songName;
        String songSinger;
        public SongInfo(String name, String singer){
            songName = name;
            songSinger = singer;
        }

        @Override
        public String toString() {
            return "SongInfo{" +
                    "songName='" + songName + '\'' +
                    ", songSinger='" + songSinger + '\'' +
                    '}';
        }
    }
}
