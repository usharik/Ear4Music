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

import ru.usharik.ear4music.NoteInfo;
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
    private NotesEnum pressedNote;
    private NoteInfo currentNoteInfo;
    private Rect pressedNoteRect;
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
            paint.setColor(getKeyColor(r, note, getColor(R.color.keyboardWhite)));
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
            paint.setColor(getKeyColor(r, note, getColor(R.color.keyboardBlack)));
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(r, paint);
            paint.setColor(getColor(R.color.keyboardStroke));
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(r, paint);
        }
    }

    private int getKeyColor(Rect r, NotesEnum note, int defaultColor) {
        if (currentNoteInfo != null && note != null && currentNoteInfo.isHighlighted && currentNoteInfo.note == note) {
            return getColor(R.color.keyboardHighlight);
        }
        if (!r.equals(pressedNoteRect)) {
            return defaultColor;
        }
        if (currentNoteInfo == null) {
            return getColor(R.color.lightGray);
        }
        return currentNoteInfo.note == pressedNote ? getColor(R.color.green) : getColor(R.color.red);
    }

    private int getColor(int colorResId) {
        return ContextCompat.getColor(getContext(), colorResId);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (currentNoteInfo == null) {
                    return true;
                }
                int x=(int) event.getX();
                int y=(int) event.getY();
                pressedNote = null;
                pressedNoteRect = null;
                for (NotesEnum note : notes2rect.keySet()) {
                    Rect rect = notes2rect.get(note);
                    if (rect.contains(x, y)) {
                        pressedNote = note;
                        pressedNoteRect = rect;
                        performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                        break;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (currentNoteInfo != null) {
                    pianoKeyboardListener.onKeyPressed(new KeyPress(pressedNote, System.currentTimeMillis()));
                }
                pressedNote = null;
                pressedNoteRect = null;
                break;
        }
        this.invalidate();
        return true;
    }

    public void setCurrentNoteInfo(NoteInfo currentNoteInfo) {
        this.currentNoteInfo=currentNoteInfo;
    }

    public void setPianoKeyboardListener(PianoKeyboardListener pianoKeyboardListener) {
        this.pianoKeyboardListener = pianoKeyboardListener;
    }

    public void setShowNoteNames(boolean showNoteNames) {
        this.showNoteNames = showNoteNames;
        invalidate();
    }
}
