package ru.usharik.ear4music;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ru.usharik.ear4music.adapter.StatisticsReportAdapter;
import ru.usharik.ear4music.service.StatisticsStorage;

public class StatisticsReportDialog extends DialogFragment {

    private static final String ARG_CORRECT_PERCENT = "correct_percent";
    private static final String ARG_AVG_TIME = "avg_time";
    private static final String ARG_SHOW_TIME = "show_time";

    private Runnable onDismissAction;
    private HashMap<NotesEnum, StatisticsStorage.Result> statistics;

    public static StatisticsReportDialog newInstance(
            int correctPercent,
            long avgAnswerTime,
            boolean showTime,
            HashMap<NotesEnum, StatisticsStorage.Result> statistics,
            Runnable onDismissAction) {
        StatisticsReportDialog fragment = new StatisticsReportDialog();
        fragment.statistics = statistics;
        fragment.onDismissAction = onDismissAction;
        fragment.setCancelable(false);

        Bundle args = new Bundle();
        args.putInt(ARG_CORRECT_PERCENT, correctPercent);
        args.putLong(ARG_AVG_TIME, avgAnswerTime);
        args.putBoolean(ARG_SHOW_TIME, showTime);
        fragment.setArguments(args);

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.statistics_report_dialog, null, false);
        dialog.setContentView(dialogView);
        dialog.setCancelable(false);

        setupDialogContent(dialogView);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }

        return dialog;
    }

    private void setupDialogContent(View dialogView) {
        TextView titleView = dialogView.findViewById(R.id.dialogTitle);
        TextView summaryView = dialogView.findViewById(R.id.dialogSummary);
        RecyclerView recyclerView = dialogView.findViewById(R.id.statisticsRecyclerView);
        Button okButton = dialogView.findViewById(R.id.okButton);

        Bundle args = getArguments();
        if (args == null) {
            dismiss();
            return;
        }

        int correctPercent = args.getInt(ARG_CORRECT_PERCENT);
        long avgTime = args.getLong(ARG_AVG_TIME);
        boolean showTime = args.getBoolean(ARG_SHOW_TIME);

        titleView.setText(R.string.statistics_report_title);

        // Set summary text
        String summary;
        if (showTime) {
            summary = String.format(Locale.getDefault(),
                    getString(R.string.statistics_report_summary_with_time),
                    correctPercent, avgTime);
        } else {
            summary = String.format(Locale.getDefault(),
                    getString(R.string.statistics_report_summary),
                    correctPercent);
        }
        summaryView.setText(summary);

        // Setup RecyclerView
        List<StatisticsReportAdapter.NoteStatistic> noteStatistics = new ArrayList<>();
        if (statistics != null) {
            for (Map.Entry<NotesEnum, StatisticsStorage.Result> entry : statistics.entrySet()) {
                noteStatistics.add(new StatisticsReportAdapter.NoteStatistic(
                        entry.getKey(), entry.getValue()));
            }
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(new StatisticsReportAdapter(noteStatistics));

        // Setup OK button
        okButton.setText(R.string.ok);
        okButton.setBackgroundResource(R.drawable.primary_button_background);
        okButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.textInverse));
        okButton.setOnClickListener(v -> {
            dismiss();
            if (onDismissAction != null) {
                onDismissAction.run();
            }
        });
    }
}

