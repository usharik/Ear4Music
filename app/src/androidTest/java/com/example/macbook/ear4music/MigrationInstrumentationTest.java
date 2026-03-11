package com.example.macbook.ear4music;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.macbook.ear4music.model.SubTask;
import com.example.macbook.ear4music.model.Task;
import com.example.macbook.ear4music.model.room.AppDatabase;
import com.example.macbook.ear4music.model.room.SubTaskWithTask;
import com.example.macbook.ear4music.repository.SubTaskRepository;
import com.example.macbook.ear4music.repository.TaskRepository;
import com.example.macbook.ear4music.service.AppState;
import com.example.macbook.ear4music.service.StatisticsStorage;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class MigrationInstrumentationTest {

    private static final String FRESH_DB_NAME = "ear4-music-db-room-fresh-test";

    @Test
    public void freshCreate_shouldPopulateInitialData() {
        Context context = ApplicationProvider.getApplicationContext();
        context.deleteDatabase(FRESH_DB_NAME);

        AppDatabase appDatabase = AppDatabase.create(context, FRESH_DB_NAME);
        try {
            TaskRepository taskRepository = new TaskRepository(appDatabase);
            SubTaskRepository subTaskRepository = new SubTaskRepository(appDatabase);
            List<Task> tasks = taskRepository.getAll();
            int subTaskCount = 0;
            for (Task task : tasks) {
                subTaskCount += subTaskRepository.findByTaskId(task.getId()).size();
            }

            List<SubTask> taskSubTasks = subTaskRepository.findByTaskId(1);
            SubTaskWithTask firstSubTask = subTaskRepository.findWithTaskById(1L);
            SubTask taskBoundarySubTask = subTaskRepository.findById(22L);
            SubTask lastSubTask = subTaskRepository.findById(112L);

            assertEquals(5, tasks.size());
            assertEquals(112, subTaskCount);
            assertFalse(taskSubTasks.isEmpty());
            assertEquals(1L, taskSubTasks.get(0).getTaskId());
            assertNotNull(firstSubTask);
            assertNotNull(firstSubTask.task);
            assertEquals(1L, firstSubTask.task.getId().longValue());
            assertNotNull(taskBoundarySubTask);
            assertNotNull(taskBoundarySubTask.getNextSubTaskId());
            assertEquals(23L, taskBoundarySubTask.getNextSubTaskId().longValue());
            assertNotNull(lastSubTask);
            assertNull(lastSubTask.getNextSubTaskId());
        } finally {
            appDatabase.close();
            context.deleteDatabase(FRESH_DB_NAME);
        }
    }

    @Test
    public void subTaskSelectViewModel_shouldRestoreTaskFromTaskId() {
        Context context = ApplicationProvider.getApplicationContext();
        String dbName = "ear4-music-db-room-restore-task-test";
        context.deleteDatabase(dbName);

        AppDatabase appDatabase = AppDatabase.create(context, dbName);
        try {
            TaskRepository taskRepository = new TaskRepository(appDatabase);
            SubTaskRepository subTaskRepository = new SubTaskRepository(appDatabase);
            SubTaskSelectViewModel viewModel = new SubTaskSelectViewModel(taskRepository, subTaskRepository, new AppState());

            assertTrue(viewModel.syncWithTaskId(1L));
            assertNotNull(viewModel.getTask());
            assertEquals(1L, viewModel.getTask().getId().longValue());
        } finally {
            appDatabase.close();
            context.deleteDatabase(dbName);
        }
    }

    @Test
    public void executeTaskViewModel_shouldRestoreSubTaskFromSubTaskId() {
        Context context = ApplicationProvider.getApplicationContext();
        String dbName = "ear4-music-db-room-restore-subtask-test";
        context.deleteDatabase(dbName);

        AppDatabase appDatabase = AppDatabase.create(context, dbName);
        try {
            TaskRepository taskRepository = new TaskRepository(appDatabase);
            SubTaskRepository subTaskRepository = new SubTaskRepository(appDatabase);
            ExecuteTaskViewModel viewModel = new ExecuteTaskViewModel(taskRepository, subTaskRepository, new AppState(), new StatisticsStorage());

            assertTrue(viewModel.syncWithSubTaskId(1L));
            assertNotNull(viewModel.getSubTask());
            assertEquals(1L, viewModel.getSubTask().getTaskId());
            assertNotNull(viewModel.getSubTask().getNextSubTaskId());
            assertEquals(2L, viewModel.getSubTask().getNextSubTaskId().longValue());
            assertEquals(1L, viewModel.getSubTaskId());
            assertEquals(1L, viewModel.getTaskId());
            assertTrue(viewModel.syncWithSubTaskId(viewModel.getSubTask().getNextSubTaskId()));
            assertEquals(2L, viewModel.getSubTaskId());
        } finally {
            appDatabase.close();
            context.deleteDatabase(dbName);
        }
    }
}
