package com.example.studystyle.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.TypedValue;
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
    private final boolean isDarkMode;

    public QuestionAdapter(Context context, List<Question> questions) {
        this.context   = context;
        this.questions = questions;
        int nightModeFlags = context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        this.isDarkMode = (nightModeFlags == Configuration.UI_MODE_NIGHT_YES);
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

        // Set background option sesuai tema tanpa file drawable baru
        setOptionBackground(h.rbA);
        setOptionBackground(h.rbB);
        setOptionBackground(h.rbC);

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

    private void setOptionBackground(RadioButton rb) {
        float density = context.getResources().getDisplayMetrics().density;
        float radius = 10 * density;

        // Warna brand primary dari theme
        TypedValue tv = new TypedValue();
        context.getTheme().resolveAttribute(
                com.google.android.material.R.attr.colorPrimary, tv, true);
        int brandColor = tv.data;

        // State: checked
        GradientDrawable checkedBg = new GradientDrawable();
        checkedBg.setShape(GradientDrawable.RECTANGLE);
        checkedBg.setCornerRadius(radius);
        checkedBg.setColor(Color.argb(26, Color.red(brandColor),
                Color.green(brandColor), Color.blue(brandColor)));
        checkedBg.setStroke((int)(1.5f * density), brandColor);

        // State: pressed
        GradientDrawable pressedBg = new GradientDrawable();
        pressedBg.setShape(GradientDrawable.RECTANGLE);
        pressedBg.setCornerRadius(radius);
        pressedBg.setColor(Color.argb(13, Color.red(brandColor),
                Color.green(brandColor), Color.blue(brandColor)));
        pressedBg.setStroke((int)(1 * density), brandColor);

        // State: default — warna ikut tema
        int defaultBgColor = isDarkMode
                ? context.getResources().getColor(R.color.dark_input_bg, context.getTheme())
                : context.getResources().getColor(R.color.input_background, context.getTheme());
        int strokeColor = isDarkMode
                ? context.getResources().getColor(R.color.dark_divider, context.getTheme())
                : context.getResources().getColor(R.color.divider, context.getTheme());

        GradientDrawable defaultBg = new GradientDrawable();
        defaultBg.setShape(GradientDrawable.RECTANGLE);
        defaultBg.setCornerRadius(radius);
        defaultBg.setColor(defaultBgColor);
        defaultBg.setStroke((int)(1 * density), strokeColor);

        // Rakit StateListDrawable
        StateListDrawable sld = new StateListDrawable();
        sld.addState(new int[]{android.R.attr.state_checked}, checkedBg);
        sld.addState(new int[]{android.R.attr.state_pressed}, pressedBg);
        sld.addState(new int[]{}, defaultBg);

        rb.setBackground(sld);
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