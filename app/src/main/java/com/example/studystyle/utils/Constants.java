package com.example.studystyle.utils;

public class Constants {
    // ZenQuotes API (motivasi) — tidak perlu API key
    public static final String BASE_URL_QUOTE = "https://zenquotes.io/";

    // Open Library API — gratis, tanpa API key
    public static final String BASE_URL_BOOKS        = "https://openlibrary.org/";
    public static final String BASE_URL_COVERS       = "https://covers.openlibrary.org/";
    public static final String BASE_URL_BOOKS_WORKS  = "https://openlibrary.org/";

    // SharedPreferences
    public static final String PREF_NAME        = "StudyStylePrefs";
    public static final String KEY_IS_LOGGED_IN = "is_logged_in";
    public static final String KEY_USER_NAME    = "user_name";
    public static final String KEY_USER_EMAIL   = "user_email";
    public static final String KEY_USER_JURUSAN = "user_jurusan";
    public static final String KEY_USER_ID      = "user_id";
    public static final String KEY_DARK_MODE    = "dark_mode";

    // Hasil tes
    public static final String KEY_LAST_RESULT     = "last_result_";
    public static final String KEY_LAST_VISUAL     = "last_visual_";
    public static final String KEY_LAST_AUDITORY   = "last_auditory_";
    public static final String KEY_LAST_KINESTETIK = "last_kinestetik_";

    // Cache quote
    public static final String KEY_CACHED_QUOTE        = "cached_quote";
    public static final String KEY_CACHED_QUOTE_AUTHOR = "cached_quote_author";

    // Styles
    public static final String STYLE_VISUAL     = "Visual";
    public static final String STYLE_AUDITORY   = "Auditori";
    public static final String STYLE_KINESTETIK = "Kinestetik";

    // Database
    public static final String DB_NAME    = "studystyle.db";
    public static final int    DB_VERSION = 1;

    // Intent keys
    public static final String INTENT_RESULT_TYPE      = "result_type";
    public static final String INTENT_VISUAL_SCORE     = "visual_score";
    public static final String INTENT_AUDITORY_SCORE   = "auditory_score";
    public static final String INTENT_KINESTETIK_SCORE = "kinestetik_score";

    // Book search queries per learning style (Open Library)
    public static final String BOOK_QUERY_VISUAL     = "visual learning study";
    public static final String BOOK_QUERY_AUDITORY   = "auditory learning music";
    public static final String BOOK_QUERY_KINESTETIK = "kinesthetic learning activity";
}