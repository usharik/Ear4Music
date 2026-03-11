package com.example.macbook.ear4music.service;

import android.util.Log;
import com.example.macbook.ear4music.NotesEnum;
import org.billthefarmer.mididriver.MidiDriver;

/**
 * Created by macbook on 02.07.17.
 */
public class MidiSupport implements MidiDriver.OnMidiStartListener {
    private final MidiDriver midiDriver;
    private Runnable afterMidiStarted;

    public MidiSupport() {
        this.midiDriver = MidiDriver.getInstance();
        this.midiDriver.setOnMidiStartListener(this);
    }

    public synchronized void start(Runnable afterMidiStarted) {
        this.afterMidiStarted = afterMidiStarted;
        midiDriver.start();
        int[] config = midiDriver.config();
        Log.d(getClass().getName(), "Midi started");
        Log.d(getClass().getName(), "maxVoices: " + config[0]);
        Log.d(getClass().getName(), "numChannels: " + config[1]);
        Log.d(getClass().getName(), "sampleRate: " + config[2]);
        Log.d(getClass().getName(), "mixBufferSize: " + config[3]);
    }

    public synchronized void stop() {
        midiDriver.stop();
    }

    public void playNote(NotesEnum note, int longitude) {
        // Construct a note ON message for the middle C at maximum velocity on channel 1:
        byte[] event = new byte[3];
        event[0] = (byte) (0x90 | 0x00);  // 0x90 = note On, 0x00 = channel 1
        event[1] = note.getPitch();  // 0x3C = middle C
        event[2] = (byte) 0x7F;  // 0x7F = the maximum velocity (127)

        // Send the MIDI event to the synthesizer.
        midiDriver.write(event);
        if (!pause(longitude)) {
            stopNote(note.getPitch());
            return;
        }
        stopNote(note.getPitch());
    }

    private void stopNote(byte note) {
        // Construct a note OFF message for the middle C at minimum velocity on channel 1:
        byte[] event = new byte[3];
        event[0] = (byte) (0x80 | 0x00);  // 0x80 = note Off, 0x00 = channel 1
        event[1] = note;  // 0x3C = middle C
        event[2] = (byte) 0x00;  // 0x00 = the minimum velocity (0)

        // Send the MIDI event to the synthesizer.
        midiDriver.write(event);
    }

    public void playNoteWithScale(NotesEnum note, int longitude) {
        double rest = 1.0;
        int scaleLng = longitude/2;
        for (NotesEnum.Note nt : NotesEnum.getScale4Note(note)) {
            if (Thread.currentThread().isInterrupted()) {
                return;
            }
            rest-=nt.longitude;
            playNote(nt.note, (int)(scaleLng * nt.longitude));
            if (Thread.currentThread().isInterrupted()) {
                return;
            }
            Log.d(getClass().getName(),"Note " + nt.note + " lng " + (int)(scaleLng * nt.longitude));
        }
        pause((int)(scaleLng * (rest + 1)));
    }

    private boolean pause(int longitude) {
        try {
            Thread.sleep(longitude);
            return true;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            Log.d(getClass().getName(), "Midi playback interrupted");
            return false;
        }
    }

    @Override
    public void onMidiStart() {
        if (afterMidiStarted != null) {
            afterMidiStarted.run();
        }
    }
}
