package com.example.studystyle.utils;

public class Constants {
    // API
    public static final String BASE_URL = "https://api.quotable.io/";
    public static final String QUOTE_TAG = "education";

    // SharedPreferences
    public static final String PREF_NAME = "StudyStylePrefs";
    public static final String KEY_IS_LOGGED_IN = "is_logged_in";
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_USER_EMAIL = "user_email";
    public static final String KEY_USER_JURUSAN = "user_jurusan";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_DARK_MODE = "dark_mode";
    public static final String KEY_LAST_RESULT = "last_result";
    public static final String KEY_LAST_VISUAL = "last_visual";
    public static final String KEY_LAST_AUDITORY = "last_auditory";
    public static final String KEY_LAST_KINESTETIK = "last_kinestetik";

    // Learning styles
    public static final String STYLE_VISUAL = "Visual";
    public static final String STYLE_AUDITORY = "Auditori";
    public static final String STYLE_KINESTETIK = "Kinestetik";

    // Database
    public static final String DB_NAME = "studystyle.db";
    public static final int DB_VERSION = 1;

    // Intent keys
    public static final String INTENT_RESULT_TYPE = "result_type";
    public static final String INTENT_VISUAL_SCORE = "visual_score";
    public static final String INTENT_AUDITORY_SCORE = "auditory_score";
    public static final String INTENT_KINESTETIK_SCORE = "kinestetik_score";
}
