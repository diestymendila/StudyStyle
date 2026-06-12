package com.example.studystyle.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.studystyle.utils.PreferenceManager;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookDetailActivity extends AppCompatActivity {

    public static final String EXTRA_BOOK_JSON  = "extra_book_json";
    public static final String EXTRA_BOOK_KEY   = "extra_book_key";
    public static final String EXTRA_NEW_RATING = "extra_new_rating";

    private int currentRating = 0;
    private TextView[] stars;
    private String bookKey = "";
    private BookItem book;
    private PreferenceManager prefs;


    private Button btnFavoriteBook;
    private boolean isFavorited = false;


    private final List<String> reviewList = new ArrayList<>();
    private LinearLayout layoutReviewsContainer;
    private TextView tvReviewsLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        String bookJson = getIntent().getStringExtra(EXTRA_BOOK_JSON);
        if (bookJson == null) { finish(); return; }
        book = new Gson().fromJson(bookJson, BookItem.class);
        bookKey = book.getKey();

        ImageButton btnBack          = findViewById(R.id.btn_back);
        ImageView   ivCover          = findViewById(R.id.iv_detail_cover);
        TextView    tvTitle          = findViewById(R.id.tv_detail_title);
        TextView    tvAuthor         = findViewById(R.id.tv_detail_author);
        TextView    tvYear           = findViewById(R.id.tv_detail_year);
        TextView    tvGenre          = findViewById(R.id.tv_detail_genre);
        ProgressBar progressSynopsis = findViewById(R.id.progress_synopsis);
        TextView    tvSynopsis       = findViewById(R.id.tv_detail_synopsis);
        TextView    tvNoSynopsis     = findViewById(R.id.tv_detail_no_synopsis);
        layoutReviewsContainer       = findViewById(R.id.layout_reviews_container);
        tvReviewsLabel               = findViewById(R.id.tv_reviews_label);
        btnFavoriteBook              = findViewById(R.id.btn_favorite_book);

        stars = new TextView[]{
                findViewById(R.id.star_1),
                findViewById(R.id.star_2),
                findViewById(R.id.star_3),
                findViewById(R.id.star_4),
                findViewById(R.id.star_5)
        };

        prefs = new PreferenceManager(this);
        String userName = prefs.getUserName();
        if (userName == null || userName.trim().isEmpty()) userName = "Pengguna";

        // Isi data buku
        tvTitle.setText(book.getTitle());
        tvAuthor.setText("oleh " + book.getAuthor());
        String year = book.getYear();
        tvYear.setText(year.isEmpty() ? "" : "Tahun: " + year);
        tvGenre.setText(book.getGenre().isEmpty() ? "Education" : book.getGenre());

        if (!book.getCover().isEmpty()) {
            Glide.with(this)
                    .load(book.getCover())
                    .placeholder(R.drawable.ic_book_placeholder)
                    .error(R.drawable.ic_book_placeholder)
                    .centerCrop()
                    .into(ivCover);
        }


        String cached = book.getSynopsis();
        if (cached != null && !cached.isEmpty()) {
            progressSynopsis.setVisibility(View.GONE);
            tvSynopsis.setText(cached);
            tvSynopsis.setVisibility(View.VISIBLE);
        } else if (!bookKey.isEmpty()) {
            progressSynopsis.setVisibility(View.VISIBLE);
            ApiClient.getBookApiService().getBookDetail(bookKey)
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


        isFavorited = prefs.isBookFavorited(bookKey);
        updateFavoriteButton();


        btnFavoriteBook.setOnClickListener(v -> {
            if (isFavorited) {
                prefs.removeFavoriteBook(bookKey);
                isFavorited = false;
                Toast.makeText(this, "Dihapus dari favorit", Toast.LENGTH_SHORT).show();
            } else {
                prefs.addFavoriteBook(book);
                isFavorited = true;
                Toast.makeText(this, "Ditambahkan ke favorit ❤", Toast.LENGTH_SHORT).show();
            }
            updateFavoriteButton();
        });


        int savedRating = prefs.getBookRating(bookKey);
        if (savedRating > 0) {
            applyStarColors(savedRating);
            currentRating = savedRating;
            TextView tvHint = findViewById(R.id.tv_rating_hint);
            tvHint.setText(savedRating + " dari 5 bintang");
        }


        btnBack.setOnClickListener(v -> finishWithResult());


        for (int i = 0; i < stars.length; i++) {
            final int starIndex = i + 1;
            stars[i].setOnClickListener(v -> {
                currentRating = starIndex;
                applyStarColors(starIndex);
                prefs.saveBookRating(bookKey, starIndex);
                TextView tvHint = findViewById(R.id.tv_rating_hint);
                tvHint.setText(starIndex + " dari 5 bintang");
                Toast.makeText(this, "Rating " + starIndex + "/5 disimpan!", Toast.LENGTH_SHORT).show();
            });
        }


        final String finalUserName = userName;
        Button btnSubmitReview = findViewById(R.id.btn_submit_review);
        btnSubmitReview.setOnClickListener(v -> {
            com.google.android.material.textfield.TextInputEditText etReview =
                    findViewById(R.id.et_review);
            String review = etReview.getText() != null ? etReview.getText().toString().trim() : "";
            if (review.isEmpty()) {
                Toast.makeText(this, "Tulis reviewmu terlebih dahulu.", Toast.LENGTH_SHORT).show();
            } else {
                reviewList.add(review);
                addReviewCard(finalUserName, review);
                etReview.setText("");
                Toast.makeText(this, "Review berhasil disimpan!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishWithResult();
    }

    private void updateFavoriteButton() {
        if (isFavorited) {
            btnFavoriteBook.setText("❤  Tersimpan di Favorit");
            btnFavoriteBook.setBackgroundTintList(
                    ContextCompat.getColorStateList(this, R.color.brand_primary));
            btnFavoriteBook.setTextColor(
                    ContextCompat.getColor(this, R.color.white));
        } else {
            btnFavoriteBook.setText("❤  Tambahkan ke Favorit");

            btnFavoriteBook.setBackgroundTintList(
                    ContextCompat.getColorStateList(this, android.R.color.transparent));
            btnFavoriteBook.setTextColor(
                    ContextCompat.getColor(this, R.color.brand_primary));
        }
    }

    private void finishWithResult() {
        Intent result = new Intent();
        result.putExtra(EXTRA_BOOK_KEY, bookKey);
        result.putExtra(EXTRA_NEW_RATING, currentRating);
        setResult(Activity.RESULT_OK, result);
        finish();
    }

    private void applyStarColors(int rating) {
        int activeColor   = ContextCompat.getColor(this, R.color.warning);
        int inactiveColor = ContextCompat.getColor(this, R.color.text_secondary);
        for (int i = 0; i < stars.length; i++) {
            stars[i].setTextColor(i < rating ? activeColor : inactiveColor);
        }
    }

    private void addReviewCard(String author, String reviewText) {
        if (reviewList.size() == 1) {
            tvReviewsLabel.setVisibility(View.VISIBLE);
        }

        androidx.cardview.widget.CardView cardView =
                new androidx.cardview.widget.CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, dpToPx(10));
        cardView.setLayoutParams(cardParams);
        cardView.setRadius(dpToPx(12));
        cardView.setCardElevation(dpToPx(2));
        cardView.setCardBackgroundColor(
                ContextCompat.getColor(this, R.color.brand_accent));

        LinearLayout outer = new LinearLayout(this);
        outer.setOrientation(LinearLayout.HORIZONTAL);
        outer.setGravity(android.view.Gravity.CENTER_VERTICAL);
        int pad = dpToPx(14);
        outer.setPadding(pad, pad, dpToPx(8), pad);

        LinearLayout inner = new LinearLayout(this);
        inner.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams innerParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        inner.setLayoutParams(innerParams);

        TextView tvAuthorName = new TextView(this);
        tvAuthorName.setText("● " + author);
        tvAuthorName.setTextSize(13f);
        tvAuthorName.setTextColor(ContextCompat.getColor(this, R.color.brand_primary));
        tvAuthorName.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView tvReviewContent = new TextView(this);
        tvReviewContent.setText(reviewText);
        tvReviewContent.setTextSize(13f);
        tvReviewContent.setLineSpacing(0, 1.5f);
        LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        contentParams.setMargins(0, dpToPx(4), 0, 0);
        tvReviewContent.setLayoutParams(contentParams);
        tvReviewContent.setTextColor(ContextCompat.getColor(this, R.color.text_primary));

        inner.addView(tvAuthorName);
        inner.addView(tvReviewContent);

        TextView btnDelete = new TextView(this);
        btnDelete.setText("🗑");
        btnDelete.setTextSize(18f);
        LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        deleteParams.setMarginStart(dpToPx(8));
        btnDelete.setLayoutParams(deleteParams);
        btnDelete.setPadding(dpToPx(6), dpToPx(6), dpToPx(6), dpToPx(6));
        android.util.TypedValue ripple = new android.util.TypedValue();
        getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, ripple, true);
        btnDelete.setBackgroundResource(ripple.resourceId);

        btnDelete.setOnClickListener(v -> {
            reviewList.remove(reviewText);
            layoutReviewsContainer.removeView(cardView);
            if (reviewList.isEmpty()) {
                tvReviewsLabel.setVisibility(View.GONE);
            }
        });

        outer.addView(inner);
        outer.addView(btnDelete);
        cardView.addView(outer);
        layoutReviewsContainer.addView(cardView);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}