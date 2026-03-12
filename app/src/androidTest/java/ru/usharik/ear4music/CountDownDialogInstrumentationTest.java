package ru.usharik.ear4music;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertTrue;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class CountDownDialogInstrumentationTest {

    @Test
    public void countDownDialog_shouldBeDisplayed() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ExecuteTaskActivity.class);
        intent.putExtra(ExecuteTaskActivity.EXTRA_SUB_TASK_ID, 1L);

        try (ActivityScenario<ExecuteTaskActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(activity -> {
                CountDownDialog.newInstance(() -> {})
                        .show(activity.getSupportFragmentManager(), "test_countdown");
            });

            onView(withId(R.id.tvCountDown)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void countDownDialog_shouldCallActionAfterCountdown() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ExecuteTaskActivity.class);
        intent.putExtra(ExecuteTaskActivity.EXTRA_SUB_TASK_ID, 1L);

        try (ActivityScenario<ExecuteTaskActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(activity -> {
                CountDownDialog.newInstance(latch::countDown)
                        .show(activity.getSupportFragmentManager(), "test_countdown");
            });

            // The countdown emits 4 items at 1-second intervals; wait with a generous timeout
            assertTrue("Action was not invoked after countdown completed", latch.await(8, TimeUnit.SECONDS));
        }
    }
}
