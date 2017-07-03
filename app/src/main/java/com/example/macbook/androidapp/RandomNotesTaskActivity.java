package com.example.macbook.androidapp;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RandomNotesTaskActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private MidiSupport midiSupport;
    private StatisticsStorage statisticsStorage;
    private Drawable buttonDefaultBackground;
    private boolean isStarted = false;
    private HashMap<String, Button> noteButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_notes_task);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.task_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        noteButtons = buildNoteButtonsMap();
        Button button = (Button) findViewById(R.id.buttonC);
        buttonDefaultBackground = button.getBackground().getCurrent();
        spinner.setOnItemSelectedListener(this);
    }

    private HashMap<String, Button> buildNoteButtonsMap() {
        HashMap<String, Button> noteButtons = new HashMap<>();
        noteButtons.put("C", (Button) findViewById(R.id.buttonC));
        noteButtons.put("D", (Button) findViewById(R.id.buttonD));
        noteButtons.put("E", (Button) findViewById(R.id.buttonE));
        noteButtons.put("F", (Button) findViewById(R.id.buttonF));
        noteButtons.put("G", (Button) findViewById(R.id.buttonG));
        noteButtons.put("A", (Button) findViewById(R.id.buttonA));
        noteButtons.put("B", (Button) findViewById(R.id.buttonB));
        noteButtons.put("C2", (Button) findViewById(R.id.buttonC2));
        return noteButtons;
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
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        String taskText = spinner.getSelectedItem().toString();
        for(String note : noteButtons.keySet()) {
            noteButtons.get(note).setEnabled(false);
        }
        for (String note : taskText.split(" ")) {
            noteButtons.get(note).setEnabled(true);
        }
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
        statisticsStorage = new StatisticsStorage();
        getMidiSupport().playNotesInRandomOrder(melody);
    }

    public void onNoteClick(View view) {
        Button button = (Button) view;
        String text = button.getText().toString();
        TextView textView = (TextView) findViewById(R.id.answerResult);
        if (getMidiSupport().getCurrentNote() == null) {
            return;
        }
        int color;
        if (text.equals(getMidiSupport().getCurrentNote().getName())) {
            color = ContextCompat.getColor(this, R.color.green);
        } else {
            color = ContextCompat.getColor(this, R.color.red);
        }
        statisticsStorage.submitAnswer(
                getMidiSupport().getCurrentNoteNumber(),
                getMidiSupport().getCurrentNote(),
                NotesEnum.valueOf(text));
        textView.setText("Correct " + statisticsStorage.getCorrectCount() +
                " Wrong " + statisticsStorage.getWrongCount() +
                " Missed " + statisticsStorage.getMissedCount());
        AnimationDrawable drawable = buildAnimationDrawable(color);
        button.setBackground(drawable);
        drawable.start();
        Log.i(Integer.toString(getMidiSupport().getCurrentNoteNumber()), "Pressed");
    }

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

    private AnimationDrawable buildAnimationDrawable(int color) {
        AnimationDrawable drawable = new AnimationDrawable();
        drawable.addFrame(new ColorDrawable(color), 150);
        drawable.addFrame(buttonDefaultBackground, 150);
        drawable.setOneShot(true);
        return drawable;
    }

    private MidiSupport getMidiSupport() {
        if (midiSupport == null) {
            midiSupport = new MidiSupport(this);
        }
        return midiSupport;
    }
}
