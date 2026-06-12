package com.example.studystyle.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studystyle.R;
import com.example.studystyle.activities.BookDetailActivity;
import com.example.studystyle.adapters.FavoriteBookAdapter;
import com.example.studystyle.adapters.FavoriteQuoteAdapter;
import com.example.studystyle.models.BookItem;
import com.example.studystyle.utils.PreferenceManager;
import com.google.gson.Gson;

import java.util.List;

public class FavoriteFragment extends Fragment {

    private PreferenceManager prefs;

    private TextView tabQuotes, tabBooks;
    private RecyclerView rvQuotes, rvBooks;
    private LinearLayout layoutEmpty;
    private TextView tvEmptyMessage, tvEmptyHint;

    private boolean showingQuotes = true;
    private int colorSelectedText;
    private int colorUnselectedText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getContext() == null) return;

        prefs = new PreferenceManager(requireContext());

        tabQuotes      = view.findViewById(R.id.tab_quotes);
        tabBooks       = view.findViewById(R.id.tab_books);
        rvQuotes       = view.findViewById(R.id.rv_favorite_quotes);
        rvBooks        = view.findViewById(R.id.rv_favorite_books);
        layoutEmpty    = view.findViewById(R.id.layout_empty);
        tvEmptyMessage = view.findViewById(R.id.tv_empty_message);
        tvEmptyHint    = view.findViewById(R.id.tv_empty_hint);


        LinearLayout tabContainer = view.findViewById(R.id.tab_container);
        TypedValue tv = new TypedValue();
        requireContext().getTheme().resolveAttribute(R.attr.background_primary, tv, true);
        int bgColor = tv.data;

        GradientDrawable tabBg = new GradientDrawable();
        tabBg.setShape(GradientDrawable.RECTANGLE);
        tabBg.setCornerRadius(50f);
        tabBg.setColor(bgColor);
        tabBg.setStroke(2, Color.parseColor("#AAAAAA"));
        tabContainer.setBackground(tabBg);


        boolean isDark = prefs.isDarkMode();
        colorSelectedText   = requireContext().getResources().getColor(R.color.tab_selected_text, null);
        colorUnselectedText = requireContext().getResources().getColor(
                isDark ? R.color.tab_unselected_text_dark : R.color.tab_unselected_text, null);

        rvQuotes.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvBooks.setLayoutManager(new LinearLayoutManager(requireContext()));

        tabQuotes.setOnClickListener(v -> switchTab(true));
        tabBooks.setOnClickListener(v -> switchTab(false));

        switchTab(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (showingQuotes) loadQuotes();
        else loadBooks();
    }

    private void switchTab(boolean quotes) {
        showingQuotes = quotes;

        if (quotes) {
            tabQuotes.setTextColor(colorSelectedText);
            tabQuotes.setBackgroundResource(R.drawable.bg_button);
            tabBooks.setTextColor(colorUnselectedText);
            tabBooks.setBackgroundResource(R.color.transparent);
            loadQuotes();
        } else {
            tabBooks.setTextColor(colorSelectedText);
            tabBooks.setBackgroundResource(R.drawable.bg_button);
            tabQuotes.setTextColor(colorUnselectedText);
            tabQuotes.setBackgroundResource(R.color.transparent);
            loadBooks();
        }
    }

    private void loadQuotes() {
        if (!isAdded()) return;
        List<String[]> favorites = prefs.getFavoriteQuotes();

        rvBooks.setVisibility(View.GONE);

        if (favorites.isEmpty()) {
            rvQuotes.setVisibility(View.GONE);
            showEmpty("Belum ada quote favorit",
                    "Tekan ikon ❤ di samping quote untuk menyimpannya");
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvQuotes.setVisibility(View.VISIBLE);
            FavoriteQuoteAdapter adapter = new FavoriteQuoteAdapter(favorites, (pos, content, author) -> {
                prefs.removeFavoriteQuote(content, author);
                favorites.remove(pos);
                rvQuotes.getAdapter().notifyItemRemoved(pos);
                if (favorites.isEmpty()) {
                    rvQuotes.setVisibility(View.GONE);
                    showEmpty("Belum ada quote favorit",
                            "Tekan ikon ❤ di samping quote untuk menyimpannya");
                }
            });
            rvQuotes.setAdapter(adapter);
        }
    }

    private void loadBooks() {
        if (!isAdded()) return;
        List<BookItem> favorites = prefs.getFavoriteBooks();

        rvQuotes.setVisibility(View.GONE);

        if (favorites.isEmpty()) {
            rvBooks.setVisibility(View.GONE);
            showEmpty("Belum ada buku favorit",
                    "Tekan tombol ❤ di halaman detail buku untuk menyimpannya");
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvBooks.setVisibility(View.VISIBLE);
            FavoriteBookAdapter adapter = new FavoriteBookAdapter(
                    favorites,
                    (pos, book) -> {
                        prefs.removeFavoriteBook(book.getKey());
                        favorites.remove(pos);
                        rvBooks.getAdapter().notifyItemRemoved(pos);
                        if (favorites.isEmpty()) {
                            rvBooks.setVisibility(View.GONE);
                            showEmpty("Belum ada buku favorit",
                                    "Tekan tombol ❤ di halaman detail buku untuk menyimpannya");
                        }
                    },
                    book -> {
                        Intent intent = new Intent(requireContext(), BookDetailActivity.class);
                        intent.putExtra(BookDetailActivity.EXTRA_BOOK_JSON, new Gson().toJson(book));
                        startActivity(intent);
                    });
            rvBooks.setAdapter(adapter);
        }
    }

    private void showEmpty(String message, String hint) {
        layoutEmpty.setVisibility(View.VISIBLE);
        tvEmptyMessage.setText(message);
        tvEmptyHint.setText(hint);
    }
}