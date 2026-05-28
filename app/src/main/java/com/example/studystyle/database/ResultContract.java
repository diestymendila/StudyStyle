package com.example.studystyle.database;

import android.provider.BaseColumns;

/**
 * Contract class mendefinisikan konstanta nama tabel dan kolom
 * untuk tabel results di SQLite, mengikuti pola Android ContentProvider contract.
 */
public final class ResultContract {

    // Sembunyikan constructor agar tidak di-instantiate
    private ResultContract() {}

    /**
     * Inner class yang mendefinisikan konten tabel results.
     */
    public static class ResultEntry implements BaseColumns {
        public static final String TABLE_NAME         = "results";
        public static final String COLUMN_USER_ID     = "user_id";
        public static final String COLUMN_VISUAL      = "visual_score";
        public static final String COLUMN_AUDITORY    = "auditory_score";
        public static final String COLUMN_KINESTETIK  = "kinestetik_score";
        public static final String COLUMN_RESULT_TYPE = "result_type";
        public static final String COLUMN_DATE        = "date";

        // SQL untuk CREATE TABLE
        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID                  + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_USER_ID       + " INTEGER NOT NULL, " +
                        COLUMN_VISUAL        + " INTEGER NOT NULL, " +
                        COLUMN_AUDITORY      + " INTEGER NOT NULL, " +
                        COLUMN_KINESTETIK    + " INTEGER NOT NULL, " +
                        COLUMN_RESULT_TYPE   + " TEXT NOT NULL, " +
                        COLUMN_DATE          + " TEXT NOT NULL" +
                        ")";

        // SQL untuk DROP TABLE
        public static final String SQL_DROP_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    /**
     * Inner class untuk tabel users.
     */
    public static class UserEntry implements BaseColumns {
        public static final String TABLE_NAME      = "users";
        public static final String COLUMN_NAME     = "nama";
        public static final String COLUMN_EMAIL    = "email";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_JURUSAN  = "jurusan";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID             + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_NAME     + " TEXT NOT NULL, " +
                        COLUMN_EMAIL    + " TEXT UNIQUE NOT NULL, " +
                        COLUMN_PASSWORD + " TEXT NOT NULL, " +
                        COLUMN_JURUSAN  + " TEXT" +
                        ")";

        public static final String SQL_DROP_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
