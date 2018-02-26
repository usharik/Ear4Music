package com.example.macbook.ear4music.service;

import com.example.macbook.ear4music.model.DaoSession;
import com.example.macbook.ear4music.model.Options;
import com.example.macbook.ear4music.model.SubTask;
import com.example.macbook.ear4music.model.SubTaskDao;
import com.example.macbook.ear4music.model.Task;
import com.example.macbook.ear4music.model.TaskDao;

/**
 * Created by au185034 on 21/02/2018.
 */

public class InitData {
    public static final long dbVersion = 12L;

    // <string-array name="task_name">
    private static final int learn_C_E_G_C2 = 0;
    private static final int learn_B = 1;
    private static final int learn_F = 2;
    private static final int learn_A = 3;
    private static final int learn_D = 4;

    // <string-array name="task_instruction">
    private static final int learn_intonation_C_E_G_C2 = 0;
    private static final int learn_intonation_B = 1;
    private static final int learn_intonation_F = 2;
    private static final int learn_intonation_A = 3;
    private static final int learn_intonation_D = 4;
    private static final int learn_intonation_2 = 5;
    private static final int learn_notes = 6;
    private static final int learn_2_note_series = 7;
    private static final int learn_3_note_series = 8;
    private static final int learn_4_note_series = 9;

    public static void initData(DaoSession daoSession) {
        daoSession.getOptionsDao().insert(new Options(dbVersion));

        TaskDao taskDao = daoSession.getTaskDao();
        Task task1 = insertTask(taskDao, new Task(null, learn_C_E_G_C2, 0, 0, 0));
        Task task2 = insertTask(taskDao, new Task(null, learn_B, 1, 5, 0));
        Task task3 = insertTask(taskDao, new Task(null, learn_F, 2, 6, 0));
        Task task4 = insertTask(taskDao, new Task(null, learn_A, 3, 7, 0));
        Task task5 = insertTask(taskDao, new Task(null, learn_D, 4, 8, 0));

        SubTaskDao subTaskDao = daoSession.getSubTaskDao();
        long taskId = task1.getId();
        SubTask prevSubTask;
        prevSubTask = insertSubTask(subTaskDao, null, new SubTask(null, taskId, null, 15, true, true, 1, 4, 0, learn_intonation_C_E_G_C2, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 15, true, false, 1, 10, 0, learn_intonation_2, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 20, true, false, 1, 10, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 25, true, false, 1, 13, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 30, true, false, 1, 16, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 35, true, false, 1, 18, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 40, true, false, 1, 20, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 45, true, false, 1, 23, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 50, true, false, 1, 26, 0, null, false));

        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 15, false, false, 1, 8, 0, learn_notes, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 20, false, false, 1, 10, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 25, false, false, 1, 13, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 30, false, false, 1, 16, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 35, false, false, 1, 20, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 40, false, false, 1, 21, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 45, false, false, 1, 27, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 50, false, false, 1, 28, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 55, false, false, 1, 32, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 60, false, false, 1, 32, 0, null, false));

        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 65, false, false, 2, 17, 0, learn_2_note_series, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 65, false, false, 3, 12, 0, learn_3_note_series, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 65, false, false, 4, 9, 0, learn_4_note_series, false));

        taskId = task2.getId();
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 15, true, true, 1, 1, 0, learn_intonation_B, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 15, true, false, 1, 10, 0, learn_intonation_2, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 20, true, false, 1, 12, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 25, true, false, 1, 15, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 30, true, false, 1, 18, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 35, true, false, 1, 21, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 40, true, false, 1, 24, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 45, true, false, 1, 27, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 50, true, false, 1, 30, 0, null, false));

        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 20, false, false, 1, 12, 0, learn_notes, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 25, false, false, 1, 15, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 30, false, false, 1, 19, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 35, false, false, 1, 21, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 40, false, false, 1, 25, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 45, false, false, 1, 28, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 50, false, false, 1, 31, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 55, false, false, 1, 33, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 60, false, false, 1, 37, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 65, false, false, 1, 42, 0, null, false));

        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 70, false, false, 2, 19, 0, learn_2_note_series, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 70, false, false, 3, 14, 0, learn_3_note_series, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 70, false, false, 4, 12, 0, learn_4_note_series, false));

        taskId = task3.getId();
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 15, true, true, 1, 1, 0, learn_intonation_F, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 15, true, false, 1, 11, 0, learn_intonation_2, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 20, true, false, 1, 15, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 25, true, false, 1, 17, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 30, true, false, 1, 21, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 35, true, false, 1, 25, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 40, true, false, 1, 28, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 45, true, false, 1, 31, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 50, true, false, 1, 35, 0, null, false));

        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 25, false, false, 1, 20, 0, learn_notes, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 30, false, false, 1, 21, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 35, false, false, 1, 25, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 40, false, false, 1, 28, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 45, false, false, 1, 31, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 50, false, false, 1, 35, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 55, false, false, 1, 37, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 60, false, false, 1, 42, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 65, false, false, 1, 45, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 70, false, false, 1, 50, 0, null, false));

        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 75, false, false, 2, 21, 0, learn_2_note_series, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 75, false, false, 3, 16, 0, learn_3_note_series, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 75, false, false, 4, 14, 0, learn_4_note_series, false));

        taskId = task4.getId();
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 15, true, true, 1, 1, 0, learn_intonation_A, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 15, true, false, 1, 13, 0, learn_intonation_2, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 20, true, false, 1, 17, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 25, true, false, 1, 19, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 30, true, false, 1, 22, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 35, true, false, 1, 26, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 40, true, false, 1, 29, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 45, true, false, 1, 33, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 50, true, false, 1, 37, 0, null, false));

        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 30, false, false, 1, 26, 0, learn_notes, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 35, false, false, 1, 28, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 40, false, false, 1, 29, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 45, false, false, 1, 32, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 50, false, false, 1, 37, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 55, false, false, 1, 40, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 60, false, false, 1, 44, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 65, false, false, 1, 47, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 70, false, false, 1, 55, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 75, false, false, 1, 60, 0, null, false));

        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 80, false, false, 2, 21, 0, learn_2_note_series, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 80, false, false, 3, 16, 0, learn_3_note_series, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 80, false, false, 4, 14, 0, learn_4_note_series, false));

        taskId = task5.getId();
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 15, true, true, 1, 1, 0, learn_intonation_D, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 15, true, false, 1, 13, 0, learn_intonation_2, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 20, true, false, 1, 17, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 25, true, false, 1, 19, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 30, true, false, 1, 22, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 35, true, false, 1, 26, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 40, true, false, 1, 29, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 45, true, false, 1, 33, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 50, true, false, 1, 37, 0, null, false));

        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 35, false, false, 1, 30, 0, learn_notes, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 40, false, false, 1, 23, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 45, false, false, 1, 34, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 50, false, false, 1, 39, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 55, false, false, 1, 42, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 60, false, false, 1, 46, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 65, false, false, 1, 49, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 70, false, false, 1, 57, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 75, false, false, 1, 65, 0, null, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 80, false, false, 1, 70, 0, null, false));

        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 85, false, false, 2, 25, 0, learn_2_note_series, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 85, false, false, 3, 20, 0, learn_3_note_series, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 85, false, false, 4, 17, 0, learn_4_note_series, false));

        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 85, false, false, 1, 70, 0, learn_notes, false));
        prevSubTask = insertSubTask(subTaskDao, prevSubTask, new SubTask(null, taskId, null, 90, false, false, 1, 75, 0, learn_notes, false));
    }

    private static Task insertTask(TaskDao taskDao, Task task) {
        taskDao.insert(task);
        return task;
    }

    private static SubTask insertSubTask(SubTaskDao subTaskDao, SubTask prevSubTask, SubTask subTask) {
        subTaskDao.insert(subTask);
        if (prevSubTask != null) {
            prevSubTask.setNextSubTaskId(subTask.getId());
            subTaskDao.update(prevSubTask);
        }
        return subTask;
    }
}
