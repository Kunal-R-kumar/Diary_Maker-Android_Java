package com.example.diary_maker.model;

public class SubjectEntry {
    public String emoji;
    public String subject;
    public String work;
    public boolean isBold, isItalic;

    public SubjectEntry(String emoji, String subject, String work) {
        this.emoji = emoji;
        this.subject = subject;
        this.work = work;
        this.isBold = true;
        this.isItalic = false;
    }
}

