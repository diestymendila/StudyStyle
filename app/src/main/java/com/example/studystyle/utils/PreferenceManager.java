package com.example.studystyle.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public PreferenceManager(Context context) {
        prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void setLoggedIn(boolean loggedIn) {
        editor.putBoolean(Constants.KEY_IS_LOGGED_IN, loggedIn).apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(Constants.KEY_IS_LOGGED_IN, false);
    }

    public void saveUser(String name, String email, String jurusan, int userId) {
        editor.putString(Constants.KEY_USER_NAME, name);
        editor.putString(Constants.KEY_USER_EMAIL, email);
        editor.putString(Constants.KEY_USER_JURUSAN, jurusan);
        editor.putInt(Constants.KEY_USER_ID, userId);
        editor.apply();
    }

    public String getUserName() {
        return prefs.getString(Constants.KEY_USER_NAME, "");
    }

    public String getUserEmail() {
        return prefs.getString(Constants.KEY_USER_EMAIL, "");
    }

    public String getUserJurusan() {
        return prefs.getString(Constants.KEY_USER_JURUSAN, "");
    }

    public int getUserId() {
        return prefs.getInt(Constants.KEY_USER_ID, -1);
    }

    public void setDarkMode(boolean isDark) {
        editor.putBoolean(Constants.KEY_DARK_MODE, isDark).apply();
    }

    public boolean isDarkMode() {
        return prefs.getBoolean(Constants.KEY_DARK_MODE, false);
    }

    public void saveLastResult(String resultType, int visual, int auditory, int kinestetik) {
        editor.putString(Constants.KEY_LAST_RESULT, resultType);
        editor.putInt(Constants.KEY_LAST_VISUAL, visual);
        editor.putInt(Constants.KEY_LAST_AUDITORY, auditory);
        editor.putInt(Constants.KEY_LAST_KINESTETIK, kinestetik);
        editor.apply();
    }

    public String getLastResult() {
        return prefs.getString(Constants.KEY_LAST_RESULT, "");
    }

    public int getLastVisual() { return prefs.getInt(Constants.KEY_LAST_VISUAL, 0); }
    public int getLastAuditory() { return prefs.getInt(Constants.KEY_LAST_AUDITORY, 0); }
    public int getLastKinestetik() { return prefs.getInt(Constants.KEY_LAST_KINESTETIK, 0); }

    public void clearAll() {
        editor.clear().apply();
    }

    public void updateName(String name) {
        editor.putString(Constants.KEY_USER_NAME, name).apply();
    }

    public void updateJurusan(String jurusan) {
        editor.putString(Constants.KEY_USER_JURUSAN, jurusan).apply();
    }
}
