package ru.usharik.ear4music;

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
import ru.usharik.ear4music.databinding.ExecuteTaskActivityBinding;
import ru.usharik.ear4music.framework.BannerAdLoader;
import ru.usharik.ear4music.framework.ViewActivity;
import ru.usharik.ear4music.model.SubTask;
import ru.usharik.ear4music.service.MidiSupport;
import ru.usharik.ear4music.service.StatisticsStorage;
import ru.usharik.ear4music.service.Utils;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;

import java.util.*;
import java.util.concurrent.TimeUnit;

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
    private Subject<NoteInfo> keyboardPublishSubject;
    private Subject<Boolean> taskStatePublishSubject;
    private CompositeDisposable compositeDisposable;
    private AdView bannerAdView;
    private InterstitialAd interstitialAd;
    private final Random random = new Random();

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
        applySystemBarInsets(binding.contentContainer, true, false, true, true);

        binding.pianoKeyboard.setPianoKeyboardListener(noteInfo -> keyboardPublishSubject.onNext(noteInfo));

        getViewModel().setStarted(false);
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
        if (adUnitId == null || adUnitId.trim().isEmpty()) {
            return;
        }
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
        interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                interstitialAd = null;
                loadInterstitialAd();
                startTask();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError error) {
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

        final StatisticsStorage statStore = getViewModel().getStatisticsStorage();
        statStore.reset();
        binding.progressBar.setProgress(0);
        if (notesInSequence == 1) {
            Disposable noteEmitterDisposable = notesEmitterObservable
                    .subscribeOn(Schedulers.io())
                    .doOnNext((notes) -> {
                        binding.pianoKeyboard.setCurrentNoteInfo(notes[0]);
                        runOnUiThread(this::invalidatePianoKeyboard);
                        if (notes[0].isPlayWithScale) {
                            midiSupport.playNoteWithScale(notes[0].note, notes[0].longitude);
                        } else {
                            midiSupport.playNote(notes[0].note, notes[0].longitude);
                        }
                        statStore.submitAnswer(notes[0]);
                        runOnUiThread(() -> updateProgressViews(notes[0]));
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
                        binding.pianoKeyboard.setCurrentNoteInfo(null);
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
                        runOnUiThread(() -> updateProgressViews(pressed));
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete(this::onTaskComplete)
                    .doOnDispose(this::onTaskStop)
                    .subscribe();

            compositeDisposable.add(noteEmitterDisposable);
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
        binding.pianoKeyboard.setCurrentNoteInfo(null);
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
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}
