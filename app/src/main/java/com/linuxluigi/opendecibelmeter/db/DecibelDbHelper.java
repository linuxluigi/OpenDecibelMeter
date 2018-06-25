package com.linuxluigi.opendecibelmeter.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.linuxluigi.decibelmetermonitor.data.DecibelContract.LogEntry;

public class DecibelDbHelper extends SQLiteOpenHelper {


    public static final String LOG_TAG = DecibelDbHelper.class.getSimpleName();

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "decibelMeter.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link DecibelDbHelper}.
     *
     * @param context of the app
     */
    public DecibelDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_PETS_TABLE = "CREATE TABLE " + LogEntry.TABLE_NAME + " ("
                + LogEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + LogEntry.COLUMN_LOG_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL, "
                + LogEntry.COLUMN_LOG_DECIBEL + " DOUBLE NOT NULL, "
                + LogEntry.COLUMN_LOG_LATITUDE + " DOUBLE NOT NULL, "
                + LogEntry.COLUMN_LOG_LONGITUTE + " DOUBLE NOT NULL);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}