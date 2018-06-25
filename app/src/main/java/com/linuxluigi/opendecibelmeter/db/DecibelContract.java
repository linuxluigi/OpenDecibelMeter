package com.linuxluigi.opendecibelmeter.db;

import android.provider.BaseColumns;

public class DecibelContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private DecibelContract() {}

    /**
     * Inner class that defines constant values for the log database table.
     * Each entry in the table represents a single log entry.
     */
    public static final class LogEntry implements BaseColumns {

        /** Name of database table for pets */
        public final static String TABLE_NAME = "log";

        /**
         * Unique ID number for the pet (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * timestamp of the record
         *
         * Type: DATETIME
         */
        public final static String COLUMN_LOG_TIMESTAMP ="timestamp";

        /**
         * decibel amplitude
         *
         * Type: DOUBLE
         */
        public final static String COLUMN_LOG_DECIBEL ="decibel";

        /**
         * latitude position of the record
         *
         * Type: DOUBLE
         */
        public final static String COLUMN_LOG_LATITUDE ="latitude";

        /**
         * longitude position of the record
         *
         * Type: DOUBLE
         */
        public final static String COLUMN_LOG_LONGITUTE ="longitude";

    }
}
