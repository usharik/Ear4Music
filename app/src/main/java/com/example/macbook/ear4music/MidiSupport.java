package com.example.macbook.ear4music;

import android.util.Log;
import org.billthefarmer.mididriver.MidiDriver;

/**
 * Created by macbook on 02.07.17.
 */
public class MidiSupport implements MidiDriver.OnMidiStartListener {
    private MidiDriver midiDriver;

    public MidiSupport() {
        this.midiDriver = new MidiDriver();
        this.midiDriver.setOnMidiStartListener(this);
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

    public void playNote(byte note, int longitude) {
        // Construct a note ON message for the middle C at maximum velocity on channel 1:
        byte event[] = new byte[3];
        event[0] = (byte) (0x90 | 0x00);  // 0x90 = note On, 0x00 = channel 1
        event[1] = note;  // 0x3C = middle C
        event[2] = (byte) 0x7F;  // 0x7F = the maximum velocity (127)

        // Send the MIDI event to the synthesizer.
        midiDriver.write(event);
        try {
            Thread.sleep(longitude);
        } catch (InterruptedException ex) {
            Log.d(getClass().getName(), Log.getStackTraceString(ex));
        }
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
