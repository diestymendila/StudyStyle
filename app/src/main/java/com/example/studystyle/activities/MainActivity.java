package com.example.studystyle.activities;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
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

            // Warna icon & teks bottom nav (tema-aware: ikut dark/light theme)
            TypedValue tvSelected = new TypedValue();
            TypedValue tvUnselected = new TypedValue();
            getTheme().resolveAttribute(com.google.android.material.R.attr.colorPrimary, tvSelected, true);
            getTheme().resolveAttribute(R.attr.nav_unselected_color, tvUnselected, true);
            int colorSelected   = tvSelected.data;
            int colorUnselected = tvUnselected.data;

            int[][] states = new int[][]{
                    new int[]{android.R.attr.state_checked},
                    new int[]{-android.R.attr.state_checked}
            };
            int[] colors = new int[]{colorSelected, colorUnselected};
            ColorStateList csl = new ColorStateList(states, colors);
            bottomNav.setItemIconTintList(csl);
            bottomNav.setItemTextColor(csl);

            // Set warna background bottom nav sesuai tema
            TypedValue tvNavBg = new TypedValue();
            getTheme().resolveAttribute(R.attr.nav_bg_color, tvNavBg, true);
            bottomNav.setBackgroundColor(tvNavBg.data);

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
                        bottomNav.post(() ->
                                bottomNav.setSelectedItemId(R.id.testFragment));
                    } else {
                        navController.navigate(R.id.resultFragment,
                                null,
                                new NavOptions.Builder()
                                        .setPopUpTo(R.id.homeFragment, false)
                                        .setLaunchSingleTop(true)
                                        .build());
                    }
                    return true;
                }

                // Home, Tes, Favorit, Profile — navigasi normal
                navController.navigate(id,
                        null,
                        new NavOptions.Builder()
                                .setPopUpTo(R.id.homeFragment, false)
                                .setLaunchSingleTop(true)
                                .build());
                return true;
            });

            // Sinkronisasi highlight tab saat destination berubah
            navController.addOnDestinationChangedListener(
                    (controller, destination, arguments) -> {
                        bottomNav.setVisibility(View.VISIBLE);

                        int destId = destination.getId();
                        if (destId == R.id.homeFragment
                                || destId == R.id.testFragment
                                || destId == R.id.resultFragment
                                || destId == R.id.favoriteFragment
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