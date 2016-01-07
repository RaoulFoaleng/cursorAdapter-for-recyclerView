package com.foaleng.recyclerview.cursor.content;

import android.net.Uri;
import android.provider.BaseColumns;


public class Data {

    public static final String AUTORITY = "com.foaleng.recyclerview.cursor.provider";
    public static final Uri BASE_URI = Uri.parse("content://" + AUTORITY);

    public static final String TEXT_TYPE = " TEXT";
    public static final String COMMA_SEP = ",";

    /* Inner class that defines the table contents */
    public static abstract class Entry implements BaseColumns {

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + Entry.TABLE_NAME + " (" +
                        Entry._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                        Entry.COLUMN_NAME_VALUE + TEXT_TYPE +
                        " )";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + Entry.TABLE_NAME;

        public static final String TABLE_NAME = "entry";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, TABLE_NAME);
        public static final String COLUMN_NAME_VALUE = "value";
    }
}
