package com.example.studystyle.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studystyle.R;
import com.example.studystyle.models.Result;
import com.example.studystyle.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private final Context context;
    private List<Result> results;

    public HistoryAdapter(Context context, List<Result> results) {
        this.context = context;
        this.results = results != null ? results : new ArrayList<>();
    }

    public void updateData(List<Result> newResults) {
        this.results = newResults != null ? newResults : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder h, int position) {
        Result r = results.get(position);

        h.tvResultType.setText(r.getResultType() + " Learner");
        h.tvDate.setText(r.getDate());

        int total = r.getVisualScore() + r.getAuditoryScore() + r.getKinestetikScore();
        if (total > 0) {
            int dominant;
            switch (r.getResultType()) {
                case Constants.STYLE_VISUAL:
                    dominant = (r.getVisualScore() * 100) / total; break;
                case Constants.STYLE_AUDITORY:
                    dominant = (r.getAuditoryScore() * 100) / total; break;
                default:
                    dominant = (r.getKinestetikScore() * 100) / total; break;
            }
            h.tvScore.setText(dominant + "%");
        } else {
            h.tvScore.setText("—");
        }

        int colorRes;
        switch (r.getResultType()) {
            case Constants.STYLE_VISUAL:   colorRes = R.color.color_visual;   break;
            case Constants.STYLE_AUDITORY: colorRes = R.color.color_auditory; break;
            default:                       colorRes = R.color.color_kinestetik; break;
        }
        int color = ContextCompat.getColor(context, colorRes);
        h.tvResultType.setTextColor(color);
        h.tvScore.setTextColor(color);
    }

    @Override public int getItemCount() {
        return results != null ? results.size() : 0;
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvResultType, tvDate, tvScore;
        HistoryViewHolder(@NonNull View v) {
            super(v);
            tvResultType = v.findViewById(R.id.tv_result_type);
            tvDate       = v.findViewById(R.id.tv_date);
            tvScore      = v.findViewById(R.id.tv_score);
        }
    }
}
