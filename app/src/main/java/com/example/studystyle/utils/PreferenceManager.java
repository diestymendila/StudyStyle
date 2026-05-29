package com.example.studystyle.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public PreferenceManager(Context context) {
        prefs  = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void    setLoggedIn(boolean v) { editor.putBoolean(Constants.KEY_IS_LOGGED_IN, v).apply(); }
    public boolean isLoggedIn()           { return prefs.getBoolean(Constants.KEY_IS_LOGGED_IN, false); }

    public void saveUser(String name, String email, String jurusan, int userId) {
        editor.putString(Constants.KEY_USER_NAME, name)
                .putString(Constants.KEY_USER_EMAIL, email)
                .putString(Constants.KEY_USER_JURUSAN, jurusan)
                .putInt(Constants.KEY_USER_ID, userId).apply();
    }

    public String getUserName()    { return prefs.getString(Constants.KEY_USER_NAME, ""); }
    public String getUserEmail()   { return prefs.getString(Constants.KEY_USER_EMAIL, ""); }
    public String getUserJurusan() { return prefs.getString(Constants.KEY_USER_JURUSAN, ""); }
    public int    getUserId()      { return prefs.getInt(Constants.KEY_USER_ID, -1); }

    public void    setDarkMode(boolean v) { editor.putBoolean(Constants.KEY_DARK_MODE, v).apply(); }
    public boolean isDarkMode()           { return prefs.getBoolean(Constants.KEY_DARK_MODE, false); }

    // Simpan hasil tes dengan key berbasis userId agar tidak hilang saat logout
    public void saveLastResult(String type, int v, int a, int k) {
        int userId = getUserId();
        editor.putString(Constants.KEY_LAST_RESULT     + userId, type)
                .putInt(Constants.KEY_LAST_VISUAL        + userId, v)
                .putInt(Constants.KEY_LAST_AUDITORY      + userId, a)
                .putInt(Constants.KEY_LAST_KINESTETIK    + userId, k).apply();
    }

    public String getLastResult() {
        return prefs.getString(Constants.KEY_LAST_RESULT + getUserId(), "");
    }
    public int getLastVisual() {
        return prefs.getInt(Constants.KEY_LAST_VISUAL + getUserId(), 0);
    }
    public int getLastAuditory() {
        return prefs.getInt(Constants.KEY_LAST_AUDITORY + getUserId(), 0);
    }
    public int getLastKinestetik() {
        return prefs.getInt(Constants.KEY_LAST_KINESTETIK + getUserId(), 0);
    }

    // Restore hasil setelah login — ambil dari DB lalu simpan ke prefs
    public void restoreLastResult(String type, int v, int a, int k, int userId) {
        editor.putString(Constants.KEY_LAST_RESULT     + userId, type)
                .putInt(Constants.KEY_LAST_VISUAL        + userId, v)
                .putInt(Constants.KEY_LAST_AUDITORY      + userId, a)
                .putInt(Constants.KEY_LAST_KINESTETIK    + userId, k).apply();
    }

    public void cacheQuote(String content, String author) {
        editor.putString(Constants.KEY_CACHED_QUOTE, content)
                .putString(Constants.KEY_CACHED_QUOTE_AUTHOR, author).apply();
    }
    public String getCachedQuote()       { return prefs.getString(Constants.KEY_CACHED_QUOTE, ""); }
    public String getCachedQuoteAuthor() { return prefs.getString(Constants.KEY_CACHED_QUOTE_AUTHOR, ""); }

    public void updateName(String n)    { editor.putString(Constants.KEY_USER_NAME, n).apply(); }
    public void updateJurusan(String j) { editor.putString(Constants.KEY_USER_JURUSAN, j).apply(); }

    // Logout: hapus session saja, TIDAK hapus hasil tes (key per userId tetap ada)
    public void clearSession() {
        editor.remove(Constants.KEY_IS_LOGGED_IN)
                .remove(Constants.KEY_USER_NAME)
                .remove(Constants.KEY_USER_EMAIL)
                .remove(Constants.KEY_USER_JURUSAN)
                .remove(Constants.KEY_USER_ID).apply();
    }

    // Hapus semua (untuk uninstall/reset total)
    public void clearAll() { editor.clear().apply(); }
}