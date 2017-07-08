package com.example.macbook.ear4music;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.example.macbook.ear4music.listner.MidiSupportListener;
import com.example.macbook.ear4music.listner.PianoKeyboardListener;
import com.example.macbook.ear4music.widget.PianoKeyboard;

import java.util.HashMap;

public class CountOfRandomNotesTaskActivity extends AppCompatActivity
        implements MidiSupportListener, PianoKeyboardListener,
        CompoundButton.OnCheckedChangeListener {

    private MidiSupport midiSupport;
    private StatisticsStorage statisticsStorage;
    private boolean isStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_of_random_notes_task);

        this.midiSupport = new MidiSupport(this);
        this.isStarted = false;
        Switch aSwitch = (Switch) findViewById(R.id.switchNoteNames);
        aSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        midiSupport.start();
        Log.i(getClass().getName(), "Resume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(getClass().getName(), "Start");
    }

    @Override
    protected void onPause() {
        super.onPause();
        midiSupport.stopPlayingAsync();
        PianoKeyboard pianoKeyboard = (PianoKeyboard) findViewById(R.id.piano_keyboard);
        pianoKeyboard.setCurrentNote(null);
        midiSupport.stop();
        Log.i(getClass().getName(), "Pause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(getClass().getName(), "Stop");
    }

    public void onStartClick(View view) {
        Button button = (Button) findViewById(R.id.buttonStart);
        if (isStarted) {
            midiSupport.stopPlayingAsync();
            isStarted = false;
            button.setText(R.string.start);
            TextView textViewResult = (TextView) findViewById(R.id.textViewResult);
            if (statisticsStorage != null) {
                StringBuilder sb = new StringBuilder();
                HashMap<NotesEnum, StatisticsStorage.Result> resultHashMap = statisticsStorage.calcFinalResult();
                for (NotesEnum key : resultHashMap.keySet()) {
                    StatisticsStorage.Result res = resultHashMap.get(key);
                    sb.append(key.name() + " " + res.correct + " " + res.missed + " " + res.wrong + "\n");
                }
                textViewResult.setText(sb.toString());
            }
            return;
        }
        isStarted = true;
        button.setText(R.string.stop);
        Spinner freqEdit = (Spinner) findViewById(R.id.spinnerFreq);
        int freq = Integer.parseInt(freqEdit.getSelectedItem().toString());
        statisticsStorage = new StatisticsStorage();
        Spinner countEdit = (Spinner) findViewById(R.id.spinnerNoteCount);
        int count = Integer.parseInt(countEdit.getSelectedItem().toString());
        midiSupport.playCountOfRandomNotes(NotesEnum.whiteList, freq, count);
        PianoKeyboard pianoKeyboard = (PianoKeyboard) findViewById(R.id.piano_keyboard);
        pianoKeyboard.setStatisticsStorage(statisticsStorage);
        pianoKeyboard.setPianoKeyboardListener(this);
    }

    @Override
    public void onNewNote(NotesEnum currentNote) {
        TextView textViewResult = (TextView) findViewById(R.id.textViewResult);
        textViewResult.setText("onNewNote note " + currentNote.name());
        PianoKeyboard pianoKeyboard = (PianoKeyboard) findViewById(R.id.piano_keyboard);
        pianoKeyboard.setCurrentNote(currentNote);
        pianoKeyboard.setCurrentNoteNumber(midiSupport.getCurrentNoteNumber());
    }

    @Override
    public void onMissedNote(int noteNumber) {
        statisticsStorage.submitAnswer(
                noteNumber,
                midiSupport.getCurrentNote(),
                null);
        TextView textView = (TextView) findViewById(R.id.answerResult);
        textView.setText("Correct " + statisticsStorage.getCorrectCount() +
                " Wrong " + statisticsStorage.getWrongCount() +
                " Missed " + statisticsStorage.getMissedCount());
    }

    @Override
    public void onNotePressed(NotesEnum pressedNote) {
        TextView textViewResult = (TextView) findViewById(R.id.textViewResult);
        textViewResult.setText("onNotePressed note " + pressedNote.name());
        TextView textView = (TextView) findViewById(R.id.answerResult);
        textView.setText("Correct " + statisticsStorage.getCorrectCount() +
                " Wrong " + statisticsStorage.getWrongCount() +
                " Missed " + statisticsStorage.getMissedCount());
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        PianoKeyboard pianoKeyboard = (PianoKeyboard) findViewById(R.id.piano_keyboard);
        pianoKeyboard.setShowNoteNames(b);
    }
}

