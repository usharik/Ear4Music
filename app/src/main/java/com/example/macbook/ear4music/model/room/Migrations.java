package com.example.macbook.ear4music.model.room;

import androidx.annotation.NonNull;
import androidx.sqlite.db.SupportSQLiteDatabase;

public final class Migrations {
    // <string-array name="task_name">
    private static final int LEARN_C_E_G_C2 = 0;
    private static final int LEARN_B = 1;
    private static final int LEARN_F = 2;
    private static final int LEARN_A = 3;
    private static final int LEARN_D = 4;

    // <string-array name="task_instruction">
    private static final int LEARN_INTONATION_C_E_G_C2 = 0;
    private static final int LEARN_INTONATION_B = 1;
    private static final int LEARN_INTONATION_F = 2;
    private static final int LEARN_INTONATION_A = 3;
    private static final int LEARN_INTONATION_D = 4;
    private static final int LEARN_INTONATION_2 = 5;
    private static final int LEARN_NOTES = 6;
    private static final int LEARN_2_NOTE_SERIES = 7;
    private static final int LEARN_3_NOTE_SERIES = 8;
    private static final int LEARN_4_NOTE_SERIES = 9;

    private static final String INSERT_TASK_SQL =
            "INSERT INTO `task` (`id`,`nameId`,`setOfNotesId`,`setOfNotesHighlightingId`,`donePercent`) VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_SUB_TASK_SQL =
            "INSERT INTO `sub_task` (`id`,`taskId`,`nextSubTaskId`,`notesPerMinute`,`playWithScale`,`withNoteHighlighting`,`notesInSequence`,`sequencesInSubTask`,`correctAnswerPercent`,`instructionId`,`isFavourite`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_NEXT_SUB_TASK_SQL =
            "UPDATE `sub_task` SET `nextSubTaskId` = ? WHERE `id` = ?";

    private static final TaskSeed[] TASK_SEEDS = {
            task(LEARN_C_E_G_C2, 0, 0),
            task(LEARN_B, 1, 5),
            task(LEARN_F, 2, 6),
            task(LEARN_A, 3, 7),
            task(LEARN_D, 4, 8),
    };

    private static final SubTaskSeed[] TASK_1_SUB_TASKS = {
            seed(15, true, true, 1, 4, LEARN_INTONATION_C_E_G_C2),
            seed(15, true, false, 1, 10, LEARN_INTONATION_2),
            seed(20, true, false, 1, 10, null),
            seed(25, true, false, 1, 13, null),
            seed(30, true, false, 1, 16, null),
            seed(35, true, false, 1, 18, null),
            seed(40, true, false, 1, 20, null),
            seed(45, true, false, 1, 23, null),
            seed(50, true, false, 1, 26, null),
            seed(15, false, false, 1, 8, LEARN_NOTES),
            seed(20, false, false, 1, 10, null),
            seed(25, false, false, 1, 13, null),
            seed(30, false, false, 1, 16, null),
            seed(35, false, false, 1, 20, null),
            seed(40, false, false, 1, 21, null),
            seed(45, false, false, 1, 27, null),
            seed(50, false, false, 1, 28, null),
            seed(55, false, false, 1, 32, null),
            seed(60, false, false, 1, 32, null),
            seed(65, false, false, 2, 17, LEARN_2_NOTE_SERIES),
            seed(65, false, false, 3, 12, LEARN_3_NOTE_SERIES),
            seed(65, false, false, 4, 9, LEARN_4_NOTE_SERIES),
    };

    private static final SubTaskSeed[] TASK_2_SUB_TASKS = {
            seed(15, true, true, 1, 1, LEARN_INTONATION_B),
            seed(15, true, false, 1, 10, LEARN_INTONATION_2),
            seed(20, true, false, 1, 12, null),
            seed(25, true, false, 1, 15, null),
            seed(30, true, false, 1, 18, null),
            seed(35, true, false, 1, 21, null),
            seed(40, true, false, 1, 24, null),
            seed(45, true, false, 1, 27, null),
            seed(50, true, false, 1, 30, null),
            seed(20, false, false, 1, 12, LEARN_NOTES),
            seed(25, false, false, 1, 15, null),
            seed(30, false, false, 1, 19, null),
            seed(35, false, false, 1, 21, null),
            seed(40, false, false, 1, 25, null),
            seed(45, false, false, 1, 28, null),
            seed(50, false, false, 1, 31, null),
            seed(55, false, false, 1, 33, null),
            seed(60, false, false, 1, 37, null),
            seed(65, false, false, 1, 42, null),
            seed(70, false, false, 2, 19, LEARN_2_NOTE_SERIES),
            seed(70, false, false, 3, 14, LEARN_3_NOTE_SERIES),
            seed(70, false, false, 4, 12, LEARN_4_NOTE_SERIES),
    };

    private static final SubTaskSeed[] TASK_3_SUB_TASKS = {
            seed(15, true, true, 1, 1, LEARN_INTONATION_F),
            seed(15, true, false, 1, 11, LEARN_INTONATION_2),
            seed(20, true, false, 1, 15, null),
            seed(25, true, false, 1, 17, null),
            seed(30, true, false, 1, 21, null),
            seed(35, true, false, 1, 25, null),
            seed(40, true, false, 1, 28, null),
            seed(45, true, false, 1, 31, null),
            seed(50, true, false, 1, 35, null),
            seed(25, false, false, 1, 20, LEARN_NOTES),
            seed(30, false, false, 1, 21, null),
            seed(35, false, false, 1, 25, null),
            seed(40, false, false, 1, 28, null),
            seed(45, false, false, 1, 31, null),
            seed(50, false, false, 1, 35, null),
            seed(55, false, false, 1, 37, null),
            seed(60, false, false, 1, 42, null),
            seed(65, false, false, 1, 45, null),
            seed(70, false, false, 1, 50, null),
            seed(75, false, false, 1, 55, null),
            seed(75, false, false, 1, 60, null),
            seed(75, false, false, 1, 65, null),
    };

    private static final SubTaskSeed[] TASK_4_SUB_TASKS = {
            seed(15, true, true, 1, 1, LEARN_INTONATION_A),
            seed(15, true, false, 1, 13, LEARN_INTONATION_2),
            seed(20, true, false, 1, 17, null),
            seed(25, true, false, 1, 19, null),
            seed(30, true, false, 1, 22, null),
            seed(35, true, false, 1, 26, null),
            seed(40, true, false, 1, 29, null),
            seed(45, true, false, 1, 33, null),
            seed(50, true, false, 1, 37, null),
            seed(30, false, false, 1, 26, LEARN_NOTES),
            seed(35, false, false, 1, 28, null),
            seed(40, false, false, 1, 29, null),
            seed(45, false, false, 1, 32, null),
            seed(50, false, false, 1, 37, null),
            seed(55, false, false, 1, 40, null),
            seed(60, false, false, 1, 44, null),
            seed(65, false, false, 1, 47, null),
            seed(70, false, false, 1, 55, null),
            seed(75, false, false, 1, 60, null),
            seed(80, false, false, 2, 21, LEARN_2_NOTE_SERIES),
            seed(80, false, false, 3, 16, LEARN_3_NOTE_SERIES),
            seed(80, false, false, 4, 14, LEARN_4_NOTE_SERIES),
    };

    private static final SubTaskSeed[] TASK_5_SUB_TASKS = {
            seed(15, true, true, 1, 1, LEARN_INTONATION_D),
            seed(15, true, false, 1, 13, LEARN_INTONATION_2),
            seed(20, true, false, 1, 17, null),
            seed(25, true, false, 1, 19, null),
            seed(30, true, false, 1, 22, null),
            seed(35, true, false, 1, 26, null),
            seed(40, true, false, 1, 29, null),
            seed(45, true, false, 1, 33, null),
            seed(50, true, false, 1, 37, null),
            seed(35, false, false, 1, 30, LEARN_NOTES),
            seed(40, false, false, 1, 23, null),
            seed(45, false, false, 1, 34, null),
            seed(50, false, false, 1, 39, null),
            seed(55, false, false, 1, 42, null),
            seed(60, false, false, 1, 46, null),
            seed(65, false, false, 1, 49, null),
            seed(70, false, false, 1, 57, null),
            seed(75, false, false, 1, 65, null),
            seed(80, false, false, 1, 70, null),
            seed(85, false, false, 2, 25, LEARN_2_NOTE_SERIES),
            seed(85, false, false, 3, 20, LEARN_3_NOTE_SERIES),
            seed(85, false, false, 4, 17, LEARN_4_NOTE_SERIES),
            seed(85, false, false, 1, 70, LEARN_NOTES),
            seed(90, false, false, 1, 75, LEARN_NOTES),
    };

    public static void populateInitialData(@NonNull SupportSQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=ON");
        db.beginTransaction();
        try {
            SeedWriter writer = new SeedWriter(db);
            long[] taskIds = insertTasks(writer);

            insertSubTasks(writer, taskIds[0], TASK_1_SUB_TASKS);
            insertSubTasks(writer, taskIds[1], TASK_2_SUB_TASKS);
            insertSubTasks(writer, taskIds[2], TASK_3_SUB_TASKS);
            insertSubTasks(writer, taskIds[3], TASK_4_SUB_TASKS);
            insertSubTasks(writer, taskIds[4], TASK_5_SUB_TASKS);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private static long[] insertTasks(SeedWriter writer) {
        long[] taskIds = new long[TASK_SEEDS.length];
        for (int i = 0; i < TASK_SEEDS.length; i++) {
            taskIds[i] = writer.insertTask(TASK_SEEDS[i]);
        }
        return taskIds;
    }

    private static void insertSubTasks(SeedWriter writer, long taskId, SubTaskSeed[] subTaskSeeds) {
        for (SubTaskSeed subTaskSeed : subTaskSeeds) {
            writer.insertSubTask(taskId, subTaskSeed);
        }
    }

    private static TaskSeed task(int nameId, int setOfNotesId, int setOfNotesHighlightingId) {
        return new TaskSeed(nameId, setOfNotesId, setOfNotesHighlightingId);
    }

    private static SubTaskSeed seed(int notesPerMinute, boolean playWithScale,
            boolean withNoteHighlighting, int notesInSequence, int sequencesInSubTask,
            Integer instructionId) {
        return new SubTaskSeed(notesPerMinute, playWithScale, withNoteHighlighting,
                notesInSequence, sequencesInSubTask, instructionId);
    }

    private static int sqliteBoolean(boolean value) {
        return value ? 1 : 0;
    }

    private static final class SeedWriter {
        private final SupportSQLiteDatabase db;
        private long nextTaskId = 1L;
        private long nextSubTaskId = 1L;
        private Long previousSubTaskId;

        private SeedWriter(SupportSQLiteDatabase db) {
            this.db = db;
        }

        private long insertTask(TaskSeed taskSeed) {
            long taskId = nextTaskId++;
            db.execSQL(INSERT_TASK_SQL, new Object[]{
                    taskId,
                    taskSeed.nameId,
                    taskSeed.setOfNotesId,
                    taskSeed.setOfNotesHighlightingId,
                    0
            });
            return taskId;
        }

        private void insertSubTask(long taskId, SubTaskSeed subTaskSeed) {
            long subTaskId = nextSubTaskId++;
            if (previousSubTaskId != null) {
                db.execSQL(UPDATE_NEXT_SUB_TASK_SQL, new Object[]{subTaskId, previousSubTaskId});
            }
            db.execSQL(INSERT_SUB_TASK_SQL, new Object[]{
                    subTaskId,
                    taskId,
                    null,
                    subTaskSeed.notesPerMinute,
                    sqliteBoolean(subTaskSeed.playWithScale),
                    sqliteBoolean(subTaskSeed.withNoteHighlighting),
                    subTaskSeed.notesInSequence,
                    subTaskSeed.sequencesInSubTask,
                    0,
                    subTaskSeed.instructionId,
                    0
            });
            previousSubTaskId = subTaskId;
        }
    }

    private record TaskSeed(int nameId,
                            int setOfNotesId,
                            int setOfNotesHighlightingId) {
    }

    private record SubTaskSeed(int notesPerMinute,
                               boolean playWithScale,
                               boolean withNoteHighlighting,
                               int notesInSequence,
                               int sequencesInSubTask, Integer instructionId) {
    }

    private Migrations() {
    }
}
