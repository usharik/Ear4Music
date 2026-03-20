package ru.usharik.ear4music.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import io.reactivex.rxjava3.observers.TestObserver;
import ru.usharik.ear4music.NoteInfo;
import ru.usharik.ear4music.NotesEnum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NoteSequenceEmitterTest {

    // Three-note melody: C E G — size divisible by 1 and 3
    private static final List<NotesEnum> MELODY_3 = Arrays.asList(NotesEnum.C, NotesEnum.E, NotesEnum.G);

    // Four-note melody — size divisible by 1, 2 and 4
    private static final List<NotesEnum> MELODY_4 =
            Arrays.asList(NotesEnum.C, NotesEnum.D, NotesEnum.E, NotesEnum.G);

    // ------------------------------------------------------------------------------------
    // getLongitude
    // ------------------------------------------------------------------------------------

    @Test
    @DisplayName("getLongitude: 60 bpm → 1000 ms")
    void getLongitude_60bpm_returns1000() {
        assertEquals(1000, new NoteSequenceEmitter(60, 1, 1, false, false).getLongitude());
    }

    @Test
    @DisplayName("getLongitude: 120 bpm → 500 ms")
    void getLongitude_120bpm_returns500() {
        assertEquals(500, new NoteSequenceEmitter(120, 1, 1, false, false).getLongitude());
    }

    @Test
    @DisplayName("getLongitude: 90 bpm → 667 ms (rounded)")
    void getLongitude_90bpm_roundsCorrectly() {
        assertEquals(667, new NoteSequenceEmitter(90, 1, 1, false, false).getLongitude());
    }

    // ------------------------------------------------------------------------------------
    // Melody mode (withNoteHighlighting = true)
    // ------------------------------------------------------------------------------------

    @Test
    @DisplayName("Melody mode: emits notes in melody order")
    void melodyMode_emitsNotesInMelodyOrder() {
        NoteSequenceEmitter emitter = new NoteSequenceEmitter(60, 1, 3, false, true);
        TestObserver<NoteInfo[]> obs = emitter.createObservable(MELODY_3).test();

        obs.assertComplete();
        obs.assertNoErrors();
        obs.assertValueCount(3);

        List<NoteInfo[]> values = obs.values();
        assertEquals(NotesEnum.C, values.get(0)[0].note);
        assertEquals(NotesEnum.E, values.get(1)[0].note);
        assertEquals(NotesEnum.G, values.get(2)[0].note);
    }

    @Test
    @DisplayName("Melody mode: emits exactly melody.size() groups")
    void melodyMode_emitsExactlyMelodySizeGroups() {
        NoteSequenceEmitter emitter = new NoteSequenceEmitter(60, 1, 99, false, true);
        emitter.createObservable(MELODY_3).test().assertValueCount(3);
    }

    @Test
    @DisplayName("Melody mode: observable completes after all notes")
    void melodyMode_completesAfterAllNotes() {
        NoteSequenceEmitter emitter = new NoteSequenceEmitter(60, 1, 1, false, true);
        emitter.createObservable(MELODY_3).test().assertComplete();
    }

    // ------------------------------------------------------------------------------------
    // Random mode (withNoteHighlighting = false)
    // ------------------------------------------------------------------------------------

    @Test
    @DisplayName("Random mode: emits exactly sequencesInSubTask groups")
    void randomMode_emitsExactlySequencesCount() {
        NoteSequenceEmitter emitter = new NoteSequenceEmitter(60, 1, 5, false, false);
        emitter.createObservable(MELODY_3).test()
                .assertComplete()
                .assertValueCount(5);
    }

    @Test
    @DisplayName("Random mode: notes come from the supplied melody")
    void randomMode_notesAreFromMelody() {
        NoteSequenceEmitter emitter = new NoteSequenceEmitter(60, 1, 10, false, false);
        List<NoteInfo[]> values = emitter.createObservable(MELODY_3).test().values();

        for (NoteInfo[] group : values) {
            assertTrue(MELODY_3.contains(group[0].note),
                    "Note " + group[0].note + " must be from the supplied melody");
        }
    }

    // ------------------------------------------------------------------------------------
    // notesInSequence > 1
    // ------------------------------------------------------------------------------------

    @Test
    @DisplayName("notesInSequence=2: each emission contains 2 NoteInfo elements")
    void notesInSequence2_eachGroupHasTwoElements() {
        // MELODY_4 has 4 notes → 2 groups of 2 in melody mode
        NoteSequenceEmitter emitter = new NoteSequenceEmitter(60, 2, 1, false, true);
        List<NoteInfo[]> values = emitter.createObservable(MELODY_4).test()
                .assertComplete()
                .values();

        assertEquals(2, values.size());
        for (NoteInfo[] group : values) {
            assertEquals(2, group.length);
        }
    }

    // ------------------------------------------------------------------------------------
    // NoteInfo field correctness
    // ------------------------------------------------------------------------------------

    @Test
    @DisplayName("NoteInfo: num is sequential across all groups")
    void noteInfo_numIsSequential() {
        NoteSequenceEmitter emitter = new NoteSequenceEmitter(60, 1, 1, false, true);
        List<NoteInfo[]> values = emitter.createObservable(MELODY_3).test().values();

        for (int i = 0; i < values.size(); i++) {
            assertEquals((long) i, values.get(i)[0].num,
                    "Expected num=" + i + " at index " + i);
        }
    }

    @Test
    @DisplayName("NoteInfo: num continues across notes within a group (notesInSequence=2)")
    void noteInfo_numContinuesAcrossGroup() {
        NoteSequenceEmitter emitter = new NoteSequenceEmitter(60, 2, 1, false, true);
        List<NoteInfo[]> values = emitter.createObservable(MELODY_4).test().values();

        // group 0 → nums 0, 1;  group 1 → nums 2, 3
        assertEquals(0L, values.get(0)[0].num);
        assertEquals(1L, values.get(0)[1].num);
        assertEquals(2L, values.get(1)[0].num);
        assertEquals(3L, values.get(1)[1].num);
    }

    @Test
    @DisplayName("NoteInfo: longitude matches getLongitude()")
    void noteInfo_longitudeMatchesCalculated() {
        NoteSequenceEmitter emitter = new NoteSequenceEmitter(120, 1, 1, false, true);
        NoteInfo note = emitter.createObservable(MELODY_3).test().values().get(0)[0];
        assertEquals(emitter.getLongitude(), note.longitude);
    }

    @Test
    @DisplayName("NoteInfo: isPlayWithScale and isHighlighted are set correctly")
    void noteInfo_flagsAreSetCorrectly() {
        NoteSequenceEmitter emitter = new NoteSequenceEmitter(60, 1, 1, true, true);
        NoteInfo note = emitter.createObservable(MELODY_3).test().values().get(0)[0];

        assertTrue(note.isPlayWithScale);
        assertTrue(note.isHighlighted);

        NoteSequenceEmitter emitter2 = new NoteSequenceEmitter(60, 1, 5, false, false);
        NoteInfo note2 = emitter2.createObservable(MELODY_3).test().values().get(0)[0];

        assertFalse(note2.isPlayWithScale);
        assertFalse(note2.isHighlighted);
    }

    @Test
    @DisplayName("NoteInfo: pressedNote is null on emission (user hasn't pressed anything yet)")
    void noteInfo_pressedNoteIsNullOnEmission() {
        NoteSequenceEmitter emitter = new NoteSequenceEmitter(60, 1, 1, false, true);
        NoteInfo note = emitter.createObservable(MELODY_3).test().values().get(0)[0];
        assertNotNull(note); // emitted
        assertEquals(null, note.pressedNote);
    }
}

