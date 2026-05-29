package com.example.studystyle.activities;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.studystyle.R;
import com.example.studystyle.utils.PreferenceManager;
import com.example.studystyle.utils.ThemeHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private BottomNavigationView bottomNav;
    private PreferenceManager prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = new PreferenceManager(this);
        ThemeHelper.applyTheme(prefs.isDarkMode());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        bottomNav = findViewById(R.id.bottom_navigation);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            // Warna icon & teks bottom nav
            int[][] states = new int[][]{
                    new int[]{android.R.attr.state_checked},
                    new int[]{-android.R.attr.state_checked}
            };
            int[] colors = new int[]{
                    ContextCompat.getColor(this, R.color.nav_selected),
                    ContextCompat.getColor(this, R.color.nav_unselected)
            };
            ColorStateList csl = new ColorStateList(states, colors);
            bottomNav.setItemIconTintList(csl);
            bottomNav.setItemTextColor(csl);

            // Hubungkan bottom nav dengan navController
            NavigationUI.setupWithNavController(bottomNav, navController);

            // Override listener agar bisa handle tab Hasil
            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.resultFragment) {
                    String lastResult = prefs.getLastResult();
                    if (lastResult == null || lastResult.isEmpty()) {
                        // Belum pernah tes → arahkan ke tab Tes
                        navController.navigate(R.id.testFragment,
                                null,
                                new NavOptions.Builder()
                                        .setPopUpTo(R.id.homeFragment, false)
                                        .build());
                        bottomNav.setSelectedItemId(R.id.testFragment);
                    } else {
                        // Sudah pernah tes → tampilkan hasil dari SharedPreferences
                        navController.navigate(R.id.resultFragment,
                                null,
                                new NavOptions.Builder()
                                        .setPopUpTo(R.id.homeFragment, false)
                                        .setLaunchSingleTop(true)
                                        .build());
                    }
                    return true;
                }

                // Tab lain pakai NavigationUI default
                return NavigationUI.onNavDestinationSelected(item, navController);
            });

            // Bottom nav SELALU tampil di semua halaman
            navController.addOnDestinationChangedListener(
                    (controller, destination, arguments) ->
                            bottomNav.setVisibility(View.VISIBLE));
        }
    }

    public NavController getNavController() {
        return navController;
    }
}