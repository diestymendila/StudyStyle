package com.example.studystyle.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studystyle.R;
import com.example.studystyle.adapters.BookAdapter;
import com.example.studystyle.adapters.HistoryAdapter;
import com.example.studystyle.api.ApiClient;
import com.example.studystyle.background.BackgroundTask;
import com.example.studystyle.background.ExecutorManager;
import com.example.studystyle.database.DatabaseHelper;
import com.example.studystyle.models.BookDetail;
import com.example.studystyle.models.BookItem;
import com.example.studystyle.models.BookSearchResponse;
import com.example.studystyle.models.Result;
import com.example.studystyle.utils.Constants;
import com.example.studystyle.utils.NetworkUtil;
import com.example.studystyle.utils.PreferenceManager;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultFragment extends Fragment {

    private PreferenceManager prefs;
    private TextView tvResultTitle, tvResultDesc, tvKelebihan, tvKekurangan;
    private TextView tvVisualPct, tvAuditoryPct, tvKinestetikPct;
    private PieChart pieChart;
    private HistoryAdapter historyAdapter;
    private BookAdapter bookAdapter;
    private LinearLayout layoutNoHistory;
    private ProgressBar progressBooks;
    private TextView tvBooksOffline;
    private RecyclerView rvBooks;
    private String currentResultType = "";

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getContext() == null) return;

        prefs = new PreferenceManager(requireContext());

        tvResultTitle   = view.findViewById(R.id.tv_result_title);
        tvResultDesc    = view.findViewById(R.id.tv_result_desc);
        tvKelebihan     = view.findViewById(R.id.tv_kelebihan);
        tvKekurangan    = view.findViewById(R.id.tv_kekurangan);
        tvVisualPct     = view.findViewById(R.id.tv_visual_pct);
        tvAuditoryPct   = view.findViewById(R.id.tv_auditory_pct);
        tvKinestetikPct = view.findViewById(R.id.tv_kinestetik_pct);
        pieChart        = view.findViewById(R.id.pie_chart);
        layoutNoHistory = view.findViewById(R.id.layout_no_history);
        progressBooks   = view.findViewById(R.id.progress_books);
        tvBooksOffline  = view.findViewById(R.id.tv_books_offline);
        rvBooks         = view.findViewById(R.id.rv_books);

        RecyclerView rvHistory = view.findViewById(R.id.rv_history);
        Button btnRetake = view.findViewById(R.id.btn_retake_test);
        Button btnHome   = view.findViewById(R.id.btn_go_home);

        historyAdapter = new HistoryAdapter(requireContext(), new ArrayList<>());
        rvHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvHistory.setAdapter(historyAdapter);
        rvHistory.setNestedScrollingEnabled(false);

        bookAdapter = new BookAdapter(requireContext(), new ArrayList<>());
        rvBooks.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvBooks.setAdapter(bookAdapter);
        rvBooks.setNestedScrollingEnabled(false);

        // ✅ Set listener bottom sheet
        bookAdapter.setOnBookClickListener(this::showBookBottomSheet);

        Bundle args = getArguments();
        String type;
        int visual, auditory, kines;

        if (args != null && !args.getString(Constants.INTENT_RESULT_TYPE, "").isEmpty()) {
            type     = args.getString(Constants.INTENT_RESULT_TYPE, "");
            visual   = args.getInt(Constants.INTENT_VISUAL_SCORE, 0);
            auditory = args.getInt(Constants.INTENT_AUDITORY_SCORE, 0);
            kines    = args.getInt(Constants.INTENT_KINESTETIK_SCORE, 0);
            prefs.saveLastResult(type, visual, auditory, kines);
            saveResultToDB(type, visual, auditory, kines);
        } else {
            type     = prefs.getLastResult();
            visual   = prefs.getLastVisual();
            auditory = prefs.getLastAuditory();
            kines    = prefs.getLastKinestetik();
        }

        if (!type.isEmpty()) {
            currentResultType = type;
            displayResult(type, visual, auditory, kines);
            loadBooks(type);
        } else {
            showEmptyState(view);
        }

        loadHistory();

        btnRetake.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_result_to_test));
        btnHome.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_result_to_home));
    }

    // ✅ Tampilkan bottom sheet detail buku
    private void showBookBottomSheet(BookItem book) {
        if (!isAdded() || getContext() == null) return;

        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View sheetView = LayoutInflater.from(requireContext())
                .inflate(R.layout.bottom_sheet_book, null);
        dialog.setContentView(sheetView);

        ImageView ivCover         = sheetView.findViewById(R.id.iv_book_cover);
        TextView tvTitle          = sheetView.findViewById(R.id.tv_sheet_title);
        TextView tvAuthor         = sheetView.findViewById(R.id.tv_sheet_author);
        TextView tvYear           = sheetView.findViewById(R.id.tv_sheet_year);
        ProgressBar progressSheet = sheetView.findViewById(R.id.progress_sheet);
        TextView tvLabel          = sheetView.findViewById(R.id.tv_sheet_sinopsis_label);
        TextView tvSinopsis       = sheetView.findViewById(R.id.tv_sheet_sinopsis);
        TextView tvNoSinopsis     = sheetView.findViewById(R.id.tv_sheet_no_sinopsis);
        Button btnOpenWeb         = sheetView.findViewById(R.id.btn_open_web);

        // Isi data dasar yang sudah ada
        tvTitle.setText(book.getTitle());
        tvAuthor.setText(book.getAuthor());
        tvYear.setText(book.getFirstPublishYear() != null
                ? "Terbit: " + book.getFirstPublishYear() : "");

        // Tombol buka web sebagai fallback
        btnOpenWeb.setOnClickListener(v -> {
            String url = book.getBookUrl();
            if (!url.isEmpty()) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });

        // Ambil work ID dari key (format: /works/OL123W)
        String key = book.getKey();
        if (key != null && key.startsWith("/works/")) {
            String workId = key.replace("/works/", "");
            progressSheet.setVisibility(View.VISIBLE);

            // Fetch detail + sinopsis
            ApiClient.getBookApiService().getBookDetail(workId)
                    .enqueue(new Callback<BookDetail>() {
                        @Override
                        public void onResponse(@NonNull Call<BookDetail> call,
                                               @NonNull Response<BookDetail> response) {
                            if (!isAdded()) return;
                            progressSheet.setVisibility(View.GONE);

                            if (response.isSuccessful() && response.body() != null) {
                                BookDetail detail = response.body();

                                // Tampilkan sinopsis
                                String sinopsis = detail.getDescription();
                                if (sinopsis != null && !sinopsis.isEmpty()) {
                                    tvLabel.setVisibility(View.VISIBLE);
                                    tvSinopsis.setVisibility(View.VISIBLE);
                                    tvSinopsis.setText(sinopsis);
                                } else {
                                    tvNoSinopsis.setVisibility(View.VISIBLE);
                                }

                                // Load cover di background thread
                                String coverUrl = detail.getCoverUrl();
                                if (coverUrl != null) {
                                    ExecutorManager.getInstance().execute(
                                            new BackgroundTask<Bitmap>() {
                                                @Override public Bitmap doInBackground() {
                                                    return downloadBitmap(coverUrl);
                                                }
                                                @Override public void onResult(Bitmap bitmap) {
                                                    if (bitmap != null && isAdded()) {
                                                        ivCover.setImageBitmap(bitmap);
                                                    }
                                                }
                                            });
                                }
                            } else {
                                tvNoSinopsis.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<BookDetail> call,
                                              @NonNull Throwable t) {
                            if (!isAdded()) return;
                            progressSheet.setVisibility(View.GONE);
                            tvNoSinopsis.setVisibility(View.VISIBLE);
                        }
                    });
        } else {
            tvNoSinopsis.setVisibility(View.VISIBLE);
        }

        dialog.show();
    }

    // ✅ Download cover buku
    private Bitmap downloadBitmap(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.connect();
            InputStream is = conn.getInputStream();
            return BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            return null;
        }
    }

    private void showEmptyState(View view) {
        view.findViewById(R.id.card_chart).setVisibility(View.GONE);
        view.findViewById(R.id.card_desc).setVisibility(View.GONE);
        view.findViewById(R.id.tv_header_label).setVisibility(View.GONE);
        view.findViewById(R.id.tv_books_label).setVisibility(View.GONE);
        tvResultTitle.setText("Belum Ada Hasil Tes");
        tvResultTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
    }

    private void displayResult(String type, int visual, int auditory, int kines) {
        int total = visual + auditory + kines;
        if (total == 0) return;

        float vPct = (visual * 100f) / total;
        float aPct = (auditory * 100f) / total;
        float kPct = (kines * 100f) / total;

        tvVisualPct.setText(String.format(Locale.getDefault(), "%.0f%%", vPct));
        tvAuditoryPct.setText(String.format(Locale.getDefault(), "%.0f%%", aPct));
        tvKinestetikPct.setText(String.format(Locale.getDefault(), "%.0f%%", kPct));

        int colorRes;
        switch (type) {
            case Constants.STYLE_VISUAL:
                colorRes = R.color.color_visual;
                tvResultTitle.setText("🎨 Visual Learner");
                tvResultDesc.setText("Kamu belajar paling efektif melalui gambar, diagram, warna, dan representasi visual. Otak kamu sangat aktif memproses informasi secara spasial.");
                tvKelebihan.setText("• Mudah memahami peta, diagram, dan grafik\n• Imajinasi dan kreativitas tinggi\n• Ingatan visual yang kuat\n• Baik dalam desain dan seni visual");
                tvKekurangan.setText("• Sulit belajar dari penjelasan verbal saja\n• Bisa terganggu lingkungan visual berantakan\n• Kadang terlalu fokus pada penampilan");
                break;
            case Constants.STYLE_AUDITORY:
                colorRes = R.color.color_auditory;
                tvResultTitle.setText("🎵 Auditori Learner");
                tvResultDesc.setText("Kamu belajar paling efektif melalui suara, musik, dan penjelasan verbal. Kamu sangat baik dalam mendengarkan dan mengingat apa yang didengar.");
                tvKelebihan.setText("• Pandai mendengarkan dan mengikuti instruksi\n• Kemampuan komunikasi verbal yang baik\n• Mudah mengingat lagu dan percakapan\n• Baik dalam debat dan diskusi kelompok");
                tvKekurangan.setText("• Mudah terganggu oleh suara bising\n• Kesulitan belajar dari teks panjang\n• Bisa lebih lambat dalam membaca mandiri");
                break;
            default:
                colorRes = R.color.color_kinestetik;
                tvResultTitle.setText("⚡ Kinestetik Learner");
                tvResultDesc.setText("Kamu belajar paling efektif melalui pengalaman langsung, gerakan, dan praktik. Kamu butuh 'menyentuh' dan merasakan materi untuk benar-benar memahaminya.");
                tvKelebihan.setText("• Baik dalam keterampilan praktis dan motorik\n• Belajar dari pengalaman dengan cepat\n• Energi tinggi dan mudah beradaptasi\n• Sangat efektif dalam lab dan praktik lapangan");
                tvKekurangan.setText("• Sulit belajar hanya dari membaca atau mendengar\n• Perlu banyak istirahat saat belajar teori\n• Kadang terlalu impulsif mengambil keputusan");
                break;
        }
        tvResultTitle.setTextColor(ContextCompat.getColor(requireContext(), colorRes));
        setupPieChart(vPct, aPct, kPct);
    }

    private void setupPieChart(float v, float a, float k) {
        try {
            List<PieEntry> entries = new ArrayList<>();
            if (v > 0) entries.add(new PieEntry(v, "Visual"));
            if (a > 0) entries.add(new PieEntry(a, "Auditori"));
            if (k > 0) entries.add(new PieEntry(k, "Kinestetik"));
            if (entries.isEmpty()) return;

            PieDataSet dataSet = new PieDataSet(entries, "");
            List<Integer> colors = new ArrayList<>();
            if (v > 0) colors.add(ContextCompat.getColor(requireContext(), R.color.color_visual));
            if (a > 0) colors.add(ContextCompat.getColor(requireContext(), R.color.color_auditory));
            if (k > 0) colors.add(ContextCompat.getColor(requireContext(), R.color.color_kinestetik));
            dataSet.setColors(colors);
            dataSet.setValueTextSize(11f);
            dataSet.setValueTextColor(Color.WHITE);
            dataSet.setSliceSpace(2f);

            PieData data = new PieData(dataSet);
            data.setValueFormatter(new PercentFormatter(pieChart));

            pieChart.setData(data);
            pieChart.setUsePercentValues(true);
            pieChart.getDescription().setEnabled(false);
            pieChart.setHoleRadius(48f);
            pieChart.setTransparentCircleRadius(52f);
            pieChart.setDrawHoleEnabled(true);
            pieChart.setHoleColor(Color.TRANSPARENT);
            pieChart.setDrawEntryLabels(false);
            pieChart.getLegend().setEnabled(true);
            pieChart.getLegend().setTextSize(12f);

            TypedValue typedValue = new TypedValue();
            requireContext().getTheme().resolveAttribute(R.attr.text_primary_color, typedValue, true);
            pieChart.getLegend().setTextColor(typedValue.data);

            pieChart.setExtraOffsets(5, 5, 5, 5);
            pieChart.animateY(1000, Easing.EaseInOutQuad);
            pieChart.invalidate();
        } catch (Exception e) {
            pieChart.setVisibility(View.GONE);
        }
    }

    private void loadBooks(String resultType) {
        if (!isAdded() || getContext() == null) return;

        if (!NetworkUtil.isConnected(requireContext())) {
            tvBooksOffline.setVisibility(View.VISIBLE);
            rvBooks.setVisibility(View.GONE);
            progressBooks.setVisibility(View.GONE);
            return;
        }

        progressBooks.setVisibility(View.VISIBLE);
        tvBooksOffline.setVisibility(View.GONE);
        rvBooks.setVisibility(View.GONE);

        String query;
        switch (resultType) {
            case Constants.STYLE_VISUAL:  query = Constants.BOOK_QUERY_VISUAL; break;
            case Constants.STYLE_AUDITORY: query = Constants.BOOK_QUERY_AUDITORY; break;
            default: query = Constants.BOOK_QUERY_KINESTETIK; break;
        }

        ApiClient.getBookApiService().searchBooks(
                query, 5, "title,author_name,first_publish_year,key"
        ).enqueue(new Callback<BookSearchResponse>() {
            @Override
            public void onResponse(@NonNull Call<BookSearchResponse> call,
                                   @NonNull Response<BookSearchResponse> response) {
                if (!isAdded()) return;
                progressBooks.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null
                        && response.body().getDocs() != null
                        && !response.body().getDocs().isEmpty()) {
                    List<BookItem> books = response.body().getDocs();
                    bookAdapter.updateData(books);
                    rvBooks.setVisibility(View.VISIBLE);
                } else {
                    tvBooksOffline.setText("Tidak ada rekomendasi buku saat ini");
                    tvBooksOffline.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFailure(@NonNull Call<BookSearchResponse> call,
                                  @NonNull Throwable t) {
                if (!isAdded()) return;
                progressBooks.setVisibility(View.GONE);
                tvBooksOffline.setVisibility(View.VISIBLE);
            }
        });
    }

    private void saveResultToDB(String type, int visual, int auditory, int kines) {
        if (!isAdded()) return;
        String date = new SimpleDateFormat("dd MMM yyyy, HH:mm",
                Locale.getDefault()).format(new Date());
        Result result = new Result(prefs.getUserId(), visual, auditory, kines, type, date);
        ExecutorManager.getInstance().execute(new BackgroundTask<Long>() {
            @Override public Long doInBackground() {
                if (getContext() == null) return -1L;
                return DatabaseHelper.getInstance(requireContext()).insertResult(result);
            }
            @Override public void onResult(Long id) { loadHistory(); }
        });
    }

    private void loadHistory() {
        if (!isAdded() || getContext() == null) return;
        int userId = prefs.getUserId();
        ExecutorManager.getInstance().execute(new BackgroundTask<List<Result>>() {
            @Override public List<Result> doInBackground() {
                if (getContext() == null) return new ArrayList<>();
                return DatabaseHelper.getInstance(requireContext())
                        .getResultsByUserId(userId);
            }
            @Override public void onResult(List<Result> results) {
                if (!isAdded()) return;
                if (results == null || results.isEmpty()) {
                    if (layoutNoHistory != null)
                        layoutNoHistory.setVisibility(View.VISIBLE);
                } else {
                    if (layoutNoHistory != null)
                        layoutNoHistory.setVisibility(View.GONE);
                    historyAdapter.updateData(results);
                }
            }
        });
    }
}