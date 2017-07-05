package com.example.macbook.ear4music;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.example.macbook.ear4music.listner.MidiSupportListener;
import com.example.macbook.ear4music.listner.PianoKeyboardListener;
import com.example.macbook.ear4music.widget.PianoKeyboard;

import java.util.ArrayList;
import java.util.List;

public class RandomNotesTaskActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener, MidiSupportListener, PianoKeyboardListener, CompoundButton.OnCheckedChangeListener {

    private MidiSupport midiSupport;
    private StatisticsStorage statisticsStorage;
    private boolean isStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_notes_task);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        Switch aSwitch = (Switch) findViewById(R.id.switchNoteNames);
        aSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMidiSupport().start();
        Log.i("RandomNotesTaskActivity", "!!!!!!!!!!!!! Resume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("RandomNotesTaskActivity", "!!!!!!!!!!!!! Start");
    }

    @Override
    protected void onPause() {
        super.onPause();
        getMidiSupport().stopPlayingAsync();
        PianoKeyboard pianoKeyboard = (PianoKeyboard) findViewById(R.id.piano_keyboard);
        pianoKeyboard.setCurrentNote(null);
        getMidiSupport().stop();

        Log.i("RandomNotesTaskActivity", "!!!!!!!!!!!!! Pause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("RandomNotesTaskActivity", "!!!!!!!!!!!!! Stop");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void onStartClick(View view) {
        Button button = (Button) findViewById(R.id.buttonStart);
        if (isStarted) {
            getMidiSupport().stopPlayingAsync();
            isStarted = false;
            button.setText("Start");
            return;
        }
        isStarted = true;
        button.setText("Stop");
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        String taskText = spinner.getSelectedItem().toString();
        List<NotesEnum> melody = new ArrayList<>();
        for (String note : taskText.split(" ")) {
            melody.add(NotesEnum.valueOf(note));
        }
        Spinner freqEdit = (Spinner) findViewById(R.id.spinnerFreq);
        int freq = Integer.parseInt(freqEdit.getSelectedItem().toString());
        statisticsStorage = new StatisticsStorage();
        getMidiSupport().playNotesInRandomOrder(melody, freq);
        PianoKeyboard pianoKeyboard = (PianoKeyboard) findViewById(R.id.piano_keyboard);
        pianoKeyboard.setStatisticsStorage(statisticsStorage);
        pianoKeyboard.setPianoKeyboardListener(this);
    }

    @Override
    public void onNewNote(NotesEnum currentNote) {
        PianoKeyboard pianoKeyboard = (PianoKeyboard) findViewById(R.id.piano_keyboard);
        pianoKeyboard.setCurrentNote(currentNote);
        pianoKeyboard.setCurrentNoteNumber(getMidiSupport().getCurrentNoteNumber());
    }

    @Override
    public void onMissedAnswer(int noteNumber) {
        statisticsStorage.submitAnswer(
                noteNumber,
                getMidiSupport().getCurrentNote(),
                null);
        TextView textView = (TextView) findViewById(R.id.answerResult);
        textView.setText("Correct " + statisticsStorage.getCorrectCount() +
                " Wrong " + statisticsStorage.getWrongCount() +
                " Missed " + statisticsStorage.getMissedCount());
        Log.i(Integer.toString(getMidiSupport().getCurrentNoteNumber()), "Missed");
    }

    @Override
    public void onCorrectNotePressed() {
        TextView textView = (TextView) findViewById(R.id.answerResult);
        textView.setText("Correct " + statisticsStorage.getCorrectCount() +
                " Wrong " + statisticsStorage.getWrongCount() +
                " Missed " + statisticsStorage.getMissedCount());
    }

    private MidiSupport getMidiSupport() {
        if (midiSupport == null) {
            midiSupport = new MidiSupport(this);
        }
        return midiSupport;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        PianoKeyboard pianoKeyboard = (PianoKeyboard) findViewById(R.id.piano_keyboard);
        pianoKeyboard.setShowNoteNames(b);
    }
}
