package ru.usharik.ear4music;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.view.View;

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

            onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
            onView(withId(R.id.sub_task_list)).check(matches(isDisplayed()));
            onView(withId(R.id.sub_task_list)).check(hasMinimumItemCount(1));

            onView(withId(R.id.sub_task_list)).perform(clickRecyclerViewItem(0));

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

    /**
     * Instrumentation-тест: задание «Все ноты без интонаций» (sub-task 10).
     *
     * <p>Sub-task 10: seed(15 bpm, playWithScale=false, withNoteHighlighting=false,
     * notesInSequence=1, sequencesInSubTask=8). Длительность: 8 нот × 4000 мс = 32 с.
     * CountDownDialog отсутствует (15 bpm &lt; 40).
     *
     * <p>Проверяем полный жизненный цикл: кнопка Start → диалог инструкции →
     * кнопка Stop (задание идёт) → диалог статистики с результатами.
     */
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

            // Ждём завершения: 8 нот × 4000 мс + 2 с запаса = 34 с
            SystemClock.sleep(34_000);

            // Диалог статистики появился — задание завершено
            onView(withId(R.id.statisticsRecyclerView)).check(matches(isDisplayed()));
            onView(withId(R.id.statisticsRecyclerView)).check(hasMinimumItemCount(1));
            onView(withId(R.id.dialogTitle))
                    .check(matches(allOf(isDisplayed(), withText(R.string.statistics_report_title))));
            // Кнопка OK доступна для нажатия — диалог функционально корректен
            onView(withId(R.id.okButton)).check(matches(isDisplayed()));
        }
    }

    /**
     * Instrumentation-тест: задание «Последовательность из двух нот» (sub-task 20).
     *
     * <p>Sub-task 20: seed(65 bpm, playWithScale=false, withNoteHighlighting=false,
     * notesInSequence=2, sequencesInSubTask=17). Длительность: 17 × 2 нот × 923 мс ≈ 31 с.
     * CountDownDialog появляется (65 bpm ≥ 40) и авто-закрывается через 3 с.
     *
     * <p>Проверяем полный жизненный цикл: кнопка Start → диалог инструкции →
     * обратный отсчёт (авто) → диалог статистики с результатами.
     * Примечание: после нажатия Start инструкция появляется мгновенно (MIDI-драйвер
     * уже инициализирован после первого теста), поэтому проверка кнопки Stop
     * между нажатием Start и появлением инструкции невозможна.
     */
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

            // Ждём: 3 с (обратный отсчёт) + 17×2 нот × 923 мс + 3 с запаса = 37 с
            SystemClock.sleep(80_000);

            // Диалог статистики появился — задание завершено
            onView(withId(R.id.statisticsRecyclerView)).check(matches(isDisplayed()));
            onView(withId(R.id.statisticsRecyclerView)).check(hasMinimumItemCount(1));
            onView(withId(R.id.dialogTitle))
                    .check(matches(allOf(isDisplayed(), withText(R.string.statistics_report_title))));
            // Кнопка OK доступна для нажатия — диалог функционально корректен
            onView(withId(R.id.okButton)).check(matches(isDisplayed()));
        }
    }

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
}