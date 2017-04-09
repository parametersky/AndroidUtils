package com.kcc.databasetry.database;

import android.provider.BaseColumns;

/**
 * Created by ford-pro2 on 16/4/30.
 */
public  class DatabaseItem implements BaseColumns {
    public  static String getCreateTable(){
        return "";
    }
    public  static String getDeleteTable(){
        return "";
    }

    protected static final String TEXT_TYPE = " TEXT";
    protected static final String COMMA_SEP = ",";
    protected static final String COLUMN_NAME_ENTRY_ID = "entry_id";
    public static final String TABLE_NAME = "test";
//    private static final String SQL_CREATE_ENTRIES =
//            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
//                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
//                    FeedEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
//                    FeedEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
//    ... // Any other options for the CREATE command
//            " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;
}
