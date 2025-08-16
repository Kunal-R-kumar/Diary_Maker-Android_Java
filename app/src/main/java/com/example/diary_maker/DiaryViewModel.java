package com.example.diary_maker;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.diary_maker.model.SubjectEntry;

import java.util.ArrayList;
import java.util.List;

public class DiaryViewModel extends ViewModel {

    private final MutableLiveData<List<SubjectEntry>> entries = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<SubjectEntry>> getEntries() {
        return entries;
    }

    public List<SubjectEntry> getEntriesList() {
        List<SubjectEntry> current = entries.getValue();
        return current != null ? new ArrayList<>(current) : new ArrayList<>();
    }

    public void addEntry(SubjectEntry entry) {
        List<SubjectEntry> current = getEntriesList();
        current.add(entry);
        entries.setValue(current);
    }

    public void deleteEntry(int position) {
        List<SubjectEntry> current = getEntriesList();
        if (position >= 0 && position < current.size()) {
            current.remove(position);
            entries.setValue(current);
        }
    }

    public void moveUp(int position) {
        List<SubjectEntry> current = getEntriesList();
        if (position > 0 && position < current.size()) {
            SubjectEntry temp = current.get(position);
            current.set(position, current.get(position - 1));
            current.set(position - 1, temp);
            entries.setValue(current);
        }
    }

    public void moveDown(int position) {
        List<SubjectEntry> current = getEntriesList();
        if (position >= 0 && position < current.size() - 1) {
            SubjectEntry temp = current.get(position);
            current.set(position, current.get(position + 1));
            current.set(position + 1, temp);
            entries.setValue(current);
        }
    }
}
