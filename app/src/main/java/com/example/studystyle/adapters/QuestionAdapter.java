package com.example.studystyle.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studystyle.R;
import com.example.studystyle.models.Question;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    private final Context context;
    private final List<Question> questions;

    public QuestionAdapter(Context context, List<Question> questions) {
        this.context   = context;
        this.questions = questions;
    }

    @NonNull @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder h, int position) {
        Question q = questions.get(position);

        h.tvNumber.setText(String.format("Pertanyaan %d", position + 1));
        h.tvQuestion.setText(q.getQuestionText());
        h.rbA.setText(q.getOptionA());
        h.rbB.setText(q.getOptionB());
        h.rbC.setText(q.getOptionC());

        // Clear listener sebelum set checked agar tidak trigger loop
        h.rgOptions.setOnCheckedChangeListener(null);
        h.rgOptions.clearCheck();

        switch (q.getSelectedOption()) {
            case 1: h.rgOptions.check(R.id.rb_option_a); break;
            case 2: h.rgOptions.check(R.id.rb_option_b); break;
            case 3: h.rgOptions.check(R.id.rb_option_c); break;
        }

        h.rgOptions.setOnCheckedChangeListener((group, checkedId) -> {
            if      (checkedId == R.id.rb_option_a) q.setSelectedOption(1);
            else if (checkedId == R.id.rb_option_b) q.setSelectedOption(2);
            else if (checkedId == R.id.rb_option_c) q.setSelectedOption(3);
            else                                     q.setSelectedOption(0);
        });
    }

    @Override public int getItemCount() { return questions.size(); }

    static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView    tvNumber, tvQuestion;
        RadioGroup  rgOptions;
        RadioButton rbA, rbB, rbC;

        QuestionViewHolder(@NonNull View v) {
            super(v);
            tvNumber   = v.findViewById(R.id.tv_question_number);
            tvQuestion = v.findViewById(R.id.tv_question_text);
            rgOptions  = v.findViewById(R.id.rg_options);
            rbA = v.findViewById(R.id.rb_option_a);
            rbB = v.findViewById(R.id.rb_option_b);
            rbC = v.findViewById(R.id.rb_option_c);
        }
    }
}
