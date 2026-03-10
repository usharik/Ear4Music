package com.example.macbook.ear4music.model;

import java.io.Serializable;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.ForeignKey;
import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

/**
 * Created by macbook on 17.02.2018.
 */

@Entity(tableName = "sub_task",
        foreignKeys = @ForeignKey(entity = Task.class, parentColumns = "id", childColumns = "taskId", onDelete = ForeignKey.CASCADE))
public class SubTask implements Serializable {
    private static final long serialVersionUID = 123L;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private Long id;

    @ColumnInfo(name = "taskId", index = true)
    private long taskId;

    // Relation will be loaded by DbService/Room DAOs when needed
    @Ignore
    private Task task;

    private Long nextSubTaskId;

    @Ignore
    private SubTask nextSubTask;

    private int notesPerMinute;

    private boolean playWithScale;

    private boolean withNoteHighlighting;

    private int notesInSequence;

    private int sequencesInSubTask;

    private int correctAnswerPercent;

    private Integer instructionId;

    @ColumnInfo(name = "isFavourite")
    private boolean isFavourite;

    public SubTask(Long id, long taskId, Long nextSubTaskId, int notesPerMinute,
                   boolean playWithScale, boolean withNoteHighlighting, int notesInSequence,
                   int sequencesInSubTask, int correctAnswerPercent, Integer instructionId,
                   boolean isFavourite) {
        this.id = id;
        this.taskId = taskId;
        this.nextSubTaskId = nextSubTaskId;
        this.notesPerMinute = notesPerMinute;
        this.playWithScale = playWithScale;
        this.withNoteHighlighting = withNoteHighlighting;
        this.notesInSequence = notesInSequence;
        this.sequencesInSubTask = sequencesInSubTask;
        this.correctAnswerPercent = correctAnswerPercent;
        this.instructionId = instructionId;
        this.isFavourite = isFavourite;
    }

    public SubTask() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public Long getNextSubTaskId() {
        return nextSubTaskId;
    }

    public void setNextSubTaskId(Long nextSubTaskId) {
        this.nextSubTaskId = nextSubTaskId;
    }

    public int getNotesPerMinute() {
        return notesPerMinute;
    }

    public void setNotesPerMinute(int notesPerMinute) {
        this.notesPerMinute = notesPerMinute;
    }

    public boolean isPlayWithScale() {
        return playWithScale;
    }

    public void setPlayWithScale(boolean playWithScale) {
        this.playWithScale = playWithScale;
    }

    public boolean isWithNoteHighlighting() {
        return withNoteHighlighting;
    }

    public void setWithNoteHighlighting(boolean withNoteHighlighting) {
        this.withNoteHighlighting = withNoteHighlighting;
    }

    public int getNotesInSequence() {
        return notesInSequence;
    }

    public void setNotesInSequence(int notesInSequence) {
        this.notesInSequence = notesInSequence;
    }

    public int getCorrectAnswerPercent() {
        return correctAnswerPercent;
    }

    public void setCorrectAnswerPercent(int correctAnswerPercent) {
        this.correctAnswerPercent = correctAnswerPercent;
    }

    public int getSequencesInSubTask() {
        return sequencesInSubTask;
    }

    public void setSequencesInSubTask(int sequencesInSubTask) {
        this.sequencesInSubTask = sequencesInSubTask;
    }

    public Integer getInstructionId() {
        return instructionId;
    }

    public void setInstructionId(Integer instructionId) {
        this.instructionId = instructionId;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
        if (task != null && task.getId() != null) {
            this.taskId = task.getId();
        }
    }

    public SubTask getNextSubTask() {
        return nextSubTask;
    }

    public void setNextSubTask(SubTask nextSubTask) {
        this.nextSubTask = nextSubTask;
        this.nextSubTaskId = nextSubTask == null ? null : nextSubTask.getId();
    }
}
