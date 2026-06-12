package com.example.studystyle.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studystyle.R;
import com.example.studystyle.utils.PreferenceManager;
import com.example.studystyle.utils.ThemeHelper;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DURATION = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PreferenceManager prefs = new PreferenceManager(this);
        ThemeHelper.applyTheme(prefs.isDarkMode());

        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);


        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent;
            if (prefs.isLoggedIn()) {
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, AuthActivity.class);
            }
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }, SPLASH_DURATION);
    }
}