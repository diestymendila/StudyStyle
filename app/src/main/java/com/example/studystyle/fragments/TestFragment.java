package com.example.studystyle.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studystyle.R;
import com.example.studystyle.adapters.QuestionAdapter;
import com.example.studystyle.models.Question;
import com.example.studystyle.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class TestFragment extends Fragment {

    private RecyclerView recyclerView;
    private QuestionAdapter adapter;
    private List<Question> questions;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rv_questions);
        Button btnSubmit = view.findViewById(R.id.btn_submit_test);

        questions = buildQuestions();
        adapter = new QuestionAdapter(requireContext(), questions);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        btnSubmit.setOnClickListener(v -> submitTest(view));
    }

    private void submitTest(View view) {
        // Validate all answered
        for (Question q : questions) {
            if (q.getSelectedOption() == 0) {
                Toast.makeText(requireContext(),
                        "Jawab semua pertanyaan terlebih dahulu!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Calculate scores
        int visual = 0, auditory = 0, kinestetik = 0;
        for (Question q : questions) {
            switch (q.getSelectedOption()) {
                case 1: visual++; break;
                case 2: auditory++; break;
                case 3: kinestetik++; break;
            }
        }

        // Determine dominant style
        String resultType;
        if (visual >= auditory && visual >= kinestetik) {
            resultType = Constants.STYLE_VISUAL;
        } else if (auditory >= visual && auditory >= kinestetik) {
            resultType = Constants.STYLE_AUDITORY;
        } else {
            resultType = Constants.STYLE_KINESTETIK;
        }

        // Navigate to result
        Bundle args = new Bundle();
        args.putString(Constants.INTENT_RESULT_TYPE, resultType);
        args.putInt(Constants.INTENT_VISUAL_SCORE, visual);
        args.putInt(Constants.INTENT_AUDITORY_SCORE, auditory);
        args.putInt(Constants.INTENT_KINESTETIK_SCORE, kinestetik);

        Navigation.findNavController(view).navigate(R.id.action_test_to_result, args);
    }

    private List<Question> buildQuestions() {
        List<Question> list = new ArrayList<>();
        list.add(new Question(1,
                "Saat belajar materi baru, saya lebih mudah memahami melalui...",
                "Diagram, gambar, atau video penjelasan",
                "Penjelasan lisan atau rekaman audio",
                "Langsung mencoba dan mempraktikkan sendiri"));
        list.add(new Question(2,
                "Ketika mengingat sesuatu, saya biasanya...",
                "Membayangkan gambar atau tulisan di kepala",
                "Mengulang dalam hati atau berbicara pelan",
                "Menggerakkan tubuh atau membuat gerakan tertentu"));
        list.add(new Question(3,
                "Saat menghafal materi, cara paling efektif bagi saya adalah...",
                "Membuat mind map atau catatan berwarna",
                "Membaca keras-keras atau mendengar rekaman",
                "Menulis berulang-ulang atau berjalan sambil belajar"));
        list.add(new Question(4,
                "Di kelas, saya lebih fokus ketika...",
                "Dosen menampilkan slide atau tulisan di papan",
                "Dosen menjelaskan secara verbal dengan detail",
                "Ada sesi diskusi, praktek, atau simulasi"));
        list.add(new Question(5,
                "Ketika memberi petunjuk arah, saya cenderung...",
                "Menggambar peta atau sketsa rute",
                "Menjelaskan langkah-langkah secara verbal",
                "Menunjukkan dengan tangan atau mengajak langsung ke tempat"));
        list.add(new Question(6,
                "Saat memilih buku teks, saya lebih suka buku yang...",
                "Banyak ilustrasi, grafik, dan diagram berwarna",
                "Penjelasan naratif yang detail dan jelas",
                "Banyak latihan soal dan aktivitas praktis"));
        list.add(new Question(7,
                "Cara saya meluangkan waktu istirahat dari belajar...",
                "Menonton video atau melihat gambar menarik",
                "Mendengarkan musik atau podcast",
                "Berolahraga, jalan-jalan, atau aktivitas fisik"));
        list.add(new Question(8,
                "Saat presentasi, saya biasanya...",
                "Mengandalkan slide visual yang menarik",
                "Fokus pada penyampaian lisan yang jelas",
                "Mendemonstrasikan langsung atau mengajak interaksi"));
        list.add(new Question(9,
                "Saat mengerjakan ujian, saya lebih mudah mengingat materi yang...",
                "Saya visualisasikan atau buat dalam bentuk diagram",
                "Saya dengar dari penjelasan dosen atau rekaman",
                "Pernah saya praktikkan secara langsung"));
        list.add(new Question(10,
                "Ketika belajar keterampilan baru seperti memasak atau coding...",
                "Saya membaca panduan dan melihat foto step-by-step",
                "Saya mendengarkan tutorial audio atau video dengan narasi",
                "Saya langsung mencoba dan belajar dari kesalahan"));
        list.add(new Question(11,
                "Saya paling sering membuat catatan berupa...",
                "Bagan, tabel, atau gambar ilustrasi",
                "Tulisan poin-poin rangkuman verbal",
                "Catatan singkat lalu langsung latihan soal"));
        list.add(new Question(12,
                "Ketika memahami konsep abstrak, saya perlu...",
                "Melihat representasi visual atau animasinya",
                "Mendengar analogi atau penjelasan mendalam",
                "Melakukan simulasi atau demonstrasi nyata"));
        list.add(new Question(13,
                "Lingkungan belajar ideal saya adalah...",
                "Tempat bersih, rapi, dengan visual inspiratif di dinding",
                "Tempat tenang, bisa sambil mendengarkan musik",
                "Fleksibel, bisa bergerak atau berganti posisi belajar"));
        list.add(new Question(14,
                "Saat membeli produk baru, saya pertama kali...",
                "Melihat tampilan fisik dan desainnya",
                "Membaca ulasan atau mendengar rekomendasi",
                "Langsung mencoba dan merasakannya"));
        list.add(new Question(15,
                "Metode belajar yang paling saya nikmati adalah...",
                "Infografis, video animasi, atau presentasi visual",
                "Kuliah umum, podcast, atau diskusi kelompok",
                "Field trip, eksperimen, atau proyek langsung"));

        return list;
    }
}
