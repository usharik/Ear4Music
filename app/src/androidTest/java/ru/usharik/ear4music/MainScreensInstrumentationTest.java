package ru.usharik.ear4music;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewAssertion;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.material.tabs.TabLayout;

import org.hamcrest.Matcher;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.usharik.ear4music.activity.ExecuteTaskActivity;
import ru.usharik.ear4music.activity.SubTaskSelectActivity;
import ru.usharik.ear4music.activity.TaskSelectActivity;
import ru.usharik.ear4music.model.room.AppDatabase;

@RunWith(AndroidJUnit4.class)
public class MainScreensInstrumentationTest {

    private static final long TASK_ID = 1L;
    private static final long SUB_TASK_ID = 1L;

    @BeforeClass
    public static void resetDatabase() {
        Context context = ApplicationProvider.getApplicationContext();
        context.deleteDatabase(AppDatabase.DATABASE_NAME);
    }

    @Test
    public void taskSelect_toSubTask_toExecute_andNavigateUp_shouldWork() {
        Context context = ApplicationProvider.getApplicationContext();
        String taskName = context.getResources().getStringArray(R.array.task_name)[0];

        try (ActivityScenario<TaskSelectActivity> scenario = ActivityScenario.launch(TaskSelectActivity.class)) {
            onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
            onView(withId(R.id.tab_layout)).check(matches(isDisplayed()));
            selectAllTasksTab(scenario);
            onView(withId(R.id.task_list)).check(matches(isDisplayed()));
            onView(withId(R.id.task_list)).check(hasMinimumItemCount(1));

            onView(allOf(withText(taskName), isDescendantOfA(withId(R.id.task_list)))).perform(click());

            // Wait for SubTaskSelectActivity to load
            SystemClock.sleep(500);

            onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
            onView(withId(R.id.sub_task_list)).check(matches(isDisplayed()));
            onView(withId(R.id.sub_task_list)).check(hasMinimumItemCount(1));

            onView(withId(R.id.sub_task_list)).perform(clickRecyclerViewItem(0));

            // Wait for ExecuteTaskActivity to load
            SystemClock.sleep(500);

            onView(withId(R.id.details_card)).check(matches(isDisplayed()));
            onView(withId(R.id.status_card)).check(matches(isDisplayed()));
            onView(withId(R.id.piano_keyboard)).check(matches(isDisplayed()));
            onView(withId(R.id.buttonStart)).check(matches(allOf(isDisplayed(), withText(R.string.start))));

            clickNavigateUp();
            onView(withId(R.id.sub_task_list)).check(matches(isDisplayed()));

            clickNavigateUp();
            onView(withId(R.id.task_list)).check(matches(isDisplayed()));
            onView(withId(R.id.tab_layout)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void subTaskSelect_withKnownTaskId_shouldDisplaySubTaskList() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), SubTaskSelectActivity.class);
        intent.putExtra(SubTaskSelectActivity.EXTRA_TASK_ID, TASK_ID);

        try (ActivityScenario<SubTaskSelectActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
            onView(withId(R.id.sub_task_list)).check(matches(isDisplayed()));
            onView(withId(R.id.sub_task_list)).check(hasMinimumItemCount(1));
        }
    }

    @Test
    public void executeTask_withKnownSubTaskId_shouldDisplayMainControls() {
        try (ActivityScenario<ExecuteTaskActivity> scenario = launchExecuteTask()) {
            onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
            onView(withId(R.id.details_card)).check(matches(isDisplayed()));
            onView(withId(R.id.status_card)).check(matches(isDisplayed()));
            onView(withId(R.id.keyboard_card)).check(matches(isDisplayed()));
            onView(withId(R.id.piano_keyboard)).check(matches(isDisplayed()));
            onView(withId(R.id.buttonStart)).check(matches(allOf(isDisplayed(), withText(R.string.start))));
        }
    }

    @Test
    public void executeTask_startAndStop_shouldToggleButtonState() {
        try (ActivityScenario<ExecuteTaskActivity> scenario = launchExecuteTask()) {
            onView(withId(R.id.buttonStart)).perform(click());

            onView(withText(R.string.task_instruction_header)).check(matches(isDisplayed()));
            onView(withText(R.string.ok)).perform(click());

            onView(withId(R.id.buttonStart)).check(matches(allOf(isDisplayed(), withText(R.string.stop))));

            onView(withId(R.id.buttonStart)).perform(click());
            onView(withId(R.id.buttonStart)).check(matches(allOf(isDisplayed(), withText(R.string.start))));
        }
    }

    @Test
    public void allNotesNoIntonation_fullRun_showsStatisticsDialog() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ExecuteTaskActivity.class);
        intent.putExtra(ExecuteTaskActivity.EXTRA_SUB_TASK_ID, 10L);

        try (ActivityScenario<ExecuteTaskActivity> scenario = ActivityScenario.launch(intent)) {
            // Исходное состояние: кнопка «Start», прогресс-бар виден
            onView(withId(R.id.buttonStart))
                    .check(matches(allOf(isDisplayed(), withText(R.string.start))));
            onView(withId(R.id.progressBar)).check(matches(isDisplayed()));

            // Нажимаем Start → появляется инструкция к заданию → подтверждаем
            onView(withId(R.id.buttonStart)).perform(click());
            onView(withText(R.string.task_instruction_header)).check(matches(isDisplayed()));
            onView(withText(R.string.ok)).perform(click());

            // Задание запущено — кнопка показывает Stop (нет CountDownDialog, т.к. 15 bpm < 40)
            onView(withId(R.id.buttonStart))
                    .check(matches(allOf(isDisplayed(), withText(R.string.stop))));

            // Single-note mode: 8 notes total - press each key and wait for task to progress
            for (int note = 0; note < 8; note++) {
                // Get expected note before pressing
                final String[] expectedNoteBefore = {""};
                scenario.onActivity(activity -> {
                    TextView tvExpectedNote = activity.findViewById(R.id.tvExpectedNote);
                    expectedNoteBefore[0] = tvExpectedNote.getText().toString();
                });

                // Press the piano key at the correct position on screen
                onView(withId(R.id.piano_keyboard))
                        .perform(clickPianoKeyForExpectedNote());

                // Wait for task to progress to next note
                waitForEvent(() -> onView(withId(R.id.tvExpectedNote)).check(matches(not(withText(expectedNoteBefore[0])))), 5000);
            }

            SystemClock.sleep(2000);

            // Диалог статистики появился — задание завершено
            onView(withId(R.id.statisticsRecyclerView)).check(matches(isDisplayed()));
            onView(withId(R.id.statisticsRecyclerView)).check(hasMinimumItemCount(1));
            onView(withId(R.id.dialogTitle))
                    .check(matches(allOf(isDisplayed(), withText(R.string.statistics_report_title))));
            onView(withId(R.id.okButton)).check(matches(isDisplayed()));

            // Verify statistics directly from StatisticsStorage service
            scenario.onActivity(activity -> {
                ru.usharik.ear4music.activity.ExecuteTaskViewModel viewModel =
                    (ru.usharik.ear4music.activity.ExecuteTaskViewModel) activity.getViewModel();
                ru.usharik.ear4music.service.StatisticsStorage stats = viewModel.getStatisticsStorage();

                assertEquals("Total answers should be 8", 8, stats.getOverallCount());
                assertTrue("Should have at least 50% correct answers", stats.getCorrectPercent() >= 50);
                assertTrue("Should have at least 4 correct answers out of 8", stats.getCorrectCount() >= 4);
            });
        }
    }


    @Test
    public void twoNoteSequence_fullRun_showsStatisticsDialog() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ExecuteTaskActivity.class);
        intent.putExtra(ExecuteTaskActivity.EXTRA_SUB_TASK_ID, 20L);

        try (ActivityScenario<ExecuteTaskActivity> scenario = ActivityScenario.launch(intent)) {
            // Исходное состояние
            onView(withId(R.id.buttonStart))
                    .check(matches(allOf(isDisplayed(), withText(R.string.start))));

            // Нажимаем Start → появляется инструкция → подтверждаем
            // После OK появится CountDownDialog (3 с) — он блокирует view hierarchy активности
            onView(withId(R.id.buttonStart)).perform(click());
            onView(withText(R.string.task_instruction_header)).check(matches(isDisplayed()));
            onView(withText(R.string.ok)).perform(click());

            // Wait for countdown (3 seconds)
            waitForEvent(() -> onView(withId(R.id.tvCountDown)).check(matches(withText("3"))), 4000);
            waitForEvent(() -> onView(withId(R.id.tvCountDown)).check(doesNotExist()), 4000);

            // Now the task is running. For 2-note sequence with 17 repetitions:
            // Each sequence: play 2 notes, then wait for user to press 2 notes
            for (int sequence = 0; sequence < 17; sequence++) {
                // Wait for sequence to start playing (status = "PLAYING")
                waitForStatusIndicator("SEQ_PLAYING", 5000);

                // Wait for sequence to finish playing and be ready for input (status = "PLAYED")
                waitForStatusIndicator("SEQ_PLAYED", 5000);

                // Press first note by clicking on the piano keyboard
                onView(withId(R.id.piano_keyboard))
                        .perform(clickPianoKeyForExpectedNote());

                // Press second note
                onView(withId(R.id.piano_keyboard))
                        .perform(clickPianoKeyForExpectedNote());
            }

            SystemClock.sleep(2000);

            // Диалог статистики появился — задание завершено
            onView(withId(R.id.statisticsRecyclerView)).check(matches(isDisplayed()));
            onView(withId(R.id.statisticsRecyclerView)).check(hasMinimumItemCount(1));
            onView(withId(R.id.dialogTitle))
                    .check(matches(allOf(isDisplayed(), withText(R.string.statistics_report_title))));
            onView(withId(R.id.okButton)).check(matches(isDisplayed()));

            // Verify statistics directly from StatisticsStorage service
            scenario.onActivity(activity -> {
                ru.usharik.ear4music.activity.ExecuteTaskViewModel viewModel =
                    (ru.usharik.ear4music.activity.ExecuteTaskViewModel) activity.getViewModel();
                ru.usharik.ear4music.service.StatisticsStorage stats = viewModel.getStatisticsStorage();

                assertEquals("Total answers should be 34", 34, stats.getOverallCount());
                assertTrue("Should have at least 50% correct answers", stats.getCorrectPercent() >= 50);
                assertTrue("Should have at least 17 correct answers out of 34", stats.getCorrectCount() >= 17);
            });
        }
    }

    @Ignore("There is a bug here")
    @Test
    public void favouriteTab_shouldShowSubTaskMarkedAsFavourite() {
        Context context = ApplicationProvider.getApplicationContext();
        String taskName = context.getResources().getStringArray(R.array.task_name)[0];

        try (ActivityScenario<TaskSelectActivity> scenario = ActivityScenario.launch(TaskSelectActivity.class)) {
            onView(withId(R.id.tab_layout)).check(matches(isDisplayed()));
            selectAllTasksTab(scenario);
            onView(withId(R.id.task_list)).check(matches(isDisplayed()));
            onView(withId(R.id.task_list)).check(hasMinimumItemCount(1));

            onView(allOf(withText(taskName), isDescendantOfA(withId(R.id.task_list)))).perform(click());
            onView(withId(R.id.sub_task_list)).check(matches(isDisplayed()));
            onView(withId(R.id.sub_task_list)).perform(clickRecyclerViewItem(0));

            onView(withId(R.id.details_card)).check(matches(isDisplayed()));
            onView(withId(R.id.imageButton)).perform(click());

            clickNavigateUp();
            clickNavigateUp();

            onView(withId(R.id.tab_layout)).check(matches(isDisplayed()));
            selectFavouriteTasksTab(scenario);
            onView(withId(R.id.favourite_task_list)).check(matches(isDisplayed()));
            onView(withId(R.id.favourite_task_list)).check(hasMinimumItemCount(1));
            onView(allOf(withText(R.string.notes_with_intonations), isDescendantOfA(withId(R.id.favourite_task_list))))
                    .check(matches(isDisplayed()));
        }
    }

    private static ActivityScenario<ExecuteTaskActivity> launchExecuteTask() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ExecuteTaskActivity.class);
        intent.putExtra(ExecuteTaskActivity.EXTRA_SUB_TASK_ID, SUB_TASK_ID);
        return ActivityScenario.launch(intent);
    }

    private static void selectAllTasksTab(ActivityScenario<TaskSelectActivity> scenario) {
        selectTaskTab(scenario, 0);
    }

    private static void selectFavouriteTasksTab(ActivityScenario<TaskSelectActivity> scenario) {
        selectTaskTab(scenario, 1);
    }

    private static void selectTaskTab(ActivityScenario<TaskSelectActivity> scenario, int position) {
        scenario.onActivity(activity -> {
            TabLayout tabLayout = activity.findViewById(R.id.tab_layout);
            assertNotNull(tabLayout);
            TabLayout.Tab tab = tabLayout.getTabAt(position);
            assertNotNull(tab);
            tab.select();
        });
    }

    private static void clickNavigateUp() {
        onView(allOf(withContentDescription(androidx.appcompat.R.string.abc_action_bar_up_description), isDisplayed()))
                .perform(click());
    }

    /**
     * Wait for the tvExpectedNote to change from the given value.
     * Used to detect when task has progressed to the next note.
     */
    private static void waitForExpectedNoteToChange(ActivityScenario<ExecuteTaskActivity> scenario,
                                                     String previousNote, long timeoutMs) {
        long startTime = SystemClock.uptimeMillis();
        while (SystemClock.uptimeMillis() - startTime < timeoutMs) {
            final boolean[] changed = {false};
            scenario.onActivity(activity -> {
                String currentNote = ((TextView) activity.findViewById(R.id.tvExpectedNote)).getText().toString();
                if (!currentNote.equals(previousNote)) {
                    changed[0] = true;
                }
            });
            if (changed[0]) {
                return;
            }
            SystemClock.sleep(100);
        }
    }

    /**
     * Wait for the tvTaskPlayedIndicator to show the expected status.
     * Polls the view until it matches or timeout occurs.
     */
    private static void waitForStatusIndicator(String expectedStatus, long timeoutMs) {
        waitForEvent(() -> onView(withId(R.id.tvTaskPlayedIndicator))
                    .check(matches(withText(expectedStatus))), timeoutMs);
    }

    private static void waitForEvent(Runnable check, long timeoutMs) {
        long startTime = SystemClock.uptimeMillis();
        while (SystemClock.uptimeMillis() - startTime < timeoutMs) {
            try {
                check.run();
                return;
            } catch (AssertionError e) {
                SystemClock.sleep(50);
            }
        }
        check.run();
    }

    private static ViewAssertion hasMinimumItemCount(int minimumCount) {
        return (view, noViewFoundException) -> {
            if (noViewFoundException != null) {
                throw noViewFoundException;
            }
            RecyclerView recyclerView = (RecyclerView) view;
            RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
            assertNotNull(adapter);
            assertTrue("Expected at least " + minimumCount + " items but was " + adapter.getItemCount(),
                    adapter.getItemCount() >= minimumCount);
        };
    }

    private static ViewAction clickRecyclerViewItem(int position) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(RecyclerView.class);
            }

            @Override
            public String getDescription() {
                return "click RecyclerView item at position " + position;
            }

            @Override
            public void perform(UiController uiController, View view) {
                RecyclerView recyclerView = (RecyclerView) view;
                recyclerView.scrollToPosition(position);
                uiController.loopMainThreadUntilIdle();

                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
                assertNotNull("No view holder found at position " + position, viewHolder);

                viewHolder.itemView.performClick();
                uiController.loopMainThreadUntilIdle();
            }
        };
    }

    /**
     * Custom ViewAction to click on a piano key by reading the expected note from tvExpectedNote.
     */
    private static ViewAction clickPianoKeyForExpectedNote() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(ru.usharik.ear4music.widget.PianoKeyboard.class);
            }

            @Override
            public String getDescription() {
                return "click piano key for expected note";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ru.usharik.ear4music.widget.PianoKeyboard keyboard =
                    (ru.usharik.ear4music.widget.PianoKeyboard) view;

                // Get the expected note from the invisible TextView
                View rootView = view.getRootView();
                TextView tvExpectedNote = rootView.findViewById(R.id.tvExpectedNote);
                if (tvExpectedNote == null) {
                    return;
                }

                String expectedNoteName = tvExpectedNote.getText().toString();
                if (expectedNoteName.isEmpty()) {
                    return;
                }

                ru.usharik.ear4music.NotesEnum expectedNote =
                    ru.usharik.ear4music.NotesEnum.valueOf(expectedNoteName);

                // Calculate position based on note (white keys only: C, D, E, F, G, A, B, C2)
                int width = keyboard.getWidth();
                int height = keyboard.getHeight();
                int whiteKeyWidth = width / 8;
                float clickY = height * 0.75f; // Click in lower part of white keys
                float clickX;

                switch (expectedNote) {
                    case C:
                        clickX = whiteKeyWidth * 0.5f;
                        break;
                    case D:
                        clickX = whiteKeyWidth * 1.5f;
                        break;
                    case E:
                        clickX = whiteKeyWidth * 2.5f;
                        break;
                    case F:
                        clickX = whiteKeyWidth * 3.5f;
                        break;
                    case G:
                        clickX = whiteKeyWidth * 4.5f;
                        break;
                    case A:
                        clickX = whiteKeyWidth * 5.5f;
                        break;
                    case B:
                        clickX = whiteKeyWidth * 6.5f;
                        break;
                    case C2:
                        clickX = whiteKeyWidth * 7.5f;
                        break;
                    default:
                        return;
                }

                // Simulate touch events
                long downTime = android.os.SystemClock.uptimeMillis();
                MotionEvent downEvent = MotionEvent.obtain(
                    downTime, downTime, MotionEvent.ACTION_DOWN, clickX, clickY, 0);
                keyboard.dispatchTouchEvent(downEvent);
                downEvent.recycle();

                uiController.loopMainThreadForAtLeast(50);

                long upTime = android.os.SystemClock.uptimeMillis();
                MotionEvent upEvent = MotionEvent.obtain(
                    downTime, upTime, MotionEvent.ACTION_UP, clickX, clickY, 0);
                keyboard.dispatchTouchEvent(upEvent);
                upEvent.recycle();

                uiController.loopMainThreadUntilIdle();
            }
        };
    }
}