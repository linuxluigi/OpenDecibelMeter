package com.linuxluigi.opendecibelmeter.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.linuxluigi.opendecibelmeter.models.LogEntry;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DecibelsQueries {

    /**
     * Load all rows from log into a list
     *
     * @param mDbHelper database helper class
     * @return list with all entries from log db
     */
    public static List<LogEntry> getAllRows(DecibelDbHelper mDbHelper) {
        List<LogEntry> logList = new ArrayList<>();

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                DecibelContract.LogEntry._ID,
                DecibelContract.LogEntry.COLUMN_LOG_DECIBEL,
                DecibelContract.LogEntry.COLUMN_LOG_LATITUDE,
                DecibelContract.LogEntry.COLUMN_LOG_LONGITUTE,
                DecibelContract.LogEntry.COLUMN_LOG_TIMESTAMP};

        // Perform a query on the log table

        try (Cursor cursor = db.query(
                DecibelContract.LogEntry.TABLE_NAME,   // The table to query
                projection,            // The columns to return
                null,                  // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null)) {

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(DecibelContract.LogEntry._ID);
            int deceibelColumnIndex = cursor.getColumnIndex(DecibelContract.LogEntry.COLUMN_LOG_DECIBEL);
            int latitudeColumnIndex = cursor.getColumnIndex(DecibelContract.LogEntry.COLUMN_LOG_LATITUDE);
            int longituteColumnIndex = cursor.getColumnIndex(DecibelContract.LogEntry.COLUMN_LOG_LONGITUTE);
            int timestampColumnIndex = cursor.getColumnIndex(DecibelContract.LogEntry.COLUMN_LOG_TIMESTAMP);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt(idColumnIndex);
                Double deceibel = cursor.getDouble(deceibelColumnIndex);
                Double latitude = cursor.getDouble(latitudeColumnIndex);
                Double longitute = cursor.getDouble(longituteColumnIndex);
                Date timestamp = Timestamp.valueOf(cursor.getString(timestampColumnIndex));

                LogEntry log = new LogEntry(currentID, deceibel, latitude, longitute, timestamp);
                logList.add(log);

            }
        }
        // Always close the cursor when you're done reading from it. This releases all its
        // resources and makes it invalid.

        return logList;
    }

}
