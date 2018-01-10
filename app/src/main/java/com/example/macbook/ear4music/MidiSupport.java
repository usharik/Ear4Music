package com.example.macbook.ear4music;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import org.billthefarmer.mididriver.MidiDriver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by macbook on 02.07.17.
 */
public class MidiSupport implements MidiDriver.OnMidiStartListener {
    private MidiDriver midiDriver;
    private volatile NotesEnum currentNote;
    private AtomicInteger currentNoteNumber;
    private Thread currentThread;

    public MidiSupport() {
        this.midiDriver = new MidiDriver();
        this.midiDriver.setOnMidiStartListener(this);
        this.currentNoteNumber = new AtomicInteger(0);
    }

    public void start() {
        midiDriver.start();
        int[] config = midiDriver.config();
        Log.d(getClass().getName(), "Midi started");
        Log.d(getClass().getName(), "maxVoices: " + config[0]);
        Log.d(getClass().getName(), "numChannels: " + config[1]);
        Log.d(getClass().getName(), "sampleRate: " + config[2]);
        Log.d(getClass().getName(), "mixBufferSize: " + config[3]);
    }

    public void stop() {
        midiDriver.stop();
    }

    public void playNotesAsync(List<NotesEnum> melody) {
        final List<NotesEnum> internMelody = new ArrayList<>(melody);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    for (NotesEnum note : internMelody) {
                        if (Thread.interrupted()) {
                            break;
                        }
                        currentNote = note;
                        playNote(note.getPitch(), 1500);
                    }
                } catch (InterruptedException ex) {
                    Log.i(getClass().getName(), "Playing interrupted");
                }
            }
        };
        currentThread = new Thread(runnable);
        currentThread.start();
    }

    public Observable<RandomNotesTaskActivity.NoteInfo> newNotesPlayingObservable(final List<NotesEnum> notes, final int notesPerMin, final int notesCount, final AtomicBoolean stop) {
        return Observable.create(new ObservableOnSubscribe<RandomNotesTaskActivity.NoteInfo>() {
            @Override
            public void subscribe(ObservableEmitter<RandomNotesTaskActivity.NoteInfo> e) throws Exception {
                final int longitude = (int) Math.round(60000.0 / notesPerMin);
                RandomNoteGenerator randomNoteGenerator = new RandomNoteGenerator(notes);
                int cnt=0;
                while (!stop.get() && (notesCount == 0 || cnt < notesCount)) {
                    final int noteNumber = currentNoteNumber.getAndIncrement();
                    currentNote = randomNoteGenerator.nextNote();
                    e.onNext(new RandomNotesTaskActivity.NoteInfo(noteNumber, currentNote, null));
                    playNote(currentNote.getPitch(), longitude);
                    cnt++;
                }
                e.onComplete();
            }
        });
    }

    public void playNote(byte note, int longitude) throws InterruptedException {
        // Construct a note ON message for the middle C at maximum velocity on channel 1:
        byte event[] = new byte[3];
        event[0] = (byte) (0x90 | 0x00);  // 0x90 = note On, 0x00 = channel 1
        event[1] = note;  // 0x3C = middle C
        event[2] = (byte) 0x7F;  // 0x7F = the maximum velocity (127)

        // Send the MIDI event to the synthesizer.
        midiDriver.write(event);
        Thread.sleep(longitude);
        stopNote(note);
    }

    public void stopNote(byte note) {
        // Construct a note OFF message for the middle C at minimum velocity on channel 1:
        byte[] event = new byte[3];
        event[0] = (byte) (0x80 | 0x00);  // 0x80 = note Off, 0x00 = channel 1
        event[1] = note;  // 0x3C = middle C
        event[2] = (byte) 0x00;  // 0x00 = the minimum velocity (0)

        // Send the MIDI event to the synthesizer.
        midiDriver.write(event);
    }

    @Override
    public void onMidiStart() {

    }
}
