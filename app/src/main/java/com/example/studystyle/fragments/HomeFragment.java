package com.example.studystyle.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.studystyle.R;
import com.example.studystyle.api.ApiClient;
import com.example.studystyle.models.Quote;
import com.example.studystyle.utils.NetworkUtil;
import com.example.studystyle.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private PreferenceManager prefs;
    private TextView tvGreeting, tvStudyStyle;
    private CardView cardResult;
    private LinearLayout layoutNoResult;

    private CardView  cardQuote1, cardQuote2, cardQuote3;
    private TextView  tvContent1, tvAuthor1;
    private TextView  tvContent2, tvAuthor2;
    private TextView  tvContent3, tvAuthor3;
    private ImageView btnFavQuote1, btnFavQuote2, btnFavQuote3;

    private ProgressBar  progressQuote;
    private LinearLayout layoutOffline;
    private Button       btnRefresh;

    // [content1, author1, content2, author2, content3, author3]
    private final String[] quoteData = {"", "", "", "", "", ""};

    // State fetch — reset tiap kali loadQuotes() dipanggil
    private final List<Quote> collectedQuotes = new ArrayList<>();
    private int     fetchCount  = 0;
    private boolean isFetching  = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getContext() == null) return;

        prefs = new PreferenceManager(requireContext());

        tvGreeting     = view.findViewById(R.id.tv_greeting);
        tvStudyStyle   = view.findViewById(R.id.tv_study_style);
        cardResult     = view.findViewById(R.id.card_result_summary);
        layoutNoResult = view.findViewById(R.id.tv_no_result);
        progressQuote  = view.findViewById(R.id.progress_quote);
        layoutOffline  = view.findViewById(R.id.layout_offline);
        btnRefresh     = view.findViewById(R.id.btn_refresh_quote);

        cardQuote1 = view.findViewById(R.id.card_quote1);
        cardQuote2 = view.findViewById(R.id.card_quote2);
        cardQuote3 = view.findViewById(R.id.card_quote3);
        tvContent1 = view.findViewById(R.id.tv_quote_content1);
        tvAuthor1  = view.findViewById(R.id.tv_quote_author1);
        tvContent2 = view.findViewById(R.id.tv_quote_content2);
        tvAuthor2  = view.findViewById(R.id.tv_quote_author2);
        tvContent3 = view.findViewById(R.id.tv_quote_content3);
        tvAuthor3  = view.findViewById(R.id.tv_quote_author3);

        btnFavQuote1 = view.findViewById(R.id.btn_fav_quote1);
        btnFavQuote2 = view.findViewById(R.id.btn_fav_quote2);
        btnFavQuote3 = view.findViewById(R.id.btn_fav_quote3);

        Button btnStartTest = view.findViewById(R.id.btn_start_test_home);

        setupUI();
        loadQuotes();

        btnRefresh.setOnClickListener(v -> loadQuotes());
        btnStartTest.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_home_to_test));

        btnFavQuote1.setOnClickListener(v -> toggleFavorite(0));
        btnFavQuote2.setOnClickListener(v -> toggleFavorite(1));
        btnFavQuote3.setOnClickListener(v -> toggleFavorite(2));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (prefs == null) return;
        setupUI();
        // Hanya reload jika tidak sedang fetch dan quotes belum tampil
        if (!isFetching) {
            loadQuotes();
        }
    }

    private void setupUI() {
        String name  = prefs.getUserName();
        String first = (name != null && name.contains(" ")) ? name.split(" ")[0] : name;
        tvGreeting.setText("Halo, " + (first != null ? first : "") + " 👋");

        String lastResult = prefs.getLastResult();
        if (lastResult != null && !lastResult.isEmpty()) {
            tvStudyStyle.setText(lastResult + " Learner");
            cardResult.setVisibility(View.VISIBLE);
            layoutNoResult.setVisibility(View.GONE);
        } else {
            cardResult.setVisibility(View.GONE);
            layoutNoResult.setVisibility(View.VISIBLE);
        }
    }

    private void loadQuotes() {
        if (!isAdded() || getContext() == null) return;
        if (isFetching) return;

        if (NetworkUtil.isConnected(requireContext())) {
            startFetchingQuotes();
        } else {
            showOfflineState();
        }
    }

    // ── Fetch 3 quote satu per satu dari /api/random ──

    private void startFetchingQuotes() {
        if (!isAdded()) return;
        isFetching = true;
        collectedQuotes.clear();
        fetchCount = 0;

        progressQuote.setVisibility(View.VISIBLE);
        hideAllQuoteCards();
        layoutOffline.setVisibility(View.GONE);

        fetchOneQuote();
    }

    private void fetchOneQuote() {
        if (!isAdded() || getContext() == null) {
            isFetching = false;
            return;
        }

        ApiClient.getApiService().getRandomQuote().enqueue(new Callback<List<Quote>>() {
            @Override
            public void onResponse(@NonNull Call<List<Quote>> call,
                                   @NonNull Response<List<Quote>> response) {
                if (!isAdded()) {
                    isFetching = false;
                    return;
                }

                if (response.isSuccessful() && response.body() != null
                        && !response.body().isEmpty()) {
                    Quote q       = response.body().get(0);
                    String content = q.getContent();
                    // Tolak response rate-limit ZenQuotes
                    if (content != null && !content.isEmpty()
                            && !content.startsWith("Too many requests")) {
                        collectedQuotes.add(q);
                    }
                }

                fetchCount++;
                scheduleNextOrFinish();
            }

            @Override
            public void onFailure(@NonNull Call<List<Quote>> call, @NonNull Throwable t) {
                if (!isAdded()) {
                    isFetching = false;
                    return;
                }
                fetchCount++;
                scheduleNextOrFinish();
            }
        });
    }

    /**
     * Lanjut fetch berikutnya jika belum dapat 3 quote,
     * atau tampilkan hasil jika sudah cukup / sudah terlalu banyak percobaan.
     */
    private void scheduleNextOrFinish() {
        if (!isAdded()) {
            isFetching = false;
            return;
        }

        boolean cukup   = collectedQuotes.size() >= 3;
        boolean terlalu = fetchCount >= 6; // Maksimal 6 percobaan untuk dapat 3 quote

        if (cukup || terlalu) {
            // Selesai — tampilkan apa yang sudah terkumpul
            isFetching = false;
            progressQuote.setVisibility(View.GONE);

            if (!collectedQuotes.isEmpty()) {
                showQuoteCards(collectedQuotes);
            } else {
                // Semua percobaan gagal atau kena rate-limit
                showOfflineState();
            }
        } else {
            // Fetch berikutnya dengan jeda 400ms agar tidak spam
            requireView().postDelayed(this::fetchOneQuote, 400);
        }
    }

    private void showOfflineState() {
        if (!isAdded()) return;
        progressQuote.setVisibility(View.GONE);
        hideAllQuoteCards();
        layoutOffline.setVisibility(View.VISIBLE);
    }

    private void showQuoteCards(List<Quote> quotes) {
        if (!isAdded()) return;
        layoutOffline.setVisibility(View.GONE);
        int size = quotes.size();
        if (size > 0) {
            quoteData[0] = quotes.get(0).getContent();
            quoteData[1] = quotes.get(0).getAuthor();
            setQuoteCard(cardQuote1, tvContent1, tvAuthor1, btnFavQuote1,
                    quoteData[0], quoteData[1]);
        }
        if (size > 1) {
            quoteData[2] = quotes.get(1).getContent();
            quoteData[3] = quotes.get(1).getAuthor();
            setQuoteCard(cardQuote2, tvContent2, tvAuthor2, btnFavQuote2,
                    quoteData[2], quoteData[3]);
        }
        if (size > 2) {
            quoteData[4] = quotes.get(2).getContent();
            quoteData[5] = quotes.get(2).getAuthor();
            setQuoteCard(cardQuote3, tvContent3, tvAuthor3, btnFavQuote3,
                    quoteData[4], quoteData[5]);
        }
    }

    private void setQuoteCard(CardView card, TextView tvContent, TextView tvAuthor,
                              ImageView btnFav, String content, String author) {
        tvContent.setText("\u201C" + content + "\u201D");
        tvAuthor.setText("— " + author);
        card.setVisibility(View.VISIBLE);
        updateHeartIcon(btnFav, content);
    }

    private void toggleFavorite(int index) {
        if (!isAdded()) return;
        String content = quoteData[index * 2];
        String author  = quoteData[index * 2 + 1];
        if (content == null || content.isEmpty()) return;

        ImageView btn = index == 0 ? btnFavQuote1
                : index == 1 ? btnFavQuote2
                : btnFavQuote3;

        if (prefs.isQuoteFavorited(content)) {
            prefs.removeFavoriteQuote(content, author);
            btn.setImageResource(R.drawable.ic_heart);
            Toast.makeText(requireContext(), "Dihapus dari favorit", Toast.LENGTH_SHORT).show();
        } else {
            prefs.addFavoriteQuote(content, author);
            btn.setImageResource(R.drawable.ic_heart_filled);
            Toast.makeText(requireContext(), "Ditambahkan ke favorit ❤", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateHeartIcon(ImageView btn, String content) {
        btn.setImageResource(
                prefs.isQuoteFavorited(content)
                        ? R.drawable.ic_heart_filled
                        : R.drawable.ic_heart);
    }

    private void hideAllQuoteCards() {
        cardQuote1.setVisibility(View.INVISIBLE);
        cardQuote2.setVisibility(View.INVISIBLE);
        cardQuote3.setVisibility(View.INVISIBLE);
    }
}