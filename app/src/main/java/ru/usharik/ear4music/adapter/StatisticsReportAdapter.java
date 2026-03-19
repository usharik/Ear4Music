package ru.usharik.ear4music.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import ru.usharik.ear4music.NotesEnum;
import ru.usharik.ear4music.R;
import ru.usharik.ear4music.service.StatisticsStorage;

public class StatisticsReportAdapter extends RecyclerView.Adapter<StatisticsReportAdapter.ViewHolder> {

    public static class NoteStatistic {
        public final NotesEnum note;
        public final StatisticsStorage.Result result;

        public NoteStatistic(NotesEnum note, StatisticsStorage.Result result) {
            this.note = note;
            this.result = result;
        }
    }

    private final List<NoteStatistic> statistics;

    public StatisticsReportAdapter(List<NoteStatistic> statistics) {
        this.statistics = statistics;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.statistics_report_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NoteStatistic stat = statistics.get(position);
        holder.noteName.setText(stat.note.name());
        
        String noteStatFormat = holder.itemView.getContext().getString(R.string.note_stat_format);
        String formattedStats = String.format(Locale.getDefault(), noteStatFormat,
                stat.result.correct, stat.result.wrong, stat.result.missed);
        
        holder.correctCount.setText(String.format(Locale.getDefault(), "✓ %d", stat.result.correct));
        holder.wrongCount.setText(String.format(Locale.getDefault(), "✗ %d", stat.result.wrong));
        holder.missedCount.setText(String.format(Locale.getDefault(), "− %d", stat.result.missed));
    }

    @Override
    public int getItemCount() {
        return statistics.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView noteName;
        final TextView correctCount;
        final TextView wrongCount;
        final TextView missedCount;

        ViewHolder(View view) {
            super(view);
            noteName = view.findViewById(R.id.noteName);
            correctCount = view.findViewById(R.id.correctCount);
            wrongCount = view.findViewById(R.id.wrongCount);
            missedCount = view.findViewById(R.id.missedCount);
        }
    }
}

