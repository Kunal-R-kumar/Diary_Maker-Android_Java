package com.example.diary_maker;

import com.example.diary_maker.model.SubjectEntry;
import java.util.List;

//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Locale;

public class MessageFormatter {

    public static String formatHomeworkMessage(
            String heading,
            String date,
            List<SubjectEntry> entries
    ) {
        StringBuilder builder = new StringBuilder();
        String formattedHeading = heading.trim();
        formattedHeading = "*" + formattedHeading + "*";
        builder.append(formattedHeading).append(".    ");

        // 📅 Date line
//        String dateStr = new SimpleDateFormat("EEEE, d MMMM", Locale.getDefault())
//                .format(new Date());
        builder.append(" *").append(date).append("*\n\n");

        // Loop over entries
        for (SubjectEntry entry : entries) {
            String emoji = entry.emoji != null ? entry.emoji : "📙";
            String subject = entry.subject != null ? entry.subject : "Subject";
            String work = entry.work != null ? entry.work : "";
            String formatted=subject.trim();
            builder.append(emoji).append(" ");
            if (entry.isBold){
                formatted="*"+formatted+"*";
                    ;}
            if (entry.isItalic) {
                formatted = "_" + formatted + "_";
            }
           builder.append(formatted).append(" - ")
                   .append(work.trim()).append("\n");
        }

        return builder.toString().trim();
    }
}
