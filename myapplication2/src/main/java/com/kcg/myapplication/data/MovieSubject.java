package com.kcg.myapplication.data;

import java.util.List;

/**
 * Created by zhouwei on 16/11/9.
 */

public class MovieSubject {
   public int count;
   public int start;
   public int total;
   public List<Movie>  subjects;
   public String title;

    @Override
    public String toString() {
        return "MovieSubject{" +
                "count=" + count +
                ", start=" + start +
                ", total=" + total +
                ", subjects=" + subjects +
                ", title='" + title + '\'' +
                '}';
    }
}
