package com.kcc.databasetry;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.kcc.databasetry.database.Record;
import com.kcc.databasetry.database.RecordHelper;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecordHelper helper = new RecordHelper(this);
        SQLiteDatabase database = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Record.COLUMN_NAME_DATE,"2016-4-30");
        values.put(Record.COLUMN_NAME_NUMBER,"93.3");
        values.put(Record.COLUMN_NAME_TYPE,"1");
        database.insert(Record.TABLE_NAME,null,values);
//        database.update()
        Cursor cursor = database.query(Record.TABLE_NAME,new String[]{Record.COLUMN_NAME_DATE,Record.COLUMN_NAME_TYPE,Record.COLUMN_NAME_NUMBER},null,
                null,null,null,null);
        if(cursor != null){
            cursor.moveToFirst();
            Log.i(TAG, "onCreate: "+cursor.getString(0)+" "+cursor.getString(1)+" "+cursor.getString(2));
        } else {
            Log.i(TAG, "onCreate: cursor is null");
        }
    }
}
