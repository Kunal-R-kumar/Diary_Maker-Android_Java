package com.example.diary_maker.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diary_maker.R;
import com.example.diary_maker.model.SubjectEntry;
import com.example.diary_maker.DiaryViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.ViewHolder> {

    private final Context context;
    private final DiaryViewModel viewModel;
    private List<SubjectEntry> entries;

    private final List<String> subjects = new ArrayList<>(Arrays.asList(
            "Maths", "हिंदी", "English", "EVS", "Other"
    ));

    private final List<String> emojis = new ArrayList<>(Arrays.asList(
            "📘", "📗", "📙", "📓", "📒", "📕", "📔","\uD83C\uDFA8", "Other"
    ));

    public DiaryAdapter(Context context, DiaryViewModel viewModel, List<SubjectEntry> entries) {
        this.context = context;
        this.viewModel = viewModel;
        this.entries = entries != null ? entries : new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_subject, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SubjectEntry entry = entries.get(position);

        if (holder.textWatcher != null) {
            holder.etDiary.removeTextChangedListener(holder.textWatcher);
        }

        holder.etDiary.setText(entry.work);
        holder.etDiary.setMovementMethod(new ScrollingMovementMethod());
        holder.etDiary.setVerticalScrollBarEnabled(true);
        holder.etDiary.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

        holder.textWatcher = new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                entry.work = s.toString();
            }
        };

        holder.etDiary.addTextChangedListener(holder.textWatcher);

        if (position == entries.size() - 1) {
            holder.etDiary.requestFocus();
        }

        holder.btnB.setChecked(entry.isBold);
        holder.btnI.setChecked(entry.isItalic);

        ArrayAdapter<String> emojiAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, emojis);
        emojiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.emojiSpinner.setAdapter(emojiAdapter);

        if (emojis.contains(entry.emoji)) {
            holder.emojiSpinner.setSelection(emojis.indexOf(entry.emoji));
        } else {
            holder.emojiSpinner.setSelection(emojis.size() - 1);
        }

        holder.emojiSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String selected = emojis.get(pos);
                if (selected.equals("Other")) {
                    promptCustomInput("Enter emoji", value -> {
                        int otherIndex = emojis.size() - 1;
                        emojis.add(otherIndex, value);
                        entry.emoji = value;
                        notifyItemChanged(holder.getAdapterPosition());
                    });
                } else {
                    entry.emoji = selected;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, subjects);
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.subjectSpinner.setAdapter(subjectAdapter);

        if (subjects.contains(entry.subject)) {
            holder.subjectSpinner.setSelection(subjects.indexOf(entry.subject));
        } else {
            holder.subjectSpinner.setSelection(subjects.size() - 1);
        }

        holder.subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String selected = subjects.get(pos);
                if (selected.equals("Other")) {
                    promptCustomInput("Enter subject", value -> {
                        int otherIndex = subjects.size() - 1;
                        subjects.add(otherIndex, value);
                        entry.subject = value;
                        notifyItemChanged(holder.getAdapterPosition());
                    });
                } else {
                    entry.subject = selected;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        holder.btnDelete.setOnClickListener(v -> viewModel.deleteEntry(holder.getAdapterPosition()));
        holder.btnUp.setOnClickListener(v -> viewModel.moveUp(holder.getAdapterPosition()));
        holder.btnDown.setOnClickListener(v -> viewModel.moveDown(holder.getAdapterPosition()));

        holder.btnB.setOnClickListener(v -> entry.isBold = holder.btnB.isChecked());
        holder.btnI.setOnClickListener(v -> entry.isItalic = holder.btnI.isChecked());
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public void updateEntries(List<SubjectEntry> newEntries) {
        this.entries = newEntries != null ? newEntries : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void requestFocusOnLast() {
        notifyItemChanged(entries.size() - 1);
    }

    private void promptCustomInput(String title, OnCustomInputListener listener) {
        EditText input = new EditText(context);
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(input)
                .setPositiveButton("OK", (dialog, which) -> {
                    String value = input.getText().toString().trim();
                    if (!value.isEmpty()) {
                        listener.onInput(value);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    interface OnCustomInputListener {
        void onInput(String value);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        Spinner emojiSpinner, subjectSpinner;
        ToggleButton btnB, btnI;
        EditText etDiary;
        ImageButton btnDelete, btnUp, btnDown;

        TextWatcher textWatcher;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            emojiSpinner = itemView.findViewById(R.id.emoji);
            subjectSpinner = itemView.findViewById(R.id.subject_name);
            btnB = itemView.findViewById(R.id.btn_b);
            btnI = itemView.findViewById(R.id.btn_i);
            etDiary = itemView.findViewById(R.id.et_diary);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            btnUp = itemView.findViewById(R.id.btn_up);
            btnDown = itemView.findViewById(R.id.btn_down);
        }
    }

    abstract static class SimpleTextWatcher implements TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void afterTextChanged(Editable s) {}
    }
}
