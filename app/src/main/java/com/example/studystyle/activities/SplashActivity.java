package com.example.studystyle.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

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

        ImageView ivLogo   = findViewById(R.id.iv_logo);
        TextView tvAppName = findViewById(R.id.tv_app_name);
        TextView tvSlogan  = findViewById(R.id.tv_slogan);

        // Logo: scale + fade
        AnimationSet logoAnim = new AnimationSet(true);
        ScaleAnimation scale = new ScaleAnimation(
                0.3f, 1f, 0.3f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(700);
        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(700);
        logoAnim.addAnimation(scale);
        logoAnim.addAnimation(fadeIn);
        logoAnim.setFillAfter(true);
        ivLogo.startAnimation(logoAnim);

        // App name fade
        AlphaAnimation textAnim = new AlphaAnimation(0f, 1f);
        textAnim.setDuration(600);
        textAnim.setStartOffset(500);
        textAnim.setFillAfter(true);
        tvAppName.startAnimation(textAnim);

        // Slogan fade
        AlphaAnimation sloganAnim = new AlphaAnimation(0f, 1f);
        sloganAnim.setDuration(600);
        sloganAnim.setStartOffset(800);
        sloganAnim.setFillAfter(true);
        tvSlogan.startAnimation(sloganAnim);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent;
            if (prefs.isLoggedIn()) {
                // Sudah login → langsung ke MainActivity
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                // Belum login → ke LoginActivity
                intent = new Intent(SplashActivity.this, AuthActivity.class);
            }
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }, SPLASH_DURATION);
    }
}
