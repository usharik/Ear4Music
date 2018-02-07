package com.example.macbook.ear4music.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.ToOne;

import java.io.Serializable;
import org.greenrobot.greendao.DaoException;

/**
 * Created by macbook on 17.02.2018.
 */

@Entity
public class SubTask implements Serializable {
    private static final long serialVersionUID = 123L;

    @Id(autoincrement = true)
    private Long id;

    @NotNull
    private long taskId;

    @ToOne(joinProperty = "taskId")
    private Task task;

    private Long nextSubTaskId;

    @ToOne(joinProperty = "nextSubTaskId")
    private SubTask nextSubTask;

    @NotNull
    private int notesPerMinute;

    @NotNull
    private boolean isPlayWithScale;

    @NotNull
    private boolean isWithNoteHighlighting;

    @NotNull
    private int notesInSequence;

    @NotNull
    private int sequencesInSubTask;

    @NotNull
    private int correctAnswerPercent;

    private Integer instructionId;

    @NotNull
    private boolean isFavourite;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 2076025699)
    private transient SubTaskDao myDao;

    @Generated(hash = 60176845)
    public SubTask(Long id, long taskId, Long nextSubTaskId, int notesPerMinute,
            boolean isPlayWithScale, boolean isWithNoteHighlighting, int notesInSequence,
            int sequencesInSubTask, int correctAnswerPercent, Integer instructionId,
            boolean isFavourite) {
        this.id = id;
        this.taskId = taskId;
        this.nextSubTaskId = nextSubTaskId;
        this.notesPerMinute = notesPerMinute;
        this.isPlayWithScale = isPlayWithScale;
        this.isWithNoteHighlighting = isWithNoteHighlighting;
        this.notesInSequence = notesInSequence;
        this.sequencesInSubTask = sequencesInSubTask;
        this.correctAnswerPercent = correctAnswerPercent;
        this.instructionId = instructionId;
        this.isFavourite = isFavourite;
    }

    @Generated(hash = 115995235)
    public SubTask() {
    }

    @Generated(hash = 100676365)
    private transient Long task__resolvedKey;

    @Generated(hash = 1553149230)
    private transient Long nextSubTask__resolvedKey;

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
        return isPlayWithScale;
    }

    public boolean isWithNoteHighlighting() {
        return isWithNoteHighlighting;
    }

    public void setWithNoteHighlighting(boolean withNoteHighlighting) {
        isWithNoteHighlighting = withNoteHighlighting;
    }

    public void setPlayWithScale(boolean playWithScale) {
        isPlayWithScale = playWithScale;
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

    public boolean getIsPlayWithScale() {
        return this.isPlayWithScale;
    }

    public void setIsPlayWithScale(boolean isPlayWithScale) {
        this.isPlayWithScale = isPlayWithScale;
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

    public boolean getIsWithNoteHighlighting() {
        return this.isWithNoteHighlighting;
    }

    public void setIsWithNoteHighlighting(boolean isWithNoteHighlighting) {
        this.isWithNoteHighlighting = isWithNoteHighlighting;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1810838705)
    public Task getTask() {
        long __key = this.taskId;
        if (task__resolvedKey == null || !task__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TaskDao targetDao = daoSession.getTaskDao();
            Task taskNew = targetDao.load(__key);
            synchronized (this) {
                task = taskNew;
                task__resolvedKey = __key;
            }
        }
        return task;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 254774866)
    public void setTask(@NotNull Task task) {
        if (task == null) {
            throw new DaoException(
                    "To-one property 'taskId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.task = task;
            taskId = task.getId();
            task__resolvedKey = taskId;
        }
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 985429138)
    public SubTask getNextSubTask() {
        Long __key = this.nextSubTaskId;
        if (nextSubTask__resolvedKey == null
                || !nextSubTask__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            SubTaskDao targetDao = daoSession.getSubTaskDao();
            SubTask nextSubTaskNew = targetDao.load(__key);
            synchronized (this) {
                nextSubTask = nextSubTaskNew;
                nextSubTask__resolvedKey = __key;
            }
        }
        return nextSubTask;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1079520897)
    public void setNextSubTask(SubTask nextSubTask) {
        synchronized (this) {
            this.nextSubTask = nextSubTask;
            nextSubTaskId = nextSubTask == null ? null : nextSubTask.getId();
            nextSubTask__resolvedKey = nextSubTaskId;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    public boolean getIsFavourite() {
        return this.isFavourite;
    }

    public void setIsFavourite(boolean isFavourite) {
        this.isFavourite = isFavourite;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 898204882)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getSubTaskDao() : null;
    }
}
