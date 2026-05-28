package com.example.studystyle.utils;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeHelper {

    public static void applyTheme(boolean isDark) {
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
