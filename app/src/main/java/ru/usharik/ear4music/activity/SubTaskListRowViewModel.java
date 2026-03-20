package ru.usharik.ear4music.activity;

import ru.usharik.ear4music.framework.ViewModelObservable;
import ru.usharik.ear4music.model.SubTask;
import ru.usharik.ear4music.model.Task;
import ru.usharik.ear4music.repository.SubTaskRepository;

/**
 * Created by macbook on 18.02.2018.
 */

public class SubTaskListRowViewModel extends ViewModelObservable {
    private final SubTask subTask;
    private final String subTaskDescription;
    private final Task task;
    private final SubTaskRepository subTaskRepository;

    public SubTaskListRowViewModel(final SubTask subTask,
                                   final String subTaskDescription,
                                   final Task task,
                                   final SubTaskRepository subTaskRepository) {
        this.subTask = subTask;
        this.subTaskDescription = subTaskDescription;
        this.task = task;
        this.subTaskRepository = subTaskRepository;
    }

    public String getNotesPerMinute() {
        return Integer.toString(subTask.getNotesPerMinute());
    }

    public int getCorrectAnswerPercent() {
        return subTask.getCorrectAnswerPercent();
    }

    public String getSubTaskDescription() {
        return subTaskDescription;
    }

    public boolean isFavourite() {
        return subTask.isFavourite();
    }

    public void setFavourite(boolean favourite) {
        subTask.setFavourite(favourite);
        subTaskRepository.update(subTask);
    }

    public int getSetOfNotesId() {
        if (task == null) {
            return 0;
        }
        return subTask.isWithNoteHighlighting() ? task.getSetOfNotesHighlightingId() : task.getSetOfNotesId();
    }
}
