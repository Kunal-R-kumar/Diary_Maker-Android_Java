package com.example.diary_maker;

import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diary_maker.adapter.DiaryAdapter;
import com.example.diary_maker.model.SubjectEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DiaryViewModel viewModel;
    private DiaryAdapter adapter;
    private RecyclerView recyclerView;
    private final String[] emojisArray = {
            "📘", "📗", "📙", "📓", "📒", "📕", "📔", "Other"
    };

    int a = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText edtHeading = findViewById(R.id.heading);
        recyclerView = findViewById(R.id.recyclerView);
        FloatingActionButton fab = findViewById(R.id.floatingActionButton2);
        TextView dateText = findViewById(R.id.date);
        ImageButton btnSelectDate = findViewById(R.id.btn_calendar);
        ImageButton btnCopy = findViewById(R.id.btn_copy);
        ImageButton btnWp = findViewById(R.id.btn_whatsapp);

        edtHeading.setText("\uD83C\uDF1F  Today's Homework");

        viewModel = new ViewModelProvider(this).get(DiaryViewModel.class);

        adapter = new DiaryAdapter(this, viewModel, new ArrayList<>());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        viewModel.getEntries().observe(this, entries -> adapter.updateEntries(entries));

        fab.setOnClickListener(v -> {
            syncAllEntries();
            SubjectEntry entry = new SubjectEntry(emojisArray[a % 7], "Maths", "");
            a++;
            viewModel.addEntry(entry);

            recyclerView.postDelayed(() -> {
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                adapter.requestFocusOnLast();
            }, 100);
        });

        btnSelectDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    MainActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        setDateText(dateText, selectedYear, selectedMonth, selectedDay);
                    },
                    year, month, day
            );

            datePickerDialog.show();
        });

        Calendar today = Calendar.getInstance();
        setDateText(dateText,
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH));

        btnCopy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Homework", messageMaker(edtHeading,dateText));
            clipboard.setPrimaryClip(clip);
        });

        btnWp.setOnClickListener(v -> {
            shareToWhatsApp(messageMaker(edtHeading,dateText));
        });
    }

    private String messageMaker(EditText edtHeading, TextView dateText ) {
        syncAllEntries();
        String heading = edtHeading.getText().toString().trim();
        List<SubjectEntry> entries = viewModel.getEntriesList();
        String dt = dateText.getText().toString();
        return MessageFormatter.formatHomeworkMessage(
                heading,
                dt,
                entries
        );
    }

    private void syncAllEntries() {
        recyclerView.clearFocus();
    }

    private void shareToWhatsApp(String message) {
        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, message);
            sendIntent.setType("text/plain");
            sendIntent.setPackage("com.whatsapp");

            startActivity(sendIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDateText(TextView textView, int year, int month, int day) {
        String[] daysOfWeek = {
                "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
        };
        String[] months = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };

        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);

        String dayOfWeek = daysOfWeek[cal.get(Calendar.DAY_OF_WEEK) - 1];
        String monthName = months[month];

        String dateText = dayOfWeek + ", " + day + " " + monthName;
        textView.setText(dateText);
    }
}
