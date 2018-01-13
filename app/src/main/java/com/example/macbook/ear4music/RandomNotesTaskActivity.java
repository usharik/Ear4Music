package com.example.macbook.ear4music;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.example.macbook.ear4music.listner.PianoKeyboardListener;
import com.example.macbook.ear4music.widget.PianoKeyboard;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class RandomNotesTaskActivity extends AppCompatActivity
        implements PianoKeyboardListener, CompoundButton.OnCheckedChangeListener {

    private MidiSupport midiSupport;
    private StatisticsStorage statisticsStorage;
    private boolean isStarted;
    private AtomicBoolean stop;
    private int taskId;
    private Subject<NoteInfo> keyboardBehaviorSubject;
    private Disposable noteEmitterDisposable;
    private Disposable keyboardDisposable;
    private AtomicLong noteNumber = new AtomicLong();

    public static class NoteInfo {
        public long num;
        public NotesEnum note;
        public NotesEnum pressedNote;

        public NoteInfo(Long num, NotesEnum note, NotesEnum pressedNote) {
            this.num = num;
            this.note = note;
            this.pressedNote = pressedNote;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.midiSupport = new MidiSupport();
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
                Log.i(getClass().getName(), "pos " + pos + " id " + id);
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
        PianoKeyboard pianoKeyboard = (PianoKeyboard) findViewById(R.id.piano_keyboard);
        pianoKeyboard.setCurrentNoteInfo(null);
        midiSupport.stop();
        Log.i(getClass().getName(), "Pause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(getClass().getName(), "Stop");
    }

    private int getNumberFromSpinner(Spinner spinner) {
        return Integer.parseInt(spinner.getSelectedItem().toString());
    }

    public void onStartClick(View view) {
        Button button = (Button) findViewById(R.id.buttonStart);
        if (isStarted) {
            isStarted = false;
            button.setText(R.string.start);
            if (noteEmitterDisposable != null && !noteEmitterDisposable.isDisposed()) {
                noteEmitterDisposable.dispose();
            }
            if (keyboardDisposable != null && !keyboardDisposable.isDisposed()) {
                keyboardDisposable.dispose();
            }
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
        final int freq = getNumberFromSpinner((Spinner) findViewById(R.id.spinnerFreq));
        final int notesInSequence = getNumberFromSpinner((Spinner) findViewById(R.id.spinnerTaskType));
        statisticsStorage = new StatisticsStorage();

        final int longitude = (int) Math.round(60000.0 / freq);
        Observable<Long> timer = Observable.interval(longitude, TimeUnit.MILLISECONDS);
        keyboardBehaviorSubject = PublishSubject.create();
        final RandomNoteGenerator randomNoteGenerator = new RandomNoteGenerator(melody);

        if (notesInSequence == 1) {
            noteEmitterDisposable = timer.subscribe(new Consumer<Long>() {
                @Override
                public void accept(Long num) throws Exception {
                    NoteInfo noteInfo = new NoteInfo(num, randomNoteGenerator.nextNote(), null);
                    PianoKeyboard pianoKeyboard = (PianoKeyboard) findViewById(R.id.piano_keyboard);
                    pianoKeyboard.setCurrentNoteInfo(noteInfo);
                    midiSupport.playNote(noteInfo.note.getPitch(), longitude);
                    statisticsStorage.submitAnswer(noteInfo);
                }
            });

            keyboardDisposable = keyboardBehaviorSubject.subscribe(new Consumer<NoteInfo>() {
                @Override
                public void accept(NoteInfo noteInfo) throws Exception {
                    statisticsStorage.submitAnswer(noteInfo);
                }
            });
        } else {
            noteNumber.set(0);
            Observable<NoteInfo[]> observable = Observable.create(new ObservableOnSubscribe<NoteInfo[]>() {
                @Override
                public void subscribe(ObservableEmitter<NoteInfo[]> e) throws Exception {
                    while(isStarted) {
                        NoteInfo[] notes = new NoteInfo[notesInSequence];
                        for (int i = 0; i < notesInSequence; i++) {
                            notes[i] = new NoteInfo(noteNumber.getAndIncrement(), randomNoteGenerator.nextNote(), null);
                            midiSupport.playNote(notes[i].note.getPitch(), longitude);
                        }
                        e.onNext(notes);
                    }
                }
            });
            noteEmitterDisposable = observable
                    .subscribeOn(Schedulers.io())
                    .flatMap(new Function<NoteInfo[], Observable<NoteInfo>>() {
                        @Override
                        public Observable<NoteInfo> apply(NoteInfo[] noteInfos) throws Exception {
                            return Observable.fromArray(noteInfos);
                        }
                    })
                    .subscribe(new Consumer<NoteInfo>() {
                        @Override
                        public void accept(final NoteInfo noteInfo) throws Exception {
                            PianoKeyboard pianoKeyboard = (PianoKeyboard) findViewById(R.id.piano_keyboard);
                            pianoKeyboard.setCurrentNoteInfo(noteInfo);
                            NoteInfo pressed = keyboardBehaviorSubject
                                    .timeout(longitude, TimeUnit.MILLISECONDS, Observable.just(noteInfo))
                                    .blockingFirst();
                            statisticsStorage.submitAnswer(pressed);
                        }
                    });
        }

        PianoKeyboard pianoKeyboard = (PianoKeyboard) findViewById(R.id.piano_keyboard);
        pianoKeyboard.setPianoKeyboardListener(this);
    }

    private void startResultActivity() {
        statisticsStorage.calculate();
        List<String[]> resultTable = new ArrayList<>();
        HashMap<NotesEnum, StatisticsStorage.Result> resultHashMap = statisticsStorage.calcFinalResult();
        for (NotesEnum key : resultHashMap.keySet()) {
            StatisticsStorage.Result res = resultHashMap.get(key);
            float sum = (res.correct + res.wrong + res.missed) / 100f;
            Locale current = getCurrentLocale();
            resultTable.add(new String[]{
                    key.name(),
                    String.format(current, "%d/%.1f%%", res.correct, res.correct / sum),
                    String.format(current, "%d/%.1f%%", res.wrong, res.wrong / sum),
                    String.format(current, "%d/%.1f%%", res.missed, res.missed / sum)});
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
    public void onNotePressed(NoteInfo noteInfo) {
        keyboardBehaviorSubject.onNext(noteInfo);
        TextView textView = (TextView) findViewById(R.id.answerResult);
        textView.setText(getNoteCount());
    }

    private String getNoteCount() {
        return "Overall " + statisticsStorage.getOverallCount() +
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
