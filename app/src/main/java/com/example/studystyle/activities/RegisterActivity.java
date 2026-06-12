package com.example.studystyle.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studystyle.R;
import com.example.studystyle.background.BackgroundTask;
import com.example.studystyle.background.ExecutorManager;
import com.example.studystyle.database.DatabaseHelper;
import com.example.studystyle.models.User;
import com.example.studystyle.utils.PreferenceManager;
import com.example.studystyle.utils.ThemeHelper;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etName, etJurusan, etEmail, etPassword, etConfirmPassword;
    private ProgressBar progressBar;
    private PreferenceManager prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = new PreferenceManager(this);
        ThemeHelper.applyTheme(prefs.isDarkMode());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName            = findViewById(R.id.et_reg_name);
        etJurusan         = findViewById(R.id.et_reg_jurusan);
        etEmail           = findViewById(R.id.et_reg_email);
        etPassword        = findViewById(R.id.et_reg_password);
        etConfirmPassword = findViewById(R.id.et_reg_confirm_password);
        progressBar       = findViewById(R.id.progress_bar_register);

        Button btnRegister = findViewById(R.id.btn_register);
        TextView tvGoLogin = findViewById(R.id.tv_go_login);

        btnRegister.setOnClickListener(v -> attemptRegister());

        tvGoLogin.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }

    private void attemptRegister() {
        String name            = etName.getText() != null ? etName.getText().toString().trim() : "";
        String jurusan         = etJurusan.getText() != null ? etJurusan.getText().toString().trim() : "";
        String email           = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password        = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
        String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";

        if (TextUtils.isEmpty(name))     { etName.setError("Nama tidak boleh kosong"); return; }
        if (TextUtils.isEmpty(email))    { etEmail.setError("Email tidak boleh kosong"); return; }
        if (TextUtils.isEmpty(password)) { etPassword.setError("Password tidak boleh kosong"); return; }
        if (password.length() < 6)      { etPassword.setError("Password minimal 6 karakter"); return; }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Password tidak cocok");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        User newUser = new User(name, email, password, jurusan);

        ExecutorManager.getInstance().execute(new BackgroundTask<Long>() {
            @Override
            public Long doInBackground() {
                DatabaseHelper db = DatabaseHelper.getInstance(RegisterActivity.this);
                if (db.isEmailExists(email)) return -2L;
                return db.insertUser(newUser);
            }

            @Override
            public void onResult(Long id) {
                progressBar.setVisibility(View.GONE);
                if (id == -2L) {
                    Toast.makeText(RegisterActivity.this,
                            "Email sudah terdaftar", Toast.LENGTH_SHORT).show();
                } else if (id > 0) {
                    // Tidak auto-login — kembali ke LoginActivity
                    Toast.makeText(RegisterActivity.this,
                            "Registrasi berhasil! Silakan login dengan akun Anda 🎉",
                            Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);

                    intent.putExtra("prefill_email", email);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this,
                            "Registrasi gagal, coba lagi", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}