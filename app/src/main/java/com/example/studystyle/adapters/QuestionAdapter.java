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
        this.context = context;
        this.questions = questions;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        Question question = questions.get(position);

        holder.tvNumber.setText(String.format("Pertanyaan %d", position + 1));
        holder.tvQuestion.setText(question.getQuestionText());
        holder.rbOptionA.setText(question.getOptionA());
        holder.rbOptionB.setText(question.getOptionB());
        holder.rbOptionC.setText(question.getOptionC());

        // Restore selection state
        holder.rgOptions.setOnCheckedChangeListener(null);
        switch (question.getSelectedOption()) {
            case 1: holder.rgOptions.check(R.id.rb_option_a); break;
            case 2: holder.rgOptions.check(R.id.rb_option_b); break;
            case 3: holder.rgOptions.check(R.id.rb_option_c); break;
            default: holder.rgOptions.clearCheck(); break;
        }

        holder.rgOptions.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_option_a) question.setSelectedOption(1);
            else if (checkedId == R.id.rb_option_b) question.setSelectedOption(2);
            else if (checkedId == R.id.rb_option_c) question.setSelectedOption(3);
            else question.setSelectedOption(0);
        });
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumber, tvQuestion;
        RadioGroup rgOptions;
        RadioButton rbOptionA, rbOptionB, rbOptionC;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.tv_question_number);
            tvQuestion = itemView.findViewById(R.id.tv_question_text);
            rgOptions = itemView.findViewById(R.id.rg_options);
            rbOptionA = itemView.findViewById(R.id.rb_option_a);
            rbOptionB = itemView.findViewById(R.id.rb_option_b);
            rbOptionC = itemView.findViewById(R.id.rb_option_c);
        }
    }
}
