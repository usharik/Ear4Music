package ru.usharik.ear4music.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashMap;

import ru.usharik.ear4music.NotesEnum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StatisticsStorageTest {

    private StatisticsStorage storage;

    @BeforeEach
    void setUp() {
        storage = new StatisticsStorage();
    }

    @Test
    @DisplayName("Initial state: all counters and percent are zero")
    void initialState_isEmpty() {
        assertEquals(0, storage.getCorrectCount());
        assertEquals(0, storage.getWrongCount());
        assertEquals(0, storage.getMissedCount());
        assertEquals(0, storage.getOverallCount());
        assertEquals(0, storage.getCorrectPercent());
        assertEquals(0, storage.getAvgAnswerTime());
    }

    @Test
    @DisplayName("submitAnswer: correct answer increments correct and overall")
    void submitAnswer_correct_incrementsCorrect() {
        storage.submitAnswer(1L, NotesEnum.C, NotesEnum.C, new Date());

        assertEquals(1, storage.getCorrectCount());
        assertEquals(0, storage.getWrongCount());
        assertEquals(0, storage.getMissedCount());
        assertEquals(1, storage.getOverallCount());
        assertEquals(100, storage.getCorrectPercent());
    }

    @Test
    @DisplayName("submitAnswer: wrong and missed answers update counters and percent")
    void submitAnswer_wrongAndMissed_updatesCounters() {
        storage.submitAnswer(1L, NotesEnum.C, NotesEnum.D, new Date());
        storage.submitAnswer(2L, NotesEnum.E, null, new Date());

        assertEquals(0, storage.getCorrectCount());
        assertEquals(1, storage.getWrongCount());
        assertEquals(1, storage.getMissedCount());
        assertEquals(2, storage.getOverallCount());
        assertEquals(0, storage.getCorrectPercent());
        assertEquals(0, storage.getAvgAnswerTime());
    }

    @Test
    @DisplayName("submitAnswer: duplicate noteNumber is counted only once")
    void submitAnswer_duplicateNoteNumber_ignoredSecondTime() {
        storage.submitAnswer(42L, NotesEnum.C, NotesEnum.C, new Date());
        storage.submitAnswer(42L, NotesEnum.C, NotesEnum.D, new Date());

        assertEquals(1, storage.getCorrectCount());
        assertEquals(0, storage.getWrongCount());
        assertEquals(0, storage.getMissedCount());
        assertEquals(1, storage.getOverallCount());
    }

    @Test
    @DisplayName("submitAnswer: null actual note is ignored")
    void submitAnswer_nullActualNote_isIgnored() {
        storage.submitAnswer(1L, null, NotesEnum.C, new Date());

        assertEquals(0, storage.getCorrectCount());
        assertEquals(0, storage.getWrongCount());
        assertEquals(0, storage.getMissedCount());
        assertEquals(0, storage.getOverallCount());
    }

    @Test
    @DisplayName("calcFinalResult: aggregates per actual note")
    void calcFinalResult_aggregatesByActualNote() {
        storage.submitAnswer(1L, NotesEnum.C, NotesEnum.C, new Date());
        storage.submitAnswer(2L, NotesEnum.C, NotesEnum.D, new Date());
        storage.submitAnswer(3L, NotesEnum.C, null, new Date());
        storage.submitAnswer(4L, NotesEnum.E, NotesEnum.E, new Date());

        HashMap<NotesEnum, StatisticsStorage.Result> result = storage.calcFinalResult();

        assertNotNull(result.get(NotesEnum.C));
        assertEquals(1, result.get(NotesEnum.C).correct);
        assertEquals(1, result.get(NotesEnum.C).wrong);
        assertEquals(1, result.get(NotesEnum.C).missed);

        assertNotNull(result.get(NotesEnum.E));
        assertEquals(1, result.get(NotesEnum.E).correct);
        assertEquals(0, result.get(NotesEnum.E).wrong);
        assertEquals(0, result.get(NotesEnum.E).missed);
    }

    @Test
    @DisplayName("reset: clears answers and counters")
    void reset_clearsAllState() {
        storage.submitAnswer(1L, NotesEnum.C, NotesEnum.C, new Date());
        storage.submitAnswer(2L, NotesEnum.E, null, new Date());

        storage.reset();

        assertEquals(0, storage.getCorrectCount());
        assertEquals(0, storage.getWrongCount());
        assertEquals(0, storage.getMissedCount());
        assertEquals(0, storage.getOverallCount());
        assertEquals(0, storage.getCorrectPercent());
        assertEquals(0, storage.getAvgAnswerTime());
        assertEquals(0, storage.calcFinalResult().size());
    }
}

