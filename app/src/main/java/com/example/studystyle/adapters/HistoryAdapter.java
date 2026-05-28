package com.example.studystyle.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studystyle.R;
import com.example.studystyle.models.Result;
import com.example.studystyle.utils.Constants;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private final Context context;
    private List<Result> results;

    public HistoryAdapter(Context context, List<Result> results) {
        this.context = context;
        this.results = results;
    }

    public void updateData(List<Result> newResults) {
        this.results = newResults;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        Result result = results.get(position);

        holder.tvResultType.setText(result.getResultType() + " Learner");
        holder.tvDate.setText(result.getDate());

        int total = result.getVisualScore() + result.getAuditoryScore() + result.getKinestetikScore();
        if (total > 0) {
            int dominant;
            switch (result.getResultType()) {
                case Constants.STYLE_VISUAL:
                    dominant = (result.getVisualScore() * 100) / total;
                    break;
                case Constants.STYLE_AUDITORY:
                    dominant = (result.getAuditoryScore() * 100) / total;
                    break;
                default:
                    dominant = (result.getKinestetikScore() * 100) / total;
                    break;
            }
            holder.tvScore.setText(dominant + "%");
        }

        // Color accent based on type
        int colorRes;
        switch (result.getResultType()) {
            case Constants.STYLE_VISUAL:
                colorRes = R.color.color_visual;
                break;
            case Constants.STYLE_AUDITORY:
                colorRes = R.color.color_auditory;
                break;
            default:
                colorRes = R.color.color_kinestetik;
                break;
        }
        holder.tvResultType.setTextColor(ContextCompat.getColor(context, colorRes));
        holder.tvScore.setTextColor(ContextCompat.getColor(context, colorRes));
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvResultType, tvDate, tvScore;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_history);
            tvResultType = itemView.findViewById(R.id.tv_result_type);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvScore = itemView.findViewById(R.id.tv_score);
        }
    }
}
