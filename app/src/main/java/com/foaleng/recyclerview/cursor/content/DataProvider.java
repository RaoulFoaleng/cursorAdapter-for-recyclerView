package com.foaleng.recyclerview.cursor.content;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;

public class DataProvider extends ContentProvider {

    /*
     * Defines a handle to the database helper object. The DataDbHelper class is defined
     * in a following snippet.
     */
    private DataDbHelper mDataDbHelper;

    private SQLiteDatabase db;

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "data.db";

    // Creates a UriMatcher object.
    private static UriMatcher sUriMatcher;

    private static final int ENTRY = 0;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(Data.AUTORITY, Data.Entry.TABLE_NAME, ENTRY);
    }

    @Override
    public boolean onCreate() {
        /*
         * Creates a new helper object. This method always returns quickly.
         * Notice that the database itself isn't created or opened
         * until SQLiteOpenHelper.getWritableDatabase is called
         */
        mDataDbHelper = new DataDbHelper(
                getContext(),        // the application context
                DATABASE_NAME,       // the name of the database)
                null,                // uses the default SQLite cursor
                1                    // the version number
        );

        /**
         * Create a write able database which will trigger its
         * creation if it doesn't already exist.
         */
        db = mDataDbHelper.getWritableDatabase();
        return (db == null) ? false : true;
    }


    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID = -1L;

        switch (sUriMatcher.match(uri)) {
            case ENTRY:
                rowID = db.insertOrThrow(Data.Entry.TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException(" no table for uri " + uri);
        }

        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(Data.BASE_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }

        throw new SQLException("Failed to add a record into " + uri + ", rowID " + rowID);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int row = 0;

        switch (sUriMatcher.match(uri)) {
            case ENTRY:
                row = db.update(Data.Entry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException(" no table for uri " + uri);
        }

        if (row > 0) {
            getContext().getContentResolver().notifyChange(Data.BASE_URI, null);
        }

        return row;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int row = 0;

        switch (sUriMatcher.match(uri)) {
            case ENTRY:
                row = db.delete(Data.Entry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException(" no table for uri " + uri);
        }

        if (row > 0) {
            getContext().getContentResolver().notifyChange(Data.BASE_URI, null);
        }

        return row;
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor cursor = null;

        switch (sUriMatcher.match(uri)) {
            case ENTRY:
                cursor = db.query(Data.Entry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException(" no table for uri " + uri);
        }

        if (cursor != null)
            cursor.setNotificationUri(getContext().getContentResolver(), Data.BASE_URI);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }


    public class DataDbHelper extends SQLiteOpenHelper {

        public DataDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        public DataDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(Data.Entry.SQL_CREATE_ENTRIES);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(Data.Entry.SQL_DELETE_ENTRIES);
            onCreate(db);
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }
}
