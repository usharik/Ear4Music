package com.example.macbook.ear4music;

import org.junit.Test;

import java.util.HashSet;

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