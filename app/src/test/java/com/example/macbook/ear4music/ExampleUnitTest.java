package com.example.macbook.ear4music;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void statisticsStorageTest() {
        StatisticsStorage statisticsStorage = new StatisticsStorage();
        statisticsStorage.submitAnswer(1, NotesEnum.C, NotesEnum.C);
        statisticsStorage.submitAnswer(2, NotesEnum.D, NotesEnum.D);
        statisticsStorage.submitAnswer(2, NotesEnum.D, NotesEnum.E);

        statisticsStorage.submitAnswer(3, NotesEnum.C, NotesEnum.E);
        statisticsStorage.submitAnswer(4, NotesEnum.D, NotesEnum.F);
        statisticsStorage.submitAnswer(5, NotesEnum.C, NotesEnum.G);
        statisticsStorage.submitAnswer(5, NotesEnum.C, NotesEnum.C);

        statisticsStorage.submitAnswer(6, NotesEnum.C, null);
        statisticsStorage.submitAnswer(7, NotesEnum.D, null);
        statisticsStorage.submitAnswer(8, NotesEnum.C, null);
        statisticsStorage.submitAnswer(9, NotesEnum.D, null);
        statisticsStorage.submitAnswer(9, NotesEnum.D, NotesEnum.D);

        assertEquals(2, statisticsStorage.getCorrectCount());
        assertEquals(3, statisticsStorage.getWrongCount());
        assertEquals(4, statisticsStorage.getMissedCount());
    }
}