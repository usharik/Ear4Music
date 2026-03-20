package ru.usharik.ear4music.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import ru.usharik.ear4music.NoteInfo;
import ru.usharik.ear4music.NotesEnum;
import ru.usharik.ear4music.widget.KeyPress;

import static org.junit.jupiter.api.Assertions.*;

class JudgedAnswerTest {

    private static final long NOTE_NUM = 7L;
    private static final Date NOTE_TIME = new Date(1_000_000L);

    private NoteInfo prompt(NotesEnum note) {
        return new NoteInfo(NOTE_NUM, note, null, false, false, 1);
    }

    // --- from() factory ---

    @Test
    @DisplayName("from: captures noteNumber, expectedNote and pressedNote from prompt + KeyPress")
    void from_populatesAllFields() {
        KeyPress keyPress = new KeyPress(NotesEnum.E, System.currentTimeMillis());
        JudgedAnswer answer = JudgedAnswer.from(prompt(NotesEnum.C), keyPress);

        assertEquals(NOTE_NUM, answer.noteNumber);
        assertEquals(NotesEnum.C, answer.expectedNote);
        assertEquals(NotesEnum.E, answer.pressedNote);
        assertNotNull(answer.noteTime);
    }

    @Test
    @DisplayName("from: correct answer when pressed == expected")
    void from_correct_whenPressedMatchesExpected() {
        KeyPress keyPress = new KeyPress(NotesEnum.C, System.currentTimeMillis());
        JudgedAnswer answer = JudgedAnswer.from(prompt(NotesEnum.C), keyPress);

        assertTrue(answer.isCorrect());
        assertFalse(answer.isMissed());
    }

    @Test
    @DisplayName("from: wrong answer when pressed != expected")
    void from_wrong_whenPressedDiffersFromExpected() {
        KeyPress keyPress = new KeyPress(NotesEnum.D, System.currentTimeMillis());
        JudgedAnswer answer = JudgedAnswer.from(prompt(NotesEnum.C), keyPress);

        assertFalse(answer.isCorrect());
        assertFalse(answer.isMissed());
    }

    @Test
    @DisplayName("from: pressedNote=null results in isMissed()=true and isCorrect()=false")
    void from_nullPress_isMissed() {
        KeyPress keyPress = new KeyPress(null, System.currentTimeMillis());
        JudgedAnswer answer = JudgedAnswer.from(prompt(NotesEnum.C), keyPress);

        assertTrue(answer.isMissed());
        assertFalse(answer.isCorrect());
    }

    // --- missed() factory ---

    @Test
    @DisplayName("missed: pressedNote is null and isMissed() is true")
    void missed_setsNullPressedNote() {
        JudgedAnswer answer = JudgedAnswer.missed(prompt(NotesEnum.G));

        assertNull(answer.pressedNote);
        assertTrue(answer.isMissed());
        assertFalse(answer.isCorrect());
        assertEquals(NOTE_NUM, answer.noteNumber);
        assertEquals(NotesEnum.G, answer.expectedNote);
    }

    // --- StatisticsStorage integration via submitAnswer(JudgedAnswer) ---

    @Test
    @DisplayName("StatisticsStorage.submitAnswer(JudgedAnswer): correct answer counted correctly")
    void statisticsStorage_acceptsJudgedAnswer_correct() {
        StatisticsStorage storage = new StatisticsStorage();
        JudgedAnswer answer = new JudgedAnswer(1L, NotesEnum.C, NotesEnum.C, NOTE_TIME);
        storage.submitAnswer(answer);

        assertEquals(1, storage.getCorrectCount());
        assertEquals(0, storage.getWrongCount());
        assertEquals(0, storage.getMissedCount());
    }

    @Test
    @DisplayName("StatisticsStorage.submitAnswer(JudgedAnswer): wrong answer counted correctly")
    void statisticsStorage_acceptsJudgedAnswer_wrong() {
        StatisticsStorage storage = new StatisticsStorage();
        JudgedAnswer answer = new JudgedAnswer(2L, NotesEnum.C, NotesEnum.D, NOTE_TIME);
        storage.submitAnswer(answer);

        assertEquals(0, storage.getCorrectCount());
        assertEquals(1, storage.getWrongCount());
        assertEquals(0, storage.getMissedCount());
    }

    @Test
    @DisplayName("StatisticsStorage.submitAnswer(JudgedAnswer): missed answer counted correctly")
    void statisticsStorage_acceptsJudgedAnswer_missed() {
        StatisticsStorage storage = new StatisticsStorage();
        JudgedAnswer answer = JudgedAnswer.missed(prompt(NotesEnum.E));
        storage.submitAnswer(answer);

        assertEquals(0, storage.getCorrectCount());
        assertEquals(0, storage.getWrongCount());
        assertEquals(1, storage.getMissedCount());
    }
}
