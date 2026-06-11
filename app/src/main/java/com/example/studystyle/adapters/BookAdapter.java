package com.example.studystyle.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.studystyle.R;
import com.example.studystyle.activities.BookDetailActivity;
import com.example.studystyle.models.BookItem;
import com.example.studystyle.utils.PreferenceManager;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    public interface OnBookClickListener {
        void onBookClick(BookItem book);
    }

    private final Context context;
    private List<BookItem> books;
    private OnBookClickListener listener;
    private final PreferenceManager prefs;

    public BookAdapter(Context context, List<BookItem> books) {
        this.context = context;
        this.books   = books != null ? books : new ArrayList<>();
        this.prefs   = new PreferenceManager(context);
    }

    public void setOnBookClickListener(OnBookClickListener listener) {
        this.listener = listener;
    }

    public void updateData(List<BookItem> newBooks) {
        this.books = newBooks != null ? newBooks : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder h, int position) {
        BookItem book = books.get(position);

        h.tvTitle.setText(book.getTitle());
        h.tvAuthor.setText(book.getAuthor());
        h.tvYear.setText(book.getYear());
        h.tvGenre.setText(book.getGenre().isEmpty() ? "Education" : book.getGenre());

        // Tampilkan rating tersimpan, atau "Belum dirating" jika belum ada
        int savedRating = prefs.getBookRating(book.getKey());
        if (savedRating > 0) {
            h.tvRating.setText("★ " + savedRating + "/5");
        } else {
            h.tvRating.setText("★ Belum dirating");
        }

        if (!book.getCover().isEmpty()) {
            Glide.with(context)
                    .load(book.getCover())
                    .placeholder(R.drawable.ic_book_placeholder)
                    .error(R.drawable.ic_book_placeholder)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .into(h.ivCover);
        } else {
            h.ivCover.setImageResource(R.drawable.ic_book_placeholder);
        }

        // Klik card → buka BookDetailActivity via listener (agar ResultFragment bisa
        // pakai startActivityForResult dan menerima result balik)
        h.cardBook.setOnClickListener(v -> {
            if (listener != null) listener.onBookClick(book);
        });
    }

    @Override public int getItemCount() { return books != null ? books.size() : 0; }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        CardView cardBook;
        ImageView ivCover;
        TextView tvTitle, tvAuthor, tvYear, tvGenre, tvRating;

        BookViewHolder(@NonNull View v) {
            super(v);
            cardBook = v.findViewById(R.id.card_book);
            ivCover  = v.findViewById(R.id.iv_book_cover);
            tvTitle  = v.findViewById(R.id.tv_book_title);
            tvAuthor = v.findViewById(R.id.tv_book_author);
            tvYear   = v.findViewById(R.id.tv_book_year);
            tvGenre  = v.findViewById(R.id.tv_book_genre);
            tvRating = v.findViewById(R.id.tv_book_rating);
        }
    }
}