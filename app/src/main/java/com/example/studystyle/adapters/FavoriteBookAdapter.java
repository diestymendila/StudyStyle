package com.example.studystyle.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.studystyle.R;
import com.example.studystyle.models.BookItem;

import java.util.List;

public class FavoriteBookAdapter extends RecyclerView.Adapter<FavoriteBookAdapter.ViewHolder> {

    public interface OnRemoveListener {
        void onRemove(int position, BookItem book);
    }

    public interface OnItemClickListener {
        void onClick(BookItem book);
    }

    private final List<BookItem> items;
    private final OnRemoveListener  removeListener;
    private final OnItemClickListener clickListener;

    public FavoriteBookAdapter(List<BookItem> items,
                               OnRemoveListener removeListener,
                               OnItemClickListener clickListener) {
        this.items         = items;
        this.removeListener  = removeListener;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite_book, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        BookItem book = items.get(position);
        h.tvTitle.setText(book.getTitle());
        h.tvAuthor.setText(book.getAuthor());
        h.tvGenre.setText(book.getGenre());

        if (!book.getCover().isEmpty()) {
            Glide.with(h.ivCover.getContext())
                    .load(book.getCover())
                    .placeholder(R.drawable.ic_book_placeholder)
                    .error(R.drawable.ic_book_placeholder)
                    .centerCrop()
                    .into(h.ivCover);
        } else {
            h.ivCover.setImageResource(R.drawable.ic_book_placeholder);
        }

        h.btnRemove.setOnClickListener(v -> {
            int pos = h.getAdapterPosition();
            if (pos != RecyclerView.NO_ID) {
                removeListener.onRemove(pos, book);
            }
        });

        h.itemView.setOnClickListener(v -> clickListener.onClick(book));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover, btnRemove;
        TextView  tvTitle, tvAuthor, tvGenre;

        ViewHolder(View v) {
            super(v);
            ivCover   = v.findViewById(R.id.iv_fav_book_cover);
            tvTitle   = v.findViewById(R.id.tv_fav_book_title);
            tvAuthor  = v.findViewById(R.id.tv_fav_book_author);
            tvGenre   = v.findViewById(R.id.tv_fav_book_genre);
            btnRemove = v.findViewById(R.id.btn_remove_favorite_book);
        }
    }
}