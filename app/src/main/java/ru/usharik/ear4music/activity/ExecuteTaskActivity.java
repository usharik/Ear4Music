package ru.usharik.ear4music.activity;

import android.app.Dialog;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import ru.usharik.ear4music.BuildConfig;
import ru.usharik.ear4music.dialog.CountDownDialog;
import ru.usharik.ear4music.NoteInfo;
import ru.usharik.ear4music.NotesEnum;
import ru.usharik.ear4music.R;
import ru.usharik.ear4music.dialog.StatisticsReportDialog;
import ru.usharik.ear4music.Utilities;
import ru.usharik.ear4music.databinding.ExecuteTaskActivityBinding;
import ru.usharik.ear4music.framework.BannerAdLoader;
import ru.usharik.ear4music.framework.ViewActivity;
import ru.usharik.ear4music.model.SubTask;
import ru.usharik.ear4music.service.MidiPlayer;
import ru.usharik.ear4music.service.MidiSupport;
import ru.usharik.ear4music.service.StatisticsStorage;
import ru.usharik.ear4music.service.SequenceFlowRunner;
import ru.usharik.ear4music.service.SingleNoteFlowRunner;
import ru.usharik.ear4music.service.Utils;
import ru.usharik.ear4music.widget.KeyPress;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;

import java.util.*;
import java.util.HashMap;

import javax.inject.Inject;

public class ExecuteTaskActivity extends ViewActivity<ExecuteTaskViewModel> {
    public static final String EXTRA_SUB_TASK_ID = "ru.usharik.ear4music.extra.SUB_TASK_ID";

    private record DialogActionSpec(int labelResId,
                                    boolean primary,
                                    Runnable action) {
    }

    @Inject
    MidiSupport midiSupport;

    private ExecuteTaskActivityBinding binding;
    private Subject<KeyPress> keyboardPublishSubject;
    private Subject<Boolean> taskStatePublishSubject;
    private NoteInfo currentActiveNoteInfo;
    private CompositeDisposable compositeDisposable;
    private AdView bannerAdView;
    private InterstitialAd interstitialAd;
    private final Random random = new Random();
    private boolean startedAfterAd = false;
    private boolean isShowingInterstitial = false;

    @Override
    protected Class<ExecuteTaskViewModel> getViewModelClass() {
        return ExecuteTaskViewModel.class;
    }

    @Override
    protected void onResume() {
        super.onResume();

        taskStatePublishSubject = PublishSubject.create();

        taskStatePublishSubject.doOnNext((isStarted) -> {
            binding.buttonStart.setEnabled(true);
            getViewModel().setStarted(isStarted);
            if (isStarted) {
                midiSupport.start(this::showInstructionAndRunTask);
            }
        }).subscribe();

        binding = DataBindingUtil.setContentView(this, R.layout.execute_task_activity);
        setSupportActionBar(binding.toolbar);

        applySystemBarInsets(binding.toolbar, true, true, true, false);
        applySystemBarInsets(binding.contentContainer, true, false, true, false);
        applySystemBarInsetsAsMargin(binding.bannerContainer, false, false, false, true);

        binding.pianoKeyboard.setPianoKeyboardListener(keyPress -> {
            // keyboardPublishSubject is only initialized when a task is running.
            if (keyboardPublishSubject != null) {
                keyboardPublishSubject.onNext(keyPress);
            }
        });

        if (!isShowingInterstitial) {
            getViewModel().setStarted(false);
        }
        isShowingInterstitial = false;
        long subTaskId = getIntent().getLongExtra(EXTRA_SUB_TASK_ID, -1L);
        if (!getViewModel().syncWithSubTaskId(subTaskId)) {
            finish();
            return;
        }
        setActivityTitle();

        binding.setViewModel(getViewModel());
        loadBanner(binding.bannerContainer);
        loadInterstitialAd();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.answerResult.setText(getNoteCount());
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
    protected void onPause() {
        BannerAdLoader.destroy(binding.bannerContainer, bannerAdView);
        bannerAdView = null;
        super.onPause();
        stopTask();
        Log.i(getClass().getName(), "Pause");
    }

    private void loadBanner(FrameLayout container) {
        container.post(() -> bannerAdView = BannerAdLoader.loadAnchoredBanner(
                this,
                container,
                BuildConfig.ADMOB_EXECUTE_BANNER_AD_UNIT_ID,
                bannerAdView));
    }

    private void loadInterstitialAd() {
        String adUnitId = BuildConfig.ADMOB_AFTER_START_INTERSTITIAL_AD_UNIT_ID;
        InterstitialAd.load(this, adUnitId, new AdRequest.Builder().build(),
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd ad) {
                        interstitialAd = ad;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError error) {
                        interstitialAd = null;
                    }
                });
    }

    private void showInterstitialAdAndStartTask() {
        isShowingInterstitial = true;
        interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                interstitialAd = null;
                loadInterstitialAd();
                startedAfterAd = true;
                startTask();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError error) {
                isShowingInterstitial = false;
                interstitialAd = null;
                startTask();
            }

            @Override
            public void onAdShowedFullScreenContent() {
                interstitialAd = null;
            }
        });
        interstitialAd.show(this);
    }

    private List<NotesEnum> getMelodyFromString(String str) {
        List<NotesEnum> melody = new ArrayList<>();
        for (String note : str.split(" ")) {
            melody.add(NotesEnum.valueOf(note));
        }
        return melody;
    }

    private void startTask() {
        taskStatePublishSubject.onNext(true);
    }

    private void stopTask() {
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    public void onStartClick(View view) {
        Utilities.vibrate(this);
        binding.buttonStart.setEnabled(false);
        if (getViewModel().isStarted()) {
            stopTask();
        } else {
            int randomValue = random.nextInt(101);
            if (randomValue <= 40 && interstitialAd != null) {
                showInterstitialAdAndStartTask();
            } else {
                startTask();
            }
        }
    }

    private String getMelody() {
        if (getViewModel().isWithNoteHighlighting()) {
            return getResources().getStringArray(R.array.task_list)[getViewModel().getSetOfNotesHighlightingId()];
        } else {
            return binding.tvSetOfNotes.getText().toString();
        }
    }

    private void runTask() {
        if (startedAfterAd || getViewModel().getSubTask().getNotesPerMinute() >= 40) {
            startedAfterAd = false;
            countDownDialog(() -> runTaskIntern(binding.getViewModel().getNotesInSequence(), getMelodyFromString(getMelody())));
        } else {
            runTaskIntern(binding.getViewModel().getNotesInSequence(), getMelodyFromString(getMelody()));
        }
    }

    private void runTaskIntern(
            final int notesInSequence,
            final List<NotesEnum> melody) {
        keyboardPublishSubject = PublishSubject.create();
        Observable<NoteInfo[]> notesEmitterObservable = getViewModel().createNotesEmitterObservable(melody);
        compositeDisposable = new CompositeDisposable();

        final StatisticsStorage statStore = getViewModel().getStatisticsStorage();
        statStore.reset();
        binding.progressBar.setProgress(0);

        MidiPlayer midiPlayer = MidiPlayer.create(
                (note, longitude) -> midiSupport.playNote(note, longitude),
                (note, longitude) -> midiSupport.playNoteWithScale(note, longitude)
        );

        if (notesInSequence == 1) {
            SingleNoteFlowRunner singleNoteRunner = new SingleNoteFlowRunner(
                    midiPlayer,
                    // onNoteActive
                    noteInfo -> runOnUiThread(() -> {
                        currentActiveNoteInfo = noteInfo;
                        binding.pianoKeyboard.setCurrentNoteInfo(noteInfo);
                        binding.tvExpectedNote.setText(noteInfo.note.name());
                        invalidatePianoKeyboard();
                    }),
                    // onProgressUpdated
                    noteInfo -> runOnUiThread(() -> updateProgressViews(noteInfo)),
                    statStore,
                    Schedulers.io(),
                    AndroidSchedulers.mainThread());
            compositeDisposable.add(
                    singleNoteRunner.buildFlow(notesEmitterObservable, this::onTaskComplete, this::onTaskStop));
            // Adapter: join the raw KeyPress with the current prompt NoteInfo to form a judged answer.
            compositeDisposable.add(keyboardPublishSubject.subscribe(keyPress -> {
                NoteInfo active = currentActiveNoteInfo;
                if (active != null) {
                    statStore.submitAnswer(active.num, active.note, keyPress.pressedNote(), active.time);
                }
                // If active is null the task has just been stopped; silently ignore the stale press.
            }));
        } else {
            SequenceFlowRunner sequenceRunner = new SequenceFlowRunner(
                    midiPlayer,
                    // onSequenceGroupStarted
                    () -> runOnUiThread(() -> {
                        // Sequence mode: group started (playing notes)
                        currentActiveNoteInfo = null;
                        binding.pianoKeyboard.setCurrentNoteInfo(null);
                        binding.tvExpectedNote.setText("");
                        binding.tvTaskPlayedIndicator.setText("SEQ_PLAYING");
                    }),
                    // onSequenceNoteActive
                    noteInfo -> runOnUiThread(() -> {
                        // Sequence mode: notes played, now waiting for answer
                        currentActiveNoteInfo = noteInfo;
                        binding.pianoKeyboard.setCurrentNoteInfo(noteInfo);
                        binding.tvExpectedNote.setText(noteInfo.note.name());
                        binding.tvTaskPlayedIndicator.setText("SEQ_PLAYED");
                    }),
                    // onProgressUpdated
                    noteInfo -> runOnUiThread(() -> updateProgressViews(noteInfo)),
                    statStore,
                    keyboardPublishSubject,
                    Schedulers.io(),
                    AndroidSchedulers.mainThread());
            compositeDisposable.add(
                    sequenceRunner.buildFlow(notesEmitterObservable, this::onTaskComplete, this::onTaskStop));
        }
    }

    private void countDownDialog(Runnable action) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        CountDownDialog.newInstance(action)
                .show(ft, "dialog");
    }

    private void invalidatePianoKeyboard() {
        binding.pianoKeyboard.invalidate();
        binding.answerResult.setText(getNoteCount());
    }

    private void updateProgressViews(NoteInfo noteInfo) {
        binding.progressBar.setProgress((int) (noteInfo.num+1) * 100 / getViewModel().getSequencesInSubTask() / getViewModel().getNotesInSequence());
        binding.answerResult.setText(getNoteCount());
    }

    private void onTaskComplete() {
        storeTaskResults();
        showStatisticsReportDialog();
    }

    private void showStatisticsReportDialog() {
        boolean showTime = getViewModel().getSubTask().getNotesInSequence() == 1;
        HashMap<NotesEnum, StatisticsStorage.Result> statistics =
                getViewModel().getStatisticsStorage().calcFinalResult();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        StatisticsReportDialog.newInstance(
                getCorrectAnswerPercent(),
                getAvgAnswerTime(),
                showTime,
                statistics,
                this::showTaskCompletionDialog
        ).show(ft, "statistics_report");
    }

    private void showTaskCompletionDialog() {
        // Show interstitial ad before the completion dialog
        showInterstitialAdAndContinue(this::showCompletionDialogAfterAd);
    }

    private void showInterstitialAdAndContinue(Runnable continueAction) {
        int randomValue = random.nextInt(101);
        if (randomValue <= 40 && interstitialAd != null) {
            isShowingInterstitial = true;
            interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    interstitialAd = null;
                    loadInterstitialAd();
                    isShowingInterstitial = false;
                    if (continueAction != null) {
                        continueAction.run();
                    }
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError error) {
                    isShowingInterstitial = false;
                    interstitialAd = null;
                    if (continueAction != null) {
                        continueAction.run();
                    }
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    interstitialAd = null;
                }
            });
            interstitialAd.show(this);
        } else {
            if (continueAction != null) {
                continueAction.run();
            }
        }
    }

    private void showCompletionDialogAfterAd() {
        String message;
        if (getViewModel().getSubTask().getNotesInSequence() == 1) {
            message = getResources().getString(R.string.task_accomplished_with_time, getCorrectAnswerPercent(), getAvgAnswerTime());
        } else {
            message = getResources().getString(R.string.task_accomplished, getCorrectAnswerPercent());
        }
        showStyledDialog(
                getString(R.string.task_completed_header),
                message,
                new DialogActionSpec(R.string.next_task, true, this::onNextTask),
                new DialogActionSpec(R.string.repeat, false, this::runTask),
                new DialogActionSpec(R.string.stop, false, this::onTaskStop));
    }

    private void onNextTask() {
        SubTask subTask = getViewModel().getSubTask();
        if (subTask == null || subTask.getNextSubTaskId() == null) {
            return;
        }
        if (!getViewModel().syncWithSubTaskId(subTask.getNextSubTaskId())) {
            return;
        }
        setActivityTitle();
        showInstructionAndRunTask();
    }

    private void onTaskStop() {
        midiSupport.stop();
        currentActiveNoteInfo = null;
        binding.pianoKeyboard.setCurrentNoteInfo(null);
        binding.tvExpectedNote.setText("");
        invalidatePianoKeyboard();
        taskStatePublishSubject.onNext(false);
    }

    private void showInstructionAndRunTask() {
        if (getViewModel().getInstructionId() == null) {
            runTask();
            return;
        }
        String[] taskInstr = getResources().getStringArray(R.array.task_instruction);
        showStyledDialog(
                getString(R.string.task_instruction_header),
                String.format(taskInstr[getViewModel().getInstructionId()], getViewModel().getNotesPerMinute()),
                new DialogActionSpec(R.string.ok, true, this::runTask),
                null,
                null);
    }

    private void showStyledDialog(String title,
                                  String message,
                                  DialogActionSpec primaryAction,
                                  DialogActionSpec secondaryAction,
                                  DialogActionSpec tertiaryAction) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.execute_action_dialog, null, false);
        dialog.setContentView(dialogView);
        dialog.setCancelable(false);

        TextView titleView = dialogView.findViewById(R.id.dialogTitle);
        TextView messageView = dialogView.findViewById(R.id.dialogMessage);
        titleView.setText(title);
        messageView.setText(message);

        bindDialogButton(dialogView.findViewById(R.id.primaryButton), primaryAction, dialog);
        bindDialogButton(dialogView.findViewById(R.id.secondaryButton), secondaryAction, dialog);
        bindDialogButton(dialogView.findViewById(R.id.tertiaryButton), tertiaryAction, dialog);

        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    private void bindDialogButton(Button button, DialogActionSpec actionSpec, Dialog dialog) {
        if (actionSpec == null) {
            button.setVisibility(View.GONE);
            return;
        }
        button.setText(actionSpec.labelResId);
        if (actionSpec.primary) {
            button.setBackgroundResource(R.drawable.primary_button_background);
            button.setTextColor(ContextCompat.getColor(this, R.color.textInverse));
        } else {
            button.setBackgroundResource(R.drawable.rect_background);
            button.setTextColor(ContextCompat.getColor(this, R.color.textPrimary));
        }
        button.setOnClickListener(v -> {
            dialog.dismiss();
            if (actionSpec.action != null) {
                actionSpec.action.run();
            }
        });
    }

    private void storeTaskResults() {
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
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}
