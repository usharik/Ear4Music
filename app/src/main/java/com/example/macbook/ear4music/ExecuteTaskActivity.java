package com.example.macbook.ear4music;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.util.Log;
import android.view.View;

import com.example.macbook.ear4music.databinding.ExecuteTaskActivityBinding;
import com.example.macbook.ear4music.framework.ViewActivity;
import com.example.macbook.ear4music.service.MidiSupport;
import com.example.macbook.ear4music.service.Utils;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class ExecuteTaskActivity extends ViewActivity<ExecuteTaskViewModel> {

    @Inject
    MidiSupport midiSupport;

    private ExecuteTaskActivityBinding binding;
    private Subject<NoteInfo> keyboardPublishSubject;
    private CompositeDisposable compositeDisposable;

    @Override
    protected Class<ExecuteTaskViewModel> getViewModelClass() {
        return ExecuteTaskViewModel.class;
    }

    @Override
    protected void onResume() {
        super.onResume();

        binding = DataBindingUtil.setContentView(this, R.layout.execute_task_activity);

        binding.pianoKeyboard.setPianoKeyboardListener((noteInfo) -> {
            keyboardPublishSubject.onNext(noteInfo);
            binding.answerResult.setText(getNoteCount());
        });

        midiSupport.start();

        ExecuteTaskViewModel viewModel = getViewModel();
        viewModel.setStarted(false);
        viewModel.syncWithAppState();
        setActivityTitle();

        binding.setViewModel(viewModel);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Log.i(getClass().getName(), "Resume");
    }

    private void setActivityTitle() {
        String taskName = String.format(
                getCurrentLocale(),
                "%d.%d %s",
                getViewModel().getTaskId(),
                getViewModel().getSubTaskId(),
                Utils.getSubTaskDescription(getResources(), getViewModel().getSubTask()));
        setTitle(taskName);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(getClass().getName(), "Start");
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (getViewModel().isStarted()) {
            if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
                compositeDisposable.dispose();
            }
        }

        binding.pianoKeyboard.setCurrentNoteInfo(null);
        midiSupport.stop();
        Log.i(getClass().getName(), "Pause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(getClass().getName(), "Stop");
    }

    private List<NotesEnum> getMelodyFromString(String str) {
        List<NotesEnum> melody = new ArrayList<>();
        for (String note : str.split(" ")) {
            melody.add(NotesEnum.valueOf(note));
        }
        return melody;
    }

    private void stopIfNeeded() {
        if (getViewModel().isStarted()) {
            if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
                compositeDisposable.dispose();
            }
            storeTaskResults();

            return;
        }
        getViewModel().setStarted(true);
    }

    public void onStartClick(View view) {
        stopIfNeeded();
        if (!getViewModel().isStarted()) {
            return;
        }

        showInstructionAndRunTask();
    }

    private String getMelody() {
        if (getViewModel().isWithNoteHighlighting()) {
            return getResources().getStringArray(R.array.task_list)[getViewModel().getSetOfNotesHighlightingId()];
        } else {
            return binding.tvSetOfNotes.getText().toString();
        }
    }

    private void runTask() {
        if (getViewModel().getSubTask().getNotesPerMinute() >= 40) {
            countDownDialog(this::runTaskIntern);
        } else {
            runTaskIntern();
        }
    }

    private void runTaskIntern() {
        final List<NotesEnum> melody = getMelodyFromString(getMelody());
        final int notesInSequence = binding.getViewModel().getNotesInSequence();

        keyboardPublishSubject = PublishSubject.create();
        Observable<NoteInfo[]> notesEmitterObservable = getViewModel().createNotesEmitterObservable(melody);
        compositeDisposable = new CompositeDisposable();

        getViewModel().resetStatisticsStorage();
        final StatisticsStorage statStore = getViewModel().getStatisticsStorage();
        if (notesInSequence == 1) {
            Disposable noteEmitterDisposable = notesEmitterObservable
                    .subscribeOn(Schedulers.io())
                    .doOnNext((notes) -> {
                        binding.pianoKeyboard.setCurrentNoteInfo(notes[0]);
                        runOnUiThread(this::invalidateUi);
                        if (notes[0].isPlayWithScale) {
                            midiSupport.playNoteWithScale(notes[0].note, notes[0].longitude);
                        } else {
                            midiSupport.playNote(notes[0].note, notes[0].longitude);
                        }
                        getViewModel().getStatisticsStorage().submitAnswer(notes[0]);
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete(this::onTaskComplete)
                    .doOnDispose(this::onTaskStop)
                    .subscribe();

            compositeDisposable.add(noteEmitterDisposable);
            compositeDisposable.add(keyboardPublishSubject.subscribe(statStore::submitAnswer));
        } else {
            Disposable noteEmitterDisposable = notesEmitterObservable
                    .subscribeOn(Schedulers.io())
                    .flatMap((notes) -> {
                        for (NoteInfo nt : notes) {
                            midiSupport.playNote(nt.note, nt.longitude);
                        }
                        return Observable.just(notes);
                    })
                    .flatMap(Observable::fromArray)
                    .doOnNext((noteInfo) -> {
                        binding.pianoKeyboard.setCurrentNoteInfo(noteInfo);
                        NoteInfo pressed = keyboardPublishSubject
                                .timeout(noteInfo.longitude, TimeUnit.MILLISECONDS, Observable.just(noteInfo))
                                .blockingFirst();
                        statStore.submitAnswer(pressed);
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete(this::onTaskComplete)
                    .doOnDispose(this::onTaskStop)
                    .subscribe();

            compositeDisposable.add(noteEmitterDisposable);
        }
    }

    private void countDownDialog(Runnable action) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        CountDownDialog.newInstance(action)
                .show(ft, "dialog");
    }

    private void invalidateUi() {
        binding.pianoKeyboard.invalidate();
        binding.answerResult.setText(getNoteCount());
    }

    private void onTaskComplete() {
        storeTaskResults();
        String message;
        if (getViewModel().getSubTask().getNotesInSequence() == 1) {
            message = getResources().getString(R.string.task_accomplished_with_time, getCorrectAnswerPercent(), getAvgAnswerTime());
        } else {
            message = getResources().getString(R.string.task_accomplished, getCorrectAnswerPercent());
        }
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(R.string.next_task, this::onNextTask)
                .setNegativeButton(R.string.repeat, this::onRepeatTask)
                .setNeutralButton(R.string.stop, (v1, v2) -> getViewModel().setStarted(false))
                .setCancelable(false)
                .show();
    }

    private void onNextTask(DialogInterface var1, int var2) {
        if (getViewModel().getSubTask().getNextSubTask() == null) {
            return;
        }
        getViewModel().setSubTask(getViewModel().getSubTask().getNextSubTask());
        setActivityTitle();
        showInstructionAndRunTask();
    }

    private void onRepeatTask(DialogInterface var1, int var2) {
        runTask();
    }

    private void onTaskStop() {
        getViewModel().setStarted(false);
        binding.buttonStart.setText(R.string.start);
        storeTaskResults();
    }

    private void showInstructionAndRunTask() {
        if (getViewModel().getInstructionId() == null) {
            runTask();
            return;
        }
        String[] taskInstr = getResources().getStringArray(R.array.task_instruction);
        new AlertDialog.Builder(this)
                .setMessage(String.format(taskInstr[getViewModel().getInstructionId()], getViewModel().getNotesPerMinute()))
                .setPositiveButton(R.string.ok, (v1, v2) -> runTask())
                .setCancelable(false)
                .show();
    }

    private void storeTaskResults() {
        getViewModel().getStatisticsStorage().calculate();
        binding.getViewModel().setCorrectAnswerPercent(getCorrectAnswerPercent());
    }

    private int getCorrectAnswerPercent() {
        if (getViewModel().isWithNoteHighlighting()) {
            return 100;
        } else {
            return getViewModel().getStatisticsStorage().getCorrectPercent();
        }
    }

    private long getAvgAnswerTime() {
        return  getViewModel().getStatisticsStorage().getAvgAnswerTime();
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

    private String getNoteCount() {
        StatisticsStorage st = getViewModel().getStatisticsStorage();
        return getResources().getString(R.string.progress,
                st.getOverallCount(),
                st.getCorrectCount(),
                st.getWrongCount(),
                st.getMissedCount());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
