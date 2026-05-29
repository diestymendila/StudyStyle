package com.example.studystyle.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.studystyle.R;
import com.example.studystyle.activities.LoginActivity;
import com.example.studystyle.activities.MainActivity;
import com.example.studystyle.background.BackgroundTask;
import com.example.studystyle.background.ExecutorManager;
import com.example.studystyle.database.DatabaseHelper;
import com.example.studystyle.utils.PreferenceManager;
import com.example.studystyle.utils.ThemeHelper;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

public class ProfileFragment extends Fragment {

    private PreferenceManager prefs;
    private TextView tvName, tvEmail, tvJurusan, tvDominantStyle;
    private SwitchMaterial switchDarkMode;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = new PreferenceManager(requireContext());

        tvName          = view.findViewById(R.id.tv_profile_name);
        tvEmail         = view.findViewById(R.id.tv_profile_email);
        tvJurusan       = view.findViewById(R.id.tv_profile_jurusan);
        tvDominantStyle = view.findViewById(R.id.tv_dominant_style);
        switchDarkMode  = view.findViewById(R.id.switch_dark_mode);
        Button btnEdit   = view.findViewById(R.id.btn_edit_profile);
        Button btnLogout = view.findViewById(R.id.btn_logout);

        loadProfile();

        // Dark mode: simpan preferensi lalu restart MainActivity agar benar-benar berubah
        switchDarkMode.setChecked(prefs.isDarkMode());
        switchDarkMode.setOnCheckedChangeListener((btn, isChecked) -> {
            prefs.setDarkMode(isChecked);
            ThemeHelper.applyTheme(isChecked);
            // Restart activity agar theme diterapkan ke semua view
            Intent intent = new Intent(requireActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        btnEdit.setOnClickListener(v -> showEditDialog());
        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    @Override public void onResume() { super.onResume(); loadProfile(); }

    private void loadProfile() {
        tvName.setText(prefs.getUserName());
        tvEmail.setText(prefs.getUserEmail());
        String jurusan = prefs.getUserJurusan();
        tvJurusan.setText(TextUtils.isEmpty(jurusan) ? "Belum diisi" : jurusan);
        String style = prefs.getLastResult();
        tvDominantStyle.setText(TextUtils.isEmpty(style) ? "Belum ada hasil tes" : style + " Learner");
    }

    private void showEditDialog() {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_edit_profile, null);
        TextInputEditText etName    = dialogView.findViewById(R.id.et_dialog_name);
        TextInputEditText etJurusan = dialogView.findViewById(R.id.et_dialog_jurusan);
        etName.setText(prefs.getUserName());
        etJurusan.setText(prefs.getUserJurusan());

        new AlertDialog.Builder(requireContext())
                .setTitle("Edit Profil")
                .setView(dialogView)
                .setPositiveButton("Simpan", (dialog, which) -> {
                    String newName    = etName.getText() != null ? etName.getText().toString().trim() : "";
                    String newJurusan = etJurusan.getText() != null ? etJurusan.getText().toString().trim() : "";
                    if (TextUtils.isEmpty(newName)) {
                        Toast.makeText(requireContext(), "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ExecutorManager.getInstance().execute(new BackgroundTask<Boolean>() {
                        @Override public Boolean doInBackground() {
                            return DatabaseHelper.getInstance(requireContext())
                                    .updateUser(prefs.getUserId(), newName, newJurusan);
                        }
                        @Override public void onResult(Boolean success) {
                            if (!isAdded()) return;
                            if (Boolean.TRUE.equals(success)) {
                                prefs.updateName(newName);
                                prefs.updateJurusan(newJurusan);
                                loadProfile();
                                Toast.makeText(requireContext(), "Profil diperbarui!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Keluar")
                .setMessage("Yakin ingin keluar dari akun?")
                .setPositiveButton("Keluar", (dialog, which) -> {
                    // clearSession() - tidak hapus hasil tes per userId
                    prefs.clearSession();
                    Intent intent = new Intent(requireActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .setNegativeButton("Batal", null)
                .show();
    }
}