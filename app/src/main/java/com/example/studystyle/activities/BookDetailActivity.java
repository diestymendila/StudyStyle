package com.example.studystyle.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.studystyle.R;
import com.example.studystyle.api.ApiClient;
import com.example.studystyle.models.BookDetail;
import com.example.studystyle.models.BookItem;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookDetailActivity extends AppCompatActivity {

    public static final String EXTRA_BOOK_JSON = "extra_book_json";

    private int currentRating = 0;
    private TextView[] stars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        // Parse BookItem dari Intent
        String bookJson = getIntent().getStringExtra(EXTRA_BOOK_JSON);
        if (bookJson == null) { finish(); return; }
        BookItem book = new Gson().fromJson(bookJson, BookItem.class);

        // View references
        ImageButton btnBack          = findViewById(R.id.btn_back);
        ImageView ivCover            = findViewById(R.id.iv_detail_cover);
        TextView tvTitle             = findViewById(R.id.tv_detail_title);
        TextView tvAuthor            = findViewById(R.id.tv_detail_author);
        TextView tvYear              = findViewById(R.id.tv_detail_year);
        TextView tvGenre             = findViewById(R.id.tv_detail_genre);
        ProgressBar progressSynopsis = findViewById(R.id.progress_synopsis);
        TextView tvSynopsis          = findViewById(R.id.tv_detail_synopsis);
        TextView tvNoSynopsis        = findViewById(R.id.tv_detail_no_synopsis);
        Button btnFavorite           = findViewById(R.id.btn_favorite);
        Button btnSubmitReview       = findViewById(R.id.btn_submit_review);

        stars = new TextView[]{
                findViewById(R.id.star_1),
                findViewById(R.id.star_2),
                findViewById(R.id.star_3),
                findViewById(R.id.star_4),
                findViewById(R.id.star_5)
        };

        // Isi data
        tvTitle.setText(book.getTitle());
        tvAuthor.setText("oleh " + book.getAuthor());
        tvYear.setText(book.getYear().isEmpty() ? "" : "Tahun: " + book.getYear());
        tvGenre.setText(book.getGenre().isEmpty() ? "Education" : book.getGenre());

        if (!book.getCover().isEmpty()) {
            Glide.with(this)
                    .load(book.getCover())
                    .placeholder(R.drawable.ic_book_placeholder)
                    .error(R.drawable.ic_book_placeholder)
                    .centerCrop()
                    .into(ivCover);
        }

        // Sinopsis — pakai cache atau fetch
        String cached = book.getSynopsis();
        if (cached != null && !cached.isEmpty()) {
            progressSynopsis.setVisibility(View.GONE);
            tvSynopsis.setText(cached);
            tvSynopsis.setVisibility(View.VISIBLE);
        } else if (!book.getKey().isEmpty()) {
            progressSynopsis.setVisibility(View.VISIBLE);
            ApiClient.getBookApiService().getBookDetail(book.getKey())
                    .enqueue(new Callback<BookDetail>() {
                        @Override
                        public void onResponse(@NonNull Call<BookDetail> call,
                                               @NonNull Response<BookDetail> response) {
                            progressSynopsis.setVisibility(View.GONE);
                            if (response.isSuccessful() && response.body() != null) {
                                String desc = response.body().getDescription();
                                if (desc != null && !desc.isEmpty()) {
                                    tvSynopsis.setText(desc);
                                    tvSynopsis.setVisibility(View.VISIBLE);
                                    return;
                                }
                            }
                            tvNoSynopsis.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onFailure(@NonNull Call<BookDetail> call,
                                              @NonNull Throwable t) {
                            progressSynopsis.setVisibility(View.GONE);
                            tvNoSynopsis.setText("Gagal memuat sinopsis.");
                            tvNoSynopsis.setVisibility(View.VISIBLE);
                        }
                    });
        } else {
            progressSynopsis.setVisibility(View.GONE);
            tvNoSynopsis.setVisibility(View.VISIBLE);
        }

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Favorit
        btnFavorite.setOnClickListener(v -> {
            btnFavorite.setText("♥  Ditambahkan ke Favorit");
            Toast.makeText(this, "\"" + book.getTitle() + "\" ditambahkan ke favorit!", Toast.LENGTH_SHORT).show();
        });

        // Rating bintang
        for (int i = 0; i < stars.length; i++) {
            final int starIndex = i + 1;
            stars[i].setOnClickListener(v -> setRating(starIndex));
        }

        // Submit review
        btnSubmitReview.setOnClickListener(v -> {
            com.google.android.material.textfield.TextInputEditText etReview =
                    findViewById(R.id.et_review);
            String review = etReview.getText() != null ? etReview.getText().toString().trim() : "";
            if (review.isEmpty()) {
                Toast.makeText(this, "Tulis reviewmu terlebih dahulu.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Review berhasil dikirim!", Toast.LENGTH_SHORT).show();
                etReview.setText("");
            }
        });
    }

    private void setRating(int rating) {
        currentRating = rating;
        int activeColor  = ContextCompat.getColor(this, R.color.warning);
        int inactiveColor = ContextCompat.getColor(this, R.color.text_secondary);
        for (int i = 0; i < stars.length; i++) {
            stars[i].setTextColor(i < rating ? activeColor : inactiveColor);
        }
        TextView tvHint = findViewById(R.id.tv_rating_hint);
        tvHint.setText(rating + " dari 5 bintang");
    }
}