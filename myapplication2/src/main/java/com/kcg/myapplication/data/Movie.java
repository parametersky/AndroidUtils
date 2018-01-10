package com.kcg.myapplication.data;

/**
 *
 * Created by zhouwei on 16/11/9.
 */

public class Movie {
    public Rate rating;
    public String title;
    public String collect_count;
    public String original_title;
    public String subtype;
    public String year;
    public MovieImage images;

    public static class Rate{
        public int max;
        public float average;
        public String stars;
        public int min;

        @Override
        public String toString() {
            return "Rate{" +
                    "max=" + max +
                    ", average=" + average +
                    ", stars='" + stars + '\'' +
                    ", min=" + min +
                    '}';
        }
    }

    public static class MovieImage{
        public String small;
        public String large;
        public String medium;

        @Override
        public String toString() {
            return "MovieImage{" +
                    "small='" + small + '\'' +
                    ", large='" + large + '\'' +
                    ", medium='" + medium + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Movie{" +
                "ratng=" + rating +
                ", title='" + title + '\'' +
                ", collect_count='" + collect_count + '\'' +
                ", original_title='" + original_title + '\'' +
                ", subtype='" + subtype + '\'' +
                ", year='" + year + '\'' +
                ", images=" + images +
                '}';
    }
}
