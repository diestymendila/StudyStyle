package com.example.studystyle.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class ProfileFragment extends Fragment {

    private PreferenceManager prefs;
    private TextView tvName, tvEmail, tvJurusan, tvDominantStyle;
    private ImageView ivProfileAvatar;
    private SwitchMaterial switchDarkMode;

    // Untuk dialog edit
    private ImageView ivDialogAvatar;
    private Uri selectedPhotoUri = null;
    private boolean photoRemoved = false;

    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null && ivDialogAvatar != null) {
                    selectedPhotoUri = uri;
                    photoRemoved = false;
                    ivDialogAvatar.setImageURI(uri);
                }
            });

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
        ivProfileAvatar = view.findViewById(R.id.iv_profile_avatar);
        switchDarkMode  = view.findViewById(R.id.switch_dark_mode);
        Button btnEdit   = view.findViewById(R.id.btn_edit_profile);
        Button btnLogout = view.findViewById(R.id.btn_logout);

        loadProfile();

        switchDarkMode.setChecked(prefs.isDarkMode());
        switchDarkMode.setOnCheckedChangeListener((btn, isChecked) -> {
            prefs.setDarkMode(isChecked);
            ThemeHelper.applyTheme(isChecked);
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

        // Load foto profil jika ada
        loadAvatarInto(ivProfileAvatar);
    }

    private void loadAvatarInto(ImageView imageView) {
        if (imageView == null) return;
        String photoPath = prefs.getProfilePhotoPath();
        if (!TextUtils.isEmpty(photoPath)) {
            File file = new File(photoPath);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                    return;
                }
            }
        }
        // Fallback ke placeholder
        imageView.setImageResource(R.drawable.ic_person_placeholder);
    }

    private void showEditDialog() {
        selectedPhotoUri = null;
        photoRemoved = false;

        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_edit_profile, null);

        TextInputEditText etName    = dialogView.findViewById(R.id.et_dialog_name);
        TextInputEditText etJurusan = dialogView.findViewById(R.id.et_dialog_jurusan);
        ivDialogAvatar              = dialogView.findViewById(R.id.iv_dialog_avatar);
        Button btnChangePhoto       = dialogView.findViewById(R.id.btn_change_photo);
        Button btnRemovePhoto       = dialogView.findViewById(R.id.btn_remove_photo);

        etName.setText(prefs.getUserName());
        etJurusan.setText(prefs.getUserJurusan());

        // Tampilkan foto saat ini di dialog
        loadAvatarInto(ivDialogAvatar);

        btnChangePhoto.setOnClickListener(v ->
                pickImageLauncher.launch("image/*"));

        btnRemovePhoto.setOnClickListener(v -> {
            photoRemoved = true;
            selectedPhotoUri = null;
            ivDialogAvatar.setImageResource(R.drawable.ic_person_placeholder);
        });

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
                    saveProfile(newName, newJurusan);
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void saveProfile(String newName, String newJurusan) {
        ExecutorManager.getInstance().execute(new BackgroundTask<Boolean>() {
            @Override public Boolean doInBackground() {
                // Simpan foto jika dipilih
                if (selectedPhotoUri != null) {
                    String path = savePhotoToInternal(selectedPhotoUri);
                    if (path != null) prefs.setProfilePhotoPath(path);
                } else if (photoRemoved) {
                    // Hapus file lama jika ada
                    String oldPath = prefs.getProfilePhotoPath();
                    if (!TextUtils.isEmpty(oldPath)) {
                        new File(oldPath).delete();
                    }
                    prefs.setProfilePhotoPath("");
                }
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
    }

    private String savePhotoToInternal(Uri uri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            // Compress bitmap agar tidak boros storage
            Bitmap original = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            // Resize ke max 400x400
            int maxSize = 400;
            int width = original.getWidth();
            int height = original.getHeight();
            float scale = Math.min((float) maxSize / width, (float) maxSize / height);
            Bitmap resized = Bitmap.createScaledBitmap(original,
                    (int)(width * scale), (int)(height * scale), true);

            File dir = requireContext().getFilesDir();
            File file = new File(dir, "profile_photo_" + prefs.getUserId() + ".jpg");
            FileOutputStream fos = new FileOutputStream(file);
            resized.compress(Bitmap.CompressFormat.JPEG, 85, fos);
            fos.close();

            return file.getAbsolutePath();
        } catch (Exception e) {
            return null;
        }
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Keluar")
                .setMessage("Yakin ingin keluar dari akun?")
                .setPositiveButton("Keluar", (dialog, which) -> {
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