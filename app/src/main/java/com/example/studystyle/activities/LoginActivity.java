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
import com.example.studystyle.models.Result;
import com.example.studystyle.models.User;
import com.example.studystyle.utils.PreferenceManager;
import com.example.studystyle.utils.ThemeHelper;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private ProgressBar progressBar;
    private PreferenceManager prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = new PreferenceManager(this);
        ThemeHelper.applyTheme(prefs.isDarkMode());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail     = findViewById(R.id.et_email);
        etPassword  = findViewById(R.id.et_password);
        progressBar = findViewById(R.id.progress_bar_login);

        Button btnLogin       = findViewById(R.id.btn_login);
        TextView tvGoRegister = findViewById(R.id.tv_go_register);

        btnLogin.setOnClickListener(v -> attemptLogin());
        tvGoRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            overridePendingTransition(R.anim.slide_up, R.anim.fade_out);
        });

        // Isi otomatis email jika dikirim dari RegisterActivity
        String prefillEmail = getIntent().getStringExtra("prefill_email");
        if (prefillEmail != null && !prefillEmail.isEmpty()) {
            etEmail.setText(prefillEmail);
            etPassword.requestFocus();
        }
    }

    private void attemptLogin() {
        String email    = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        if (TextUtils.isEmpty(email))    { etEmail.setError("Email tidak boleh kosong"); return; }
        if (TextUtils.isEmpty(password)) { etPassword.setError("Password tidak boleh kosong"); return; }

        progressBar.setVisibility(View.VISIBLE);

        ExecutorManager.getInstance().execute(new BackgroundTask<User>() {
            @Override public User doInBackground() {
                DatabaseHelper db = DatabaseHelper.getInstance(LoginActivity.this);
                if (db.validateLogin(email, password)) return db.getUserByEmail(email);
                return null;
            }
            @Override public void onResult(User user) {
                progressBar.setVisibility(View.GONE);
                if (user != null) {
                    prefs.setLoggedIn(true);
                    prefs.saveUser(user.getName(), user.getEmail(),
                            user.getJurusan() != null ? user.getJurusan() : "",
                            user.getId());

                    // Restore hasil tes terakhir dari DB ke prefs
                    restoreLastResultFromDB(user.getId());
                } else {
                    Toast.makeText(LoginActivity.this,
                            "Email atau password salah", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void restoreLastResultFromDB(int userId) {
        ExecutorManager.getInstance().execute(new BackgroundTask<Result>() {
            @Override public Result doInBackground() {
                return DatabaseHelper.getInstance(LoginActivity.this)
                        .getLatestResult(userId);
            }
            @Override public void onResult(Result result) {
                if (result != null) {
                    prefs.restoreLastResult(
                            result.getResultType(),
                            result.getVisualScore(),
                            result.getAuditoryScore(),
                            result.getKinestetikScore(),
                            userId
                    );
                }
                goToMain();
            }
        });
    }

    private void goToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}