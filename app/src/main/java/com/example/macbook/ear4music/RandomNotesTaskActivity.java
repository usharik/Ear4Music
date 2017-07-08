package com.example.macbook.ear4music;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.example.macbook.ear4music.listner.MidiSupportListener;
import com.example.macbook.ear4music.listner.PianoKeyboardListener;
import com.example.macbook.ear4music.widget.PianoKeyboard;
import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class RandomNotesTaskActivity extends AppCompatActivity
        implements MidiSupportListener,
        PianoKeyboardListener, CompoundButton.OnCheckedChangeListener {

    private MidiSupport midiSupport;
    private StatisticsStorage statisticsStorage;
    private boolean isStarted;
    private int taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.midiSupport = new MidiSupport(this);
        this.isStarted = false;
        this.taskId = 1;
        setContentView(R.layout.activity_random_notes_task);
        Switch aSwitch = (Switch) findViewById(R.id.switchNoteNames);
        aSwitch.setOnCheckedChangeListener(this);

        Spinner taskTypeSpinner = (Spinner) findViewById(R.id.spinnerTaskType);
        taskTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                taskId = (int) id;
                Log.i(getClass().getName(), "pos " + pos + " id " +id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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
            if (statisticsStorage != null) {
                startResultActivity();
            }
            return;
        }
        isStarted = true;
        button.setText(R.string.stop);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        String taskText = spinner.getSelectedItem().toString();
        List<NotesEnum> melody = new ArrayList<>();
        for (String note : taskText.split(" ")) {
            melody.add(NotesEnum.valueOf(note));
        }
        Spinner freqEdit = (Spinner) findViewById(R.id.spinnerFreq);
        int freq = Integer.parseInt(freqEdit.getSelectedItem().toString());
        statisticsStorage = new StatisticsStorage();
        if (taskId == 0) {
            midiSupport.playNotesInRandomOrder(melody, freq);
        } else {
            midiSupport.playCountOfRandomNotes(melody, freq, taskId+1);
        }
        PianoKeyboard pianoKeyboard = (PianoKeyboard) findViewById(R.id.piano_keyboard);
        pianoKeyboard.setStatisticsStorage(statisticsStorage);
        pianoKeyboard.setPianoKeyboardListener(this);
    }

    private void startResultActivity() {
        List<String[]> resultTable = new ArrayList<>();
        HashMap<NotesEnum, StatisticsStorage.Result> resultHashMap = statisticsStorage.calcFinalResult();
        for (NotesEnum key : resultHashMap.keySet()) {
            StatisticsStorage.Result res = resultHashMap.get(key);
            float sum = (res.correct + res.wrong + res.missed)/100f;
            Locale current = getCurrentLocale();
            resultTable.add(new String[] {
                    key.name(),
                    String.format(current, "%d/%.1f%%", res.correct, res.correct/sum),
                    String.format(current, "%d/%.1f%%", res.wrong, res.wrong/sum),
                    String.format(current, "%d/%.1f%%", res.missed, res.missed/sum)});
        }
        String[][] arr = new String[resultTable.size()][];
        arr = resultTable.toArray(arr);
        Intent intent = new Intent(getApplicationContext(), TaskResultActivity.class);
        intent.putExtra("taskResult", arr);
        startActivity(intent);
    }

    private Locale getCurrentLocale() {
        Locale locale;
        Context applicationContext = getApplicationContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = applicationContext.getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = applicationContext.getResources().getConfiguration().locale;
        }
        return locale;
    }

    @Override
    public void onNewNote(NotesEnum currentNote) {
        Log.i(getClass().getName(), "New" + currentNote.name());
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
        textView.setText(getNoteCount());
    }

    @Override
    public void onNotePressed(NotesEnum pressedNote) {
        Log.i(getClass().getName(), "Pressed" + pressedNote.name());
        TextView textViewResult = (TextView) findViewById(R.id.textViewResult);
        textViewResult.setText("onNotePressed note " + pressedNote.name());
        TextView textView = (TextView) findViewById(R.id.answerResult);
        textView.setText(getNoteCount());
    }

    private String getNoteCount() {
        return  "Overall " + statisticsStorage.getOverallCount() +
                " Correct " + statisticsStorage.getCorrectCount() +
                " Wrong " + statisticsStorage.getWrongCount() +
                " Missed " + statisticsStorage.getMissedCount();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        PianoKeyboard pianoKeyboard = (PianoKeyboard) findViewById(R.id.piano_keyboard);
        pianoKeyboard.setShowNoteNames(b);
    }
}
