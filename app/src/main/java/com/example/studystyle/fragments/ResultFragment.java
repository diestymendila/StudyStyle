package com.example.studystyle.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studystyle.R;
import com.example.studystyle.adapters.HistoryAdapter;
import com.example.studystyle.background.BackgroundTask;
import com.example.studystyle.background.ExecutorManager;
import com.example.studystyle.database.DatabaseHelper;
import com.example.studystyle.models.Result;
import com.example.studystyle.utils.Constants;
import com.example.studystyle.utils.PreferenceManager;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.animation.Easing;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ResultFragment extends Fragment {

    private PreferenceManager prefs;
    private TextView tvResultTitle, tvResultDesc, tvKelebihan, tvKekurangan;
    private TextView tvVisualPct, tvAuditoryPct, tvKinestetikPct;
    private PieChart pieChart;
    private RecyclerView rvHistory;
    private HistoryAdapter historyAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = new PreferenceManager(requireContext());

        tvResultTitle = view.findViewById(R.id.tv_result_title);
        tvResultDesc = view.findViewById(R.id.tv_result_desc);
        tvKelebihan = view.findViewById(R.id.tv_kelebihan);
        tvKekurangan = view.findViewById(R.id.tv_kekurangan);
        tvVisualPct = view.findViewById(R.id.tv_visual_pct);
        tvAuditoryPct = view.findViewById(R.id.tv_auditory_pct);
        tvKinestetikPct = view.findViewById(R.id.tv_kinestetik_pct);
        pieChart = view.findViewById(R.id.pie_chart);
        rvHistory = view.findViewById(R.id.rv_history);
        Button btnRetake = view.findViewById(R.id.btn_retake_test);
        Button btnHome = view.findViewById(R.id.btn_go_home);

        // Get args
        Bundle args = getArguments();
        if (args != null) {
            String resultType = args.getString(Constants.INTENT_RESULT_TYPE, "");
            int visual = args.getInt(Constants.INTENT_VISUAL_SCORE, 0);
            int auditory = args.getInt(Constants.INTENT_AUDITORY_SCORE, 0);
            int kinestetik = args.getInt(Constants.INTENT_KINESTETIK_SCORE, 0);

            displayResult(resultType, visual, auditory, kinestetik);
            saveResult(resultType, visual, auditory, kinestetik);

            // Save to prefs for home screen
            prefs.saveLastResult(resultType, visual, auditory, kinestetik);
        } else {
            // Load from prefs (if navigated directly)
            loadFromPrefs();
        }

        setupHistory();

        btnRetake.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_result_to_test));
        btnHome.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_result_to_home));
    }

    private void displayResult(String type, int visual, int auditory, int kinestetik) {
        int total = visual + auditory + kinestetik;
        if (total == 0) return;

        float vPct = (visual * 100f) / total;
        float aPct = (auditory * 100f) / total;
        float kPct = (kinestetik * 100f) / total;

        tvVisualPct.setText(String.format(Locale.getDefault(), "%.0f%%", vPct));
        tvAuditoryPct.setText(String.format(Locale.getDefault(), "%.0f%%", aPct));
        tvKinestetikPct.setText(String.format(Locale.getDefault(), "%.0f%%", kPct));

        switch (type) {
            case Constants.STYLE_VISUAL:
                tvResultTitle.setText("🎨 Visual Learner");
                tvResultTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_visual));
                tvResultDesc.setText("Kamu belajar paling efektif melalui gambar, diagram, warna, dan representasi visual. Otak kamu sangat aktif memproses informasi secara spasial.");
                tvKelebihan.setText("✓ Mudah memahami peta, diagram, dan grafik\n✓ Imajinasi dan kreativitas tinggi\n✓ Ingatan visual yang kuat\n✓ Baik dalam desain dan seni");
                tvKekurangan.setText("✗ Sulit belajar dari penjelasan verbal saja\n✗ Bisa terganggu oleh lingkungan visual yang berantakan\n✗ Kadang terlalu fokus pada penampilan");
                break;

            case Constants.STYLE_AUDITORY:
                tvResultTitle.setText("🎵 Auditori Learner");
                tvResultTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_auditory));
                tvResultDesc.setText("Kamu belajar paling efektif melalui suara, musik, dan penjelasan verbal. Kamu sangat baik dalam mendengarkan dan mengingat apa yang didengar.");
                tvKelebihan.setText("✓ Pandai mendengarkan dan mengikuti instruksi\n✓ Kemampuan komunikasi verbal yang baik\n✓ Mudah mengingat lagu dan percakapan\n✓ Baik dalam debat dan diskusi");
                tvKekurangan.setText("✗ Mudah terganggu oleh suara bising\n✗ Kesulitan belajar dari teks panjang tanpa penjelasan\n✗ Bisa lebih lambat dalam membaca");
                break;

            default: // Kinestetik
                tvResultTitle.setText("⚡ Kinestetik Learner");
                tvResultTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_kinestetik));
                tvResultDesc.setText("Kamu belajar paling efektif melalui pengalaman langsung, gerakan, dan praktik. Kamu butuh 'menyentuh' dan merasakan materi untuk benar-benar memahaminya.");
                tvKelebihan.setText("✓ Baik dalam keterampilan praktis dan motorik\n✓ Belajar dari pengalaman dengan cepat\n✓ Energi tinggi dan mudah beradaptasi\n✓ Sangat efektif dalam lab dan lapangan");
                tvKekurangan.setText("✗ Sulit belajar hanya dari membaca atau mendengar\n✗ Perlu banyak istirahat saat belajar teori\n✗ Kadang terlalu impulsif dalam mengambil keputusan");
                break;
        }

        setupPieChart(vPct, aPct, kPct);
    }

    private void loadFromPrefs() {
        int visual = prefs.getLastVisual();
        int auditory = prefs.getLastAuditory();
        int kinestetik = prefs.getLastKinestetik();
        String type = prefs.getLastResult();
        if (!type.isEmpty()) {
            displayResult(type, visual, auditory, kinestetik);
        }
    }

    private void setupPieChart(float v, float a, float k) {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(v, "Visual"));
        entries.add(new PieEntry(a, "Auditori"));
        entries.add(new PieEntry(k, "Kinestetik"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(
                ContextCompat.getColor(requireContext(), R.color.color_visual),
                ContextCompat.getColor(requireContext(), R.color.color_auditory),
                ContextCompat.getColor(requireContext(), R.color.color_kinestetik)
        );
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setSliceSpace(3f);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setHoleRadius(50f);
        pieChart.setTransparentCircleRadius(55f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.getLegend().setEnabled(true);
        pieChart.getLegend().setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary));
        pieChart.animateY(1200, Easing.EaseInOutQuad);
        pieChart.invalidate();
    }

    private void saveResult(String type, int visual, int auditory, int kinestetik) {
        String date = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                .format(new Date());
        int userId = prefs.getUserId();

        Result result = new Result(userId, visual, auditory, kinestetik, type, date);

        ExecutorManager.getInstance().execute(new BackgroundTask<Long>() {
            @Override
            public Long doInBackground() {
                return DatabaseHelper.getInstance(requireContext()).insertResult(result);
            }

            @Override
            public void onResult(Long id) {
                setupHistory(); // refresh history after save
            }
        });
    }

    private void setupHistory() {
        int userId = prefs.getUserId();
        ExecutorManager.getInstance().execute(new BackgroundTask<List<Result>>() {
            @Override
            public List<Result> doInBackground() {
                return DatabaseHelper.getInstance(requireContext()).getResultsByUserId(userId);
            }

            @Override
            public void onResult(List<Result> results) {
                if (!isAdded()) return;
                if (historyAdapter == null) {
                    historyAdapter = new HistoryAdapter(requireContext(), results);
                    rvHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
                    rvHistory.setAdapter(historyAdapter);
                    rvHistory.setNestedScrollingEnabled(false);
                } else {
                    historyAdapter.updateData(results);
                }
            }
        });
    }
}
