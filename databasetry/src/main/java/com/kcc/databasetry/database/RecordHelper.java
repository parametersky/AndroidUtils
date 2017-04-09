package com.kcc.databasetry.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by ford-pro2 on 16/4/30.
 */
public class RecordHelper extends SQLiteOpenHelper {
    private static final String TAG = "RecordHelper";

    public RecordHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public RecordHelper(Context context) {
        super(context, "Record", null, 1);
        Log.i(TAG, "RecordHelper: constructor ");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate: ");
//        db.execSQL(Record.getCreateTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "onUpgrade: ");
    }
}
