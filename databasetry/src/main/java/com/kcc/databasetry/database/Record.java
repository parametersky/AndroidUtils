package com.kcc.databasetry.database;

/**
 * Created by ford-pro2 on 16/4/30.
 */
public class Record extends DatabaseItem {

    private String date;
    private int type;
    private float number;

    public static final String COLUMN_NAME_DATE = "date";
    public static final String COLUMN_NAME_TYPE = "type";
    public static final String COLUMN_NAME_NUMBER = "number";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_DATE + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_TYPE + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_NUMBER + TEXT_TYPE +
                    "); ";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public Record(String date, int type, float number) {

        this.date = date;
        this.type = type;
        this.number = number;
    }

    public static  String getCreateTable() {
        return SQL_CREATE_ENTRIES;
    }

    public static  String getDeleteTable() {
        return SQL_DELETE_ENTRIES;
    }
}
