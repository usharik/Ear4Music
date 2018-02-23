package com.example.macbook.ear4music;

import android.databinding.Bindable;

import com.example.macbook.ear4music.framework.ViewModelObservable;
import com.example.macbook.ear4music.model.SubTask;
import com.example.macbook.ear4music.model.SubTaskDao;
import com.example.macbook.ear4music.service.AppState;
import com.example.macbook.ear4music.service.DbService;
import com.example.macbook.ear4music.service.MelodyNoteGenerator;
import com.example.macbook.ear4music.service.NoteGenerator;
import com.example.macbook.ear4music.service.RandomNoteGenerator;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by macbook on 04.02.18.
 */

public class ExecuteTaskViewModel extends ViewModelObservable {
    private long taskId;
    private long subTaskId;
    private int setOfNotesId = 0;
    private int setOfNotesHighlightingId = 0;
    private int notesPerMinute = 0;
    private boolean isPlayWithScale = false;
    private boolean isWithNoteHighlighting = false;
    private int notesInSequence = 0;
    private int sequencesInSubTask = 0;
    private int correctPercent = 0;
    private boolean isFavourite;
    private Integer instructionId;
    private boolean showNoteNames = true;
    private boolean isStarted = false;

    private SubTask subTask;

    private final DbService dbService;
    private final AppState appState;
    private StatisticsStorage statisticsStorage;

    @Inject
    ExecuteTaskViewModel(final DbService dbService,
                         final AppState appState) {
        this.dbService = dbService;
        this.appState = appState;
    }

    public void syncWithAppState() {
        setSubTask(appState.getSubTask());
    }

    public void updateAppState() {
        appState.setTask(subTask.getTask());
        appState.setSubTask(subTask);
    }

    public Observable<NoteInfo[]> createNotesEmitterObservable(final List<NotesEnum> melody) {
        final int longitude = (int) Math.round(60000.0 / notesPerMinute);

        return Observable.create((e) -> {
            long noteNumber = 0;
            NoteGenerator noteGenerator = isWithNoteHighlighting ? new MelodyNoteGenerator(melody) : new RandomNoteGenerator(melody, sequencesInSubTask * notesInSequence);
            while (!e.isDisposed() && noteGenerator.hasNextNote()) {
                NoteInfo[] notes = new NoteInfo[notesInSequence];
                for (int i = 0; i < notesInSequence; i++) {
                    notes[i] = new NoteInfo(noteNumber++,
                            noteGenerator.nextNote(),
                            null,
                            isPlayWithScale,
                            isWithNoteHighlighting,
                            longitude);
                }
                e.onNext(notes);
            }
            if (!e.isDisposed()) {
                e.onComplete();
            }
        });
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public long getSubTaskId() {
        return subTaskId;
    }

    public void setSubTaskId(long subTaskId) {
        this.subTaskId = subTaskId;
        SubTask subTask = dbService.getDaoSession().queryBuilder(SubTask.class)
                .where(SubTaskDao.Properties.Id.eq(this.subTaskId))
                .list().get(0);
        setSubTask(subTask);
    }

    @Bindable
    public int getSetOfNotesId() {
        return setOfNotesId;
    }

    public void setSetOfNotesId(int setOfNotesId) {
        this.setOfNotesId = setOfNotesId;
        notifyPropertyChanged(BR.setOfNotesId);
    }

    public int getSetOfNotesHighlightingId() {
        return setOfNotesHighlightingId;
    }

    public void setSetOfNotesHighlightingId(int setOfNotesHighlightingId) {
        this.setOfNotesHighlightingId = setOfNotesHighlightingId;
    }

    @Bindable
    public int getNotesPerMinute() {
        return notesPerMinute;
    }

    @Bindable
    public void setNotesPerMinute(int notesPerMinute) {
        this.notesPerMinute = notesPerMinute;
        notifyPropertyChanged(BR.notesPerMinute);
    }

    @Bindable
    public boolean isPlayWithScale() {
        return isPlayWithScale;
    }

    public void setPlayWithScale(boolean playWithScale) {
        isPlayWithScale = playWithScale;
    }

    @Bindable
    public int getNotesInSequence() {
        return notesInSequence;
    }

    @Bindable
    public void setNotesInSequence(int notesInSequence) {
        this.notesInSequence = notesInSequence;
        notifyPropertyChanged(BR.notesInSequence);
    }

    public int getSequencesInSubTask() {
        return sequencesInSubTask;
    }

    public void setSequencesInSubTask(int sequencesInSubTask) {
        this.sequencesInSubTask = sequencesInSubTask;
    }

    public int getCorrectAnswerPercent() {
        return correctPercent;
    }

    public void setCorrectAnswerPercent(int correctAnswerPercent) {
        this.correctPercent = correctAnswerPercent;
        SubTask subTask = dbService.getDaoSession().queryBuilder(SubTask.class)
                .where(SubTaskDao.Properties.Id.eq(subTaskId))
                .list().get(0);
        subTask.setCorrectAnswerPercent(correctAnswerPercent);
        dbService.getDaoSession().update(subTask);
        dbService.updateTaskDonePercent(subTask.getTask());
    }

    @Bindable
    public boolean isFavourite() {
        return isFavourite;
    }

    @Bindable
    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
        notifyPropertyChanged(BR.showNoteNames);
        subTask.setFavourite(favourite);
        appState.getSubTask().setFavourite(favourite);
        dbService.getDaoSession().update(subTask);
    }

    public Integer getInstructionId() {
        return instructionId;
    }

    public void setInstructionId(Integer instructionId) {
        this.instructionId = instructionId;
    }

    @Bindable
    public boolean isShowNoteNames() {
        return showNoteNames;
    }

    @Bindable
    public void setShowNoteNames(boolean showNoteNames) {
        this.showNoteNames = showNoteNames;
        notifyPropertyChanged(BR.showNoteNames);
    }

    public boolean isWithNoteHighlighting() {
        return isWithNoteHighlighting;
    }

    public void setWithNoteHighlighting(boolean withNoteHighlighting) {
        isWithNoteHighlighting = withNoteHighlighting;
    }

    @Bindable
    public boolean isStarted() {
        return isStarted;
    }

    @Bindable
    public void setStarted(boolean started) {
        isStarted = started;
        notifyPropertyChanged(BR.started);
    }

    public SubTask getSubTask() {
        return subTask;
    }

    public void setSubTask(SubTask subTask) {
        this.subTask = subTask;
        this.subTaskId = subTask.getId();
        updateAppState();

        setTaskId(subTask.getTask().getId());
        setSetOfNotesId(subTask.getTask().getSetOfNotesId());
        setSetOfNotesHighlightingId(subTask.getTask().getSetOfNotesHighlightingId());

        setNotesPerMinute(subTask.getNotesPerMinute());
        setPlayWithScale(subTask.isPlayWithScale());
        setNotesInSequence(subTask.getNotesInSequence());
        setSequencesInSubTask(subTask.getSequencesInSubTask());
        setWithNoteHighlighting(subTask.isWithNoteHighlighting());
        setInstructionId(subTask.getInstructionId());
        setFavourite(subTask.isFavourite());
    }

    public StatisticsStorage getStatisticsStorage() {
        return statisticsStorage;
    }

    public void resetStatisticsStorage() {
        statisticsStorage = new StatisticsStorage();
    }
}
