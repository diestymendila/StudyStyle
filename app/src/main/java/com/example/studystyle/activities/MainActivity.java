package com.example.studystyle.activities;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
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

            // Override listener agar semua tab bisa berpindah dari halaman manapun
            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();

                // Jika sudah di halaman yang sama, tidak perlu navigate ulang
                NavDestination current = navController.getCurrentDestination();
                if (current != null && current.getId() == id) return true;

                if (id == R.id.resultFragment) {
                    String lastResult = prefs.getLastResult();
                    if (lastResult == null || lastResult.isEmpty()) {
                        // Belum pernah tes → arahkan ke tab Tes
                        navController.navigate(R.id.testFragment,
                                null,
                                new NavOptions.Builder()
                                        .setPopUpTo(R.id.homeFragment, false)
                                        .setLaunchSingleTop(true)
                                        .build());
                        // Update highlight ke tab Tes
                        bottomNav.post(() ->
                                bottomNav.setSelectedItemId(R.id.testFragment));
                    } else {
                        // Sudah pernah tes → tampilkan hasil
                        navController.navigate(R.id.resultFragment,
                                null,
                                new NavOptions.Builder()
                                        .setPopUpTo(R.id.homeFragment, false)
                                        .setLaunchSingleTop(true)
                                        .build());
                    }
                    return true;
                }

                // Tab Home, Tes, Profile — navigasi normal bersihkan back stack
                navController.navigate(id,
                        null,
                        new NavOptions.Builder()
                                .setPopUpTo(R.id.homeFragment, false)
                                .setLaunchSingleTop(true)
                                .build());
                return true;
            });

            // Sinkronisasi highlight tab saat destination berubah
            // (misalnya saat btn_go_home ditekan dari ResultFragment)
            navController.addOnDestinationChangedListener(
                    (controller, destination, arguments) -> {
                        bottomNav.setVisibility(View.VISIBLE);

                        // Update highlight tab sesuai destination saat ini
                        int destId = destination.getId();
                        if (destId == R.id.homeFragment
                                || destId == R.id.testFragment
                                || destId == R.id.resultFragment
                                || destId == R.id.profileFragment) {
                            if (bottomNav.getSelectedItemId() != destId) {
                                bottomNav.setSelectedItemId(destId);
                            }
                        }
                    });
        }
    }

    public NavController getNavController() {
        return navController;
    }
}