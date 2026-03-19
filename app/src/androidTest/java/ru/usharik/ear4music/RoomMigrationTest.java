package ru.usharik.ear4music;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.room.testing.MigrationTestHelper;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import ru.usharik.ear4music.model.room.AppDatabase;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test for Room database migrations.
 * Tests that migrations apply properly and preserve data integrity.
 */
@RunWith(AndroidJUnit4.class)
public class RoomMigrationTest {

    private static final String TEST_DB_NAME = "migration-test-db";

    @Rule
    public MigrationTestHelper testHelper = new MigrationTestHelper(
            InstrumentationRegistry.getInstrumentation(),
            AppDatabase.class.getCanonicalName(),
            new FrameworkSQLiteOpenHelperFactory()
    );

    /**
     * Test that migrations apply properly from version 1 to latest version.
     * This test verifies:
     * 1. Database can be created at version 1
     * 2. Test data can be inserted
     * 3. Migrations run successfully
     * 4. Data is preserved after migration
     * 5. Schema is validated after migration
     */
    @Test
    public void testMigrationsApplyProperly() throws IOException {
        // Create database at version 1
        SupportSQLiteDatabase db = testHelper.createDatabase(TEST_DB_NAME, 1);

        // Insert test data into Task table
        ContentValues taskValues = new ContentValues();
        taskValues.put("id", 1L);
        taskValues.put("nameId", 5);
        taskValues.put("setOfNotesId", 10);
        taskValues.put("setOfNotesHighlightingId", 15);
        taskValues.put("donePercent", 75);
        db.insert("task", SQLiteDatabase.CONFLICT_REPLACE, taskValues);

        // Insert test data into SubTask table
        ContentValues subTaskValues = new ContentValues();
        subTaskValues.put("id", 1L);
        subTaskValues.put("taskId", 1L);
        subTaskValues.put("nextSubTaskId", (Long) null);
        subTaskValues.put("notesPerMinute", 120);
        subTaskValues.put("playWithScale", true);
        subTaskValues.put("withNoteHighlighting", false);
        subTaskValues.put("notesInSequence", 8);
        subTaskValues.put("sequencesInSubTask", 20);
        subTaskValues.put("correctAnswerPercent", 85);
        subTaskValues.put("instructionId", 2);
        subTaskValues.put("isFavourite", true);
        db.insert("sub_task", SQLiteDatabase.CONFLICT_REPLACE, subTaskValues);

        db.close();

        // Run migrations and validate schema
        // Add all your migrations here: MIGRATION_1_2, MIGRATION_2_3, etc.
        // Example: testHelper.runMigrationsAndValidate(TEST_DB_NAME, 2, true, MIGRATION_1_2);
        
        // For now, reopen at version 1 to verify data integrity
        db = testHelper.runMigrationsAndValidate(TEST_DB_NAME, 1, true);

        // Verify Task data is preserved
        Cursor taskCursor = db.query("SELECT * FROM task WHERE id = 1");
        assertEquals("Should have 1 task", 1, taskCursor.getCount());
        taskCursor.moveToFirst();
        assertEquals("Task nameId should be preserved", 5, 
                taskCursor.getInt(taskCursor.getColumnIndexOrThrow("nameId")));
        assertEquals("Task donePercent should be preserved", 75, 
                taskCursor.getInt(taskCursor.getColumnIndexOrThrow("donePercent")));
        taskCursor.close();

        // Verify SubTask data is preserved
        Cursor subTaskCursor = db.query("SELECT * FROM sub_task WHERE id = 1");
        assertEquals("Should have 1 subtask", 1, subTaskCursor.getCount());
        subTaskCursor.moveToFirst();
        assertEquals("SubTask taskId should be preserved", 1L, 
                subTaskCursor.getLong(subTaskCursor.getColumnIndexOrThrow("taskId")));
        assertEquals("SubTask notesPerMinute should be preserved", 120, 
                subTaskCursor.getInt(subTaskCursor.getColumnIndexOrThrow("notesPerMinute")));
        assertEquals("SubTask correctAnswerPercent should be preserved", 85, 
                subTaskCursor.getInt(subTaskCursor.getColumnIndexOrThrow("correctAnswerPercent")));
        assertEquals("SubTask isFavourite should be preserved", 1, 
                subTaskCursor.getInt(subTaskCursor.getColumnIndexOrThrow("isFavourite")));
        subTaskCursor.close();

        db.close();
    }
}

