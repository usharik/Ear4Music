package ru.usharik.ear4music.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

import ru.usharik.ear4music.NotesEnum;
import ru.usharik.ear4music.R;
import ru.usharik.ear4music.listner.PianoKeyboardListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by macbook on 03.07.17.
 */
public class PianoKeyboard extends View {
    private final Paint paint;
    private final Paint textPaint;
    private final List<Rect> whiteKeys;
    private final List<Rect> blackKeys;
    private final HashMap<NotesEnum, Rect> notes2rect;
    /** Tracks which key is currently being physically held down; used to emit {@link KeyPress} on ACTION_UP. */
    private NotesEnum pressedNote;
    /** Expected note for prompt highlighting; {@code null} when no task is active.
     *  A {@code null} value also disables keyboard input (touch events are ignored). */
    private NotesEnum expectedNote;
    /** Whether the expected-note highlight colour should be drawn. */
    private boolean expectedHighlightEnabled;
    /** The note for which answer feedback (green/red) should be shown; {@code null} = no feedback. */
    private NotesEnum feedbackNote;
    /** {@code true} = correct (green), {@code false} = wrong (red), {@code null} = no feedback. */
    private Boolean feedbackIsCorrect;
    private PianoKeyboardListener pianoKeyboardListener;
    private int noteNameWidth;
    private boolean showNoteNames;

    public PianoKeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
        whiteKeys = new ArrayList<>();
        blackKeys = new ArrayList<>();
        notes2rect = new LinkedHashMap<>();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.PianoKeyboard, 0, 0);
        showNoteNames = arr.getBoolean(R.styleable.PianoKeyboard_showNoteNames, true);
        arr.recycle();
        textPaint.setFakeBoldText(true);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        int width = w-1;
        int height = h-1;
        int whiteKeySize = width/8;
        int restSpace = width - whiteKeySize*8;
        whiteKeys.clear();
        for(int i=0; i<8; i++) {
            Rect rect = new Rect(i * whiteKeySize, 0, (i + 1) * whiteKeySize, height);
            whiteKeys.add(rect);
        }
        whiteKeys.get(7).right += restSpace;

        int blackKeySize = Math.round(whiteKeySize*0.6f);
        blackKeys.clear();
        for (int blackNum : new int[] {0, 1, 3, 4, 5}) {
            Rect key = whiteKeys.get(blackNum);
            blackKeys.add(new Rect(
                    key.right - blackKeySize/2,
                    0,
                    key.right + blackKeySize/2,
                    height/2));
        }

        int i=0;
        notes2rect.clear();
        for(NotesEnum note : NotesEnum.getBlack()) {
            notes2rect.put(note, blackKeys.get(i++));
        }
        i=0;
        for(NotesEnum note : NotesEnum.getWhite()) {
            notes2rect.put(note, whiteKeys.get(i++));
        }
        textPaint.setTextSize(height*0.1f);
        Rect textRect = new Rect();
        textPaint.getTextBounds("C", 0, 1, textRect);
        noteNameWidth = textRect.width();
        super.onSizeChanged(width, height, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int i=0;
        List<NotesEnum> whiteNotes = NotesEnum.getWhite();
        for (Rect r : this.whiteKeys) {
            NotesEnum note = whiteNotes.get(i++);
            paint.setColor(getKeyColor(note, getColor(R.color.keyboardWhite)));
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(r, paint);
            paint.setColor(getColor(R.color.keyboardStroke));
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(r, paint);
            textPaint.setColor(getColor(R.color.keyboardLabel));
            if (showNoteNames) {
                canvas.drawText(note.name(), r.centerX() - noteNameWidth / 2f, r.centerY() * 5.0f / 3.0f, textPaint);
            }
        }
        i = 0;
        List<NotesEnum> blackNotes = NotesEnum.getBlack();
        for (Rect r : blackKeys) {
            NotesEnum note = blackNotes.get(i++);
            paint.setColor(getKeyColor(note, getColor(R.color.keyboardBlack)));
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(r, paint);
            paint.setColor(getColor(R.color.keyboardStroke));
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(r, paint);
        }
    }

    private int getKeyColor(NotesEnum note, int defaultColor) {
        // Priority 1: answer feedback colour (green = correct, red = wrong).
        if (feedbackIsCorrect != null && note != null && note == feedbackNote) {
            return feedbackIsCorrect ? getColor(R.color.green) : getColor(R.color.red);
        }
        // Priority 2: expected-note highlight.
        if (expectedHighlightEnabled && note != null && note == expectedNote) {
            return getColor(R.color.keyboardHighlight);
        }
        // Priority 3: default key colour.
        return defaultColor;
    }

    private int getColor(int colorResId) {
        return ContextCompat.getColor(getContext(), colorResId);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (expectedNote == null) {
                    return true;
                }
                int x=(int) event.getX();
                int y=(int) event.getY();
                pressedNote = null;
                for (NotesEnum note : notes2rect.keySet()) {
                    Rect rect = notes2rect.get(note);
                    if (rect.contains(x, y)) {
                        pressedNote = note;
                        performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                        break;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (expectedNote != null) {
                    pianoKeyboardListener.onKeyPressed(new KeyPress(pressedNote, System.currentTimeMillis()));
                }
                pressedNote = null;
                break;
        }
        this.invalidate();
        return true;
    }

    // ---------------------------------------------------------------------------
    // Public API — expected note (prompt highlighting)
    // ---------------------------------------------------------------------------

    /**
     * Sets the note that should be highlighted as the expected answer.
     *
     * @param note            the expected note, or {@code null} to disable the keyboard.
     * @param highlightEnabled whether to draw the expected-note highlight colour.
     */
    public void setExpectedNote(NotesEnum note, boolean highlightEnabled) {
        this.expectedNote = note;
        this.expectedHighlightEnabled = highlightEnabled;
        invalidate();
    }

    /** Removes the expected-note state and disables the keyboard for new presses.
     *  Touch events are silently ignored while {@code expectedNote} is {@code null}. */
    public void clearExpectedNote() {
        this.expectedNote = null;
        this.expectedHighlightEnabled = false;
        invalidate();
    }

    // ---------------------------------------------------------------------------
    // Public API — answer feedback (green / red)
    // ---------------------------------------------------------------------------

    /**
     * Shows green (correct) or red (wrong) feedback on the given note.
     * Call this to display persistent feedback independent of physical touch state.
     *
     * @param note      the key to colour.
     * @param isCorrect {@code true} for green, {@code false} for red.
     */
    public void showAnswerFeedback(NotesEnum note, boolean isCorrect) {
        this.feedbackNote = note;
        this.feedbackIsCorrect = isCorrect;
        invalidate();
    }

    /** Clears any green/red answer-feedback state. */
    public void clearAnswerFeedback() {
        this.feedbackNote = null;
        this.feedbackIsCorrect = null;
        invalidate();
    }

    // ---------------------------------------------------------------------------
    // Deprecated compatibility bridge
    // ---------------------------------------------------------------------------

    // setCurrentNoteInfo(NoteInfo) has been removed — use setExpectedNote() /
    // clearExpectedNote() for prompt state and showAnswerFeedback() /
    // clearAnswerFeedback() for green/red feedback.

    public void setPianoKeyboardListener(PianoKeyboardListener pianoKeyboardListener) {
        this.pianoKeyboardListener = pianoKeyboardListener;
    }

    public void setShowNoteNames(boolean showNoteNames) {
        this.showNoteNames = showNoteNames;
        invalidate();
    }
}
