package com.example.macbook.ear4music;

import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import static org.junit.Assert.*;

public class Ear4MusicUnitTest {

    @Test
    public void statisticsStorage_TestCorrectAnswerSubmission() {
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

    @Test
    public void statisticsStorage_TestCalcFinalResult() {
        Random rnd = new Random();
        int noteCnt = 0;
        HashMap<NotesEnum, StatisticsStorage.Result> expected = new HashMap<>();
        StatisticsStorage statisticsStorage = new StatisticsStorage();

        StatisticsStorage.Result resultC = new StatisticsStorage.Result();
        resultC.correct = 1 + rnd.nextInt(9);
        noteCnt = submitAnswers(statisticsStorage, noteCnt, NotesEnum.C, NotesEnum.C, resultC.correct);
        resultC.wrong = 1 + rnd.nextInt(9);
        noteCnt = submitAnswers(statisticsStorage, noteCnt, NotesEnum.C, NotesEnum.D, resultC.wrong);
        resultC.missed = 1 + rnd.nextInt(9);
        noteCnt = submitAnswers(statisticsStorage, noteCnt, NotesEnum.C, null, resultC.missed);
        expected.put(NotesEnum.C, resultC);

        StatisticsStorage.Result resultD = new StatisticsStorage.Result();
        resultD.correct = 1 + rnd.nextInt(9);
        noteCnt = submitAnswers(statisticsStorage, noteCnt, NotesEnum.D, NotesEnum.D, resultD.correct);
        resultD.wrong = 1 + rnd.nextInt(9);
        noteCnt = submitAnswers(statisticsStorage, noteCnt, NotesEnum.D, NotesEnum.E, resultD.wrong);
        resultD.missed = 1 + rnd.nextInt(9);
        noteCnt = submitAnswers(statisticsStorage, noteCnt, NotesEnum.D, null, resultD.missed);
        expected.put(NotesEnum.D, resultD);

        HashMap<NotesEnum, StatisticsStorage.Result> actual = statisticsStorage.calcFinalResult();
        assertEquals(expected.size(), actual.size());
        for(NotesEnum key : actual.keySet()) {
            assertEquals(expected.get(key).correct, actual.get(key).correct);
            assertEquals(expected.get(key).wrong, actual.get(key).wrong);
            assertEquals(expected.get(key).missed, actual.get(key).missed);
        }
    }

    private int submitAnswers(StatisticsStorage statisticsStorage, int num, NotesEnum current, NotesEnum pressed, int count) {
        for(int i=num;i<num+count;i++) {
            statisticsStorage.submitAnswer(i, current, pressed);
        }
        return num+count;
    }

    @Test
    public void randomNoteGenerator_TestNoteMoreTwoNotesRunning() {
        RandomNoteGenerator randomNoteGenerator = new RandomNoteGenerator(NotesEnum.getWhite());
        NotesEnum curr = null;
        NotesEnum next = null;
        for (int i=0;i<10000;i++) {
            if (curr == null) {
                curr = randomNoteGenerator.nextNote();
            } else if (next == null) {
                next = randomNoteGenerator.nextNote();
            } else {
                NotesEnum test = randomNoteGenerator.nextNote();
                assertFalse(curr == next && test == next);
                curr = null;
                next = null;
            }
        }
    }

    @Test
    public void randomNoteGenerator_TestAllNoteExistsInGeneratedSequence() {
        RandomNoteGenerator randomNoteGenerator = new RandomNoteGenerator(NotesEnum.getWhite());
        HashSet<NotesEnum> sequence = new HashSet<>();
        for (int i=0;i<10000;i++) {
            sequence.add(randomNoteGenerator.nextNote());
        }
        for (NotesEnum note : NotesEnum.getWhite()) {
            assertTrue(sequence.contains(note));
        }
    }
}