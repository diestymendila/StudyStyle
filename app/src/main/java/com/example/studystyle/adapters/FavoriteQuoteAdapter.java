package com.example.studystyle.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studystyle.R;

import java.util.List;

public class FavoriteQuoteAdapter extends RecyclerView.Adapter<FavoriteQuoteAdapter.ViewHolder> {

    public interface OnRemoveListener {
        void onRemove(int position, String content, String author);
    }

    private final List<String[]> items; // [content, author]
    private final OnRemoveListener listener;

    public FavoriteQuoteAdapter(List<String[]> items, OnRemoveListener listener) {
        this.items    = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite_quote, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        String[] item = items.get(position);
        String content = item[0];
        String author  = item[1];
        h.tvContent.setText("\u201C" + content + "\u201D");
        h.tvAuthor.setText("— " + author);
        h.btnRemove.setOnClickListener(v -> {
            int pos = h.getAdapterPosition();
            if (pos != RecyclerView.NO_ID) {
                listener.onRemove(pos, content, author);
            }
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView  tvContent, tvAuthor;
        ImageView btnRemove;

        ViewHolder(View v) {
            super(v);
            tvContent = v.findViewById(R.id.tv_fav_quote_content);
            tvAuthor  = v.findViewById(R.id.tv_fav_quote_author);
            btnRemove = v.findViewById(R.id.btn_remove_favorite_quote);
        }
    }
}