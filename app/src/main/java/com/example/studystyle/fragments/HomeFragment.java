package com.example.studystyle.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private PreferenceManager prefs;
    private TextView tvGreeting, tvStudyStyle;
    private CardView cardResult;
    private LinearLayout layoutNoResult;

    private CardView cardQuote1, cardQuote2, cardQuote3;
    private TextView tvContent1, tvAuthor1;
    private TextView tvContent2, tvAuthor2;
    private TextView tvContent3, tvAuthor3;

    private ProgressBar progressQuote;
    private LinearLayout layoutOffline;
    private Button btnRefresh;

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

        Button btnStartTest = view.findViewById(R.id.btn_start_test_home);

        setupUI();
        loadQuotes();

        btnRefresh.setOnClickListener(v -> loadQuotes());
        btnStartTest.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_home_to_test));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (prefs != null) setupUI();
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

        if (NetworkUtil.isConnected(requireContext())) {
            fetchQuotesFromApi();
        } else {
            showOfflineState();
        }
    }

    private void fetchQuotesFromApi() {
        progressQuote.setVisibility(View.VISIBLE);
        hideAllQuoteCards();
        layoutOffline.setVisibility(View.GONE);

        // ZenQuotes mengembalikan List<Quote> langsung, bukan wrapped object
        ApiClient.getApiService().getQuotes().enqueue(new Callback<List<Quote>>() {
            @Override
            public void onResponse(@NonNull Call<List<Quote>> call,
                                   @NonNull Response<List<Quote>> response) {
                if (!isAdded()) return;
                progressQuote.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null
                        && !response.body().isEmpty()) {
                    List<Quote> quotes = response.body();
                    // Cache quote pertama
                    prefs.cacheQuote(quotes.get(0).getContent(), quotes.get(0).getAuthor());
                    showQuoteCards(quotes);
                } else {
                    showOfflineState();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Quote>> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                progressQuote.setVisibility(View.GONE);
                showOfflineState();
            }
        });
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
        if (size > 0) setQuoteCard(cardQuote1, tvContent1, tvAuthor1,
                quotes.get(0).getContent(), quotes.get(0).getAuthor());
        if (size > 1) setQuoteCard(cardQuote2, tvContent2, tvAuthor2,
                quotes.get(1).getContent(), quotes.get(1).getAuthor());
        if (size > 2) setQuoteCard(cardQuote3, tvContent3, tvAuthor3,
                quotes.get(2).getContent(), quotes.get(2).getAuthor());
    }

    private void setQuoteCard(CardView card, TextView tvContent, TextView tvAuthor,
                              String content, String author) {
        tvContent.setText("\u201C" + content + "\u201D");
        tvAuthor.setText("— " + author);
        card.setVisibility(View.VISIBLE);
    }

    private void hideAllQuoteCards() {
        cardQuote1.setVisibility(View.INVISIBLE);
        cardQuote2.setVisibility(View.INVISIBLE);
        cardQuote3.setVisibility(View.INVISIBLE);
    }
}