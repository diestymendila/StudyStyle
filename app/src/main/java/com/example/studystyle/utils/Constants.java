package com.example.studystyle.utils;

public class Constants {
    // ZenQuotes API (motivasi) — tidak perlu API key
    public static final String BASE_URL_QUOTE = "https://zenquotes.io/";

    // GetBooksInfo RapidAPI
    public static final String BASE_URL_BOOKS = "https://getbooksinfo.p.rapidapi.com/";
    public static final String RAPIDAPI_KEY   = "8bf5456eecmsh25089ea2f19b4ffp175646jsna9b29cd7814d";
    public static final String RAPIDAPI_HOST  = "getbooksinfo.p.rapidapi.com";

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

    // Book search queries per learning style
    public static final String BOOK_QUERY_VISUAL     = "visual learning";
    public static final String BOOK_QUERY_AUDITORY   = "auditory learning";
    public static final String BOOK_QUERY_KINESTETIK = "kinesthetic learning";
}