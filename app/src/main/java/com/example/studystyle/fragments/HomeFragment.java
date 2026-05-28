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
import com.example.studystyle.api.ApiService;
import com.example.studystyle.models.Quote;
import com.example.studystyle.utils.Constants;
import com.example.studystyle.utils.NetworkUtil;
import com.example.studystyle.utils.PreferenceManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private PreferenceManager prefs;
    private TextView tvGreeting, tvStudyStyle, tvQuoteContent, tvQuoteAuthor;
    private TextView tvNoResult;
    private CardView cardResult, cardQuote;
    private ProgressBar progressQuote;
    private LinearLayout layoutOffline;
    private Button btnRefreshQuote, btnStartTest;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = new PreferenceManager(requireContext());

        tvGreeting = view.findViewById(R.id.tv_greeting);
        tvStudyStyle = view.findViewById(R.id.tv_study_style);
        tvQuoteContent = view.findViewById(R.id.tv_quote_content);
        tvQuoteAuthor = view.findViewById(R.id.tv_quote_author);
        tvNoResult = view.findViewById(R.id.tv_no_result);
        cardResult = view.findViewById(R.id.card_result_summary);
        cardQuote = view.findViewById(R.id.card_quote);
        progressQuote = view.findViewById(R.id.progress_quote);
        layoutOffline = view.findViewById(R.id.layout_offline);
        btnRefreshQuote = view.findViewById(R.id.btn_refresh_quote);
        btnStartTest = view.findViewById(R.id.btn_start_test_home);

        setupUI();
        fetchQuote();

        btnRefreshQuote.setOnClickListener(v -> fetchQuote());
        btnStartTest.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_home_to_test));
    }

    @Override
    public void onResume() {
        super.onResume();
        setupUI(); // refresh after returning from test
    }

    private void setupUI() {
        String name = prefs.getUserName();
        String firstName = name.contains(" ") ? name.split(" ")[0] : name;
        tvGreeting.setText("Halo, " + firstName + " 👋");

        String lastResult = prefs.getLastResult();
        if (!lastResult.isEmpty()) {
            tvStudyStyle.setText(lastResult + " Learner");
            cardResult.setVisibility(View.VISIBLE);
            tvNoResult.setVisibility(View.GONE);
        } else {
            cardResult.setVisibility(View.GONE);
            tvNoResult.setVisibility(View.VISIBLE);
        }
    }

    private void fetchQuote() {
        if (!NetworkUtil.isConnected(requireContext())) {
            showOfflineState();
            return;
        }

        progressQuote.setVisibility(View.VISIBLE);
        cardQuote.setVisibility(View.INVISIBLE);
        layoutOffline.setVisibility(View.GONE);

        ApiService api = ApiClient.getApiService();
        api.getRandomQuote().enqueue(new Callback<Quote>() {
            @Override
            public void onResponse(@NonNull Call<Quote> call, @NonNull Response<Quote> response) {
                if (!isAdded()) return;
                progressQuote.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    Quote quote = response.body();
                    tvQuoteContent.setText("\u201C" + quote.getContent() + "\u201D");
                    tvQuoteAuthor.setText("— " + quote.getAuthor());
                    cardQuote.setVisibility(View.VISIBLE);
                } else {
                    showOfflineState();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Quote> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                progressQuote.setVisibility(View.GONE);
                showOfflineState();
            }
        });
    }

    private void showOfflineState() {
        cardQuote.setVisibility(View.GONE);
        progressQuote.setVisibility(View.GONE);
        layoutOffline.setVisibility(View.VISIBLE);
    }
}
