package ru.usharik.ear4music.activity;

import androidx.databinding.Bindable;

import ru.usharik.ear4music.BR;
import ru.usharik.ear4music.NoteInfo;
import ru.usharik.ear4music.NotesEnum;
import ru.usharik.ear4music.framework.ViewModelObservable;
import ru.usharik.ear4music.model.SubTask;
import ru.usharik.ear4music.model.Task;
import ru.usharik.ear4music.model.room.SubTaskWithTask;
import ru.usharik.ear4music.repository.SubTaskRepository;
import ru.usharik.ear4music.repository.TaskRepository;
import ru.usharik.ear4music.service.AppState;
import ru.usharik.ear4music.service.NoteSequenceEmitter;
import ru.usharik.ear4music.service.StatisticsStorage;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Observable;

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
    private boolean isFavourite;
    private Integer instructionId;
    private boolean showNoteNames = true;
    private boolean isStarted = false;

    private SubTask subTask;
    private Task task;

    private final TaskRepository taskRepository;
    private final SubTaskRepository subTaskRepository;
    private final AppState appState;
    private final StatisticsStorage statisticsStorage;

    @Inject
    public ExecuteTaskViewModel(final TaskRepository taskRepository,
                         final SubTaskRepository subTaskRepository,
                         final AppState appState,
                         final StatisticsStorage statisticsStorage) {
        this.taskRepository = taskRepository;
        this.subTaskRepository = subTaskRepository;
        this.appState = appState;
        this.statisticsStorage = statisticsStorage;
    }

    public boolean syncWithSubTaskId(long subTaskId) {
        SubTask appStateSubTask = appState.getSubTask();
        if (appStateSubTask != null && appStateSubTask.getId() != null
                && (subTaskId <= 0L || appStateSubTask.getId() == subTaskId)) {
            Task appStateTask = resolveTask(appStateSubTask, appState.getTask());
            if (appStateTask != null) {
                setSubTask(appStateSubTask, appStateTask);
                return true;
            }
        }

        if (subTaskId > 0L) {
            SubTaskWithTask subTaskWithTask = subTaskRepository.findWithTaskById(subTaskId);
            if (subTaskWithTask != null && subTaskWithTask.subTask != null && subTaskWithTask.task != null) {
                setSubTask(subTaskWithTask.subTask, subTaskWithTask.task);
                return true;
            }
        }

        clearSubTask();
        return false;
    }

    private void updateAppState() {
        appState.setTask(task);
        appState.setSubTask(subTask);
    }

    public Observable<NoteInfo[]> createNotesEmitterObservable(final List<NotesEnum> melody) {
        return new NoteSequenceEmitter(
                notesPerMinute,
                notesInSequence,
                sequencesInSubTask,
                isPlayWithScale,
                isWithNoteHighlighting
        ).createObservable(melody);
    }

    public long getTaskId() {
        return taskId;
    }

    private void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public long getSubTaskId() {
        return subTaskId;
    }

    @Bindable
    public int getSetOfNotesId() {
        return setOfNotesId;
    }

    private void setSetOfNotesId(int setOfNotesId) {
        this.setOfNotesId = setOfNotesId;
        notifyPropertyChanged(BR.setOfNotesId);
    }

    public int getSetOfNotesHighlightingId() {
        return setOfNotesHighlightingId;
    }

    private void setSetOfNotesHighlightingId(int setOfNotesHighlightingId) {
        this.setOfNotesHighlightingId = setOfNotesHighlightingId;
    }

    @Bindable
    public int getNotesPerMinute() {
        return notesPerMinute;
    }

    @Bindable
    private void setNotesPerMinute(int notesPerMinute) {
        this.notesPerMinute = notesPerMinute;
        notifyPropertyChanged(BR.notesPerMinute);
    }

    private void setPlayWithScale(boolean playWithScale) {
        isPlayWithScale = playWithScale;
    }

    @Bindable
    public int getNotesInSequence() {
        return notesInSequence;
    }

    @Bindable
    private void setNotesInSequence(int notesInSequence) {
        this.notesInSequence = notesInSequence;
        notifyPropertyChanged(BR.notesInSequence);
    }

    public int getSequencesInSubTask() {
        return sequencesInSubTask;
    }

    private void setSequencesInSubTask(int sequencesInSubTask) {
        this.sequencesInSubTask = sequencesInSubTask;
    }

    public void setCorrectAnswerPercent(int correctAnswerPercent) {
        SubTask subTask = subTaskRepository.findById(subTaskId);
        if (subTask == null) {
            return;
        }
        subTask.setCorrectAnswerPercent(correctAnswerPercent);
        subTaskRepository.update(subTask);
        if (task != null) {
            taskRepository.updateDonePercent(task);
        }
    }

    @Bindable
    public boolean isFavourite() {
        return isFavourite;
    }

    @Bindable
    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
        notifyPropertyChanged(BR.showNoteNames);
        if (subTask == null) {
            return;
        }
        subTask.setFavourite(favourite);
        SubTask appStateSubTask = appState.getSubTask();
        if (appStateSubTask != null) {
            appStateSubTask.setFavourite(favourite);
        }
        subTaskRepository.update(subTask);
    }

    public Integer getInstructionId() {
        return instructionId;
    }

    private void setInstructionId(Integer instructionId) {
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

    private void setWithNoteHighlighting(boolean withNoteHighlighting) {
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

    private Task resolveTask(SubTask subTask, Task candidateTask) {
        if (subTask == null || subTask.getTaskId() <= 0L) {
            return null;
        }
        if (candidateTask != null && candidateTask.getId() != null
                && candidateTask.getId().longValue() == subTask.getTaskId()) {
            return candidateTask;
        }
        return taskRepository.findById(subTask.getTaskId());
    }

    private void setSubTask(SubTask subTask, Task task) {
        if (subTask == null || subTask.getId() == null || task == null || task.getId() == null) {
            clearSubTask();
            return;
        }
        this.subTask = subTask;
        this.task = task;
        this.subTaskId = subTask.getId();
        updateAppState();

        setTaskId(task.getId());
        setSetOfNotesId(task.getSetOfNotesId());
        setSetOfNotesHighlightingId(task.getSetOfNotesHighlightingId());

        setNotesPerMinute(subTask.getNotesPerMinute());
        setPlayWithScale(subTask.isPlayWithScale());
        setNotesInSequence(subTask.getNotesInSequence());
        setSequencesInSubTask(subTask.getSequencesInSubTask());
        setWithNoteHighlighting(subTask.isWithNoteHighlighting());
        setInstructionId(subTask.getInstructionId());
        setFavourite(subTask.isFavourite());
    }

    private void clearSubTask() {
        this.subTask = null;
        this.task = null;
        this.taskId = 0L;
        this.subTaskId = 0L;
        appState.setTask(null);
        appState.setSubTask(null);
    }

    public StatisticsStorage getStatisticsStorage() {
        return statisticsStorage;
    }
}
