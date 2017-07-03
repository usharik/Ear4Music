package com.example.macbook.androidapp;

import android.os.Handler;
import android.util.Log;
import org.billthefarmer.mididriver.MidiDriver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * Created by macbook on 02.07.17.
 */
public class MidiSupport implements MidiDriver.OnMidiStartListener {
    private MidiDriver midiDriver;
    private RandomNotesTaskActivity randomNotesTaskActivity;
    private volatile NotesEnum currentNote;
    private volatile int currentNoteNumber;
    private Thread currentThread;

    public MidiSupport(RandomNotesTaskActivity randomNotesTaskActivity) {
        midiDriver = new MidiDriver();
        midiDriver.setOnMidiStartListener(this);
        this.randomNotesTaskActivity = randomNotesTaskActivity;
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
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    for (NotesEnum note : internMelody) {
                        if (Thread.interrupted()) {
                            break;
                        }
                        currentNote = note;
                        playNote(note.getPitch());
                    }
                } catch (InterruptedException ex) {
                    Log.i(getClass().getName(), "Playing interrupted");
                }
            }
        };
        currentThread = new Thread(runnable);
        currentThread.start();
    }

    public void playNotesInRandomOrder(List<NotesEnum> notes) {
        currentNoteNumber = 1;
        final List<NotesEnum> internNotes = new ArrayList<>(notes);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Random rnd = new Random();
                    HashSet<Integer> twoLastNotes = new HashSet<>();
                    while (!Thread.interrupted()) {
                        int nt = rnd.nextInt(internNotes.size());
                        if (twoLastNotes.size() < 2) {
                            twoLastNotes.add(nt);
                        } else {
                            while (twoLastNotes.contains(nt)) nt = rnd.nextInt(internNotes.size());
                            twoLastNotes.clear();
                        }
                        currentNote = internNotes.get(nt);
                        playNote(currentNote.getPitch());
                        final int noteNumber = currentNoteNumber;
                        randomNotesTaskActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                randomNotesTaskActivity.onMissedAnswer(noteNumber);
                            }
                        });
                        synchronized (this) {
                            currentNoteNumber++;
                        }
                    }
                } catch (InterruptedException ex) {
                    Log.i(getClass().getName(), "Playing interrupted");
                }
            }
        };
        currentThread = new Thread(runnable);
        currentThread.start();
    }

    public void stopPlayingAsync() {
        currentThread.interrupt();
        try {
            currentThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public NotesEnum getCurrentNote() {
        return currentNote;
    }

    public int getCurrentNoteNumber() {
        synchronized (this) {
            return currentNoteNumber;
        }
    }

    public void playNote(byte note) throws InterruptedException {
        // Construct a note ON message for the middle C at maximum velocity on channel 1:
        byte event[] = new byte[3];
        event[0] = (byte) (0x90 | 0x00);  // 0x90 = note On, 0x00 = channel 1
        event[1] = note;  // 0x3C = middle C
        event[2] = (byte) 0x7F;  // 0x7F = the maximum velocity (127)

        // Send the MIDI event to the synthesizer.
        midiDriver.write(event);
        Thread.sleep(1500);
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
