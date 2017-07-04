package com.example.macbook.androidapp.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.example.macbook.androidapp.NotesEnum;
import com.example.macbook.androidapp.StatisticsStorage;
import com.example.macbook.androidapp.listner.PianoKeyboardListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by macbook on 03.07.17.
 */
public class PianoKeyboard extends View {
    private Paint paint;
    private List<Rect> whiteKeys;
    private List<Rect> blackKeys;
    private HashMap<NotesEnum, Rect> notes2rect;
    private NotesEnum pressedNote;
    private NotesEnum currentNote;
    private int currentNoteNumber;
    private Rect pressedNoteRect;
    private StatisticsStorage statisticsStorage;
    private PianoKeyboardListener pianoKeyboardListener;
    private int w;
    private int h;

    public PianoKeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
        whiteKeys = new ArrayList<>();
        blackKeys = new ArrayList<>();
        notes2rect = new LinkedHashMap<>();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.w = w;
        this.h = h;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int whiteKeySize = w/8 - 1;
        int blackKeySize = whiteKeySize/2;
        if (whiteKeys.isEmpty()) {
            for(int i=0; i<8; i++) {
                Rect rect = new Rect(i * whiteKeySize, 0, (i + 1) * whiteKeySize, h-10);
                whiteKeys.add(rect);
            }
        }
        if (blackKeys.isEmpty()) {
            Rect key = whiteKeys.get(0);
            blackKeys.add(new Rect(key.right - blackKeySize/2, 0, key.right + blackKeySize/2, h/2));
            key = whiteKeys.get(1);
            blackKeys.add(new Rect(key.right - blackKeySize/2, 0, key.right + blackKeySize/2, h/2));
            key = whiteKeys.get(3);
            blackKeys.add(new Rect(key.right - blackKeySize/2, 0, key.right + blackKeySize/2, h/2));
            key = whiteKeys.get(4);
            blackKeys.add(new Rect(key.right - blackKeySize/2, 0, key.right + blackKeySize/2, h/2));
            key = whiteKeys.get(5);
            blackKeys.add(new Rect(key.right - blackKeySize/2, 0, key.right + blackKeySize/2, h/2));
            key = whiteKeys.get(7);
            blackKeys.add(new Rect(key.right - blackKeySize/2, 0, key.right + blackKeySize/2, h/2));
        }
        if (notes2rect.isEmpty()) {
            int i=0;
            for(NotesEnum note : NotesEnum.getBlack()) {
                notes2rect.put(note, blackKeys.get(i++));
            }
            i=0;
            for(NotesEnum note : NotesEnum.getWhite()) {
                notes2rect.put(note, whiteKeys.get(i++));
            }
        }
        for (Rect r : whiteKeys) {
            paint.setColor(getKeyColor(r, Color.WHITE));
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(r, paint);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(r, paint);
        }
        for (Rect r : blackKeys) {
            paint.setColor(getKeyColor(r, Color.BLACK));
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(r, paint);
        }
    }

    private int getKeyColor(Rect r, int defaultColor) {
        if (!r.equals(pressedNoteRect)) {
            return defaultColor;
        }
        if (currentNote == null) {
            return Color.GRAY;
        }
        return currentNote == pressedNote ? Color.GREEN : Color.RED;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int desiredWidth = 200;
        int desiredHeight = 200;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int x=(int) event.getX();
                int y=(int) event.getY();
                pressedNote = null;
                pressedNoteRect = null;
                for (NotesEnum note : notes2rect.keySet()) {
                    Rect rect = notes2rect.get(note);
                    if (rect.contains(x, y)) {
                        pressedNote = note;
                        pressedNoteRect = rect;
                        if (statisticsStorage != null) {
                            statisticsStorage.submitAnswer(currentNoteNumber, currentNote, pressedNote);
                        }
                        if (pressedNote.equals(currentNote)) {
                            pianoKeyboardListener.onCorrectNotePressed();
                        }
                        break;
                    }
                }
                Log.d("MULTIPLE", "X=" + x + " Y=" + y);
                break;
            case MotionEvent.ACTION_UP:
                pressedNote = null;
                pressedNoteRect = null;
                break;
        }
        this.invalidate();
        return true;
    }

    public void setCurrentNote(NotesEnum currentNote) {
        this.currentNote = currentNote;
    }

    public void setCurrentNoteNumber(int currentNoteNumber) {
        this.currentNoteNumber = currentNoteNumber;
    }

    public void setStatisticsStorage(StatisticsStorage statisticsStorage) {
        this.statisticsStorage = statisticsStorage;
    }

    public void setPianoKeyboardListener(PianoKeyboardListener pianoKeyboardListener) {
        this.pianoKeyboardListener = pianoKeyboardListener;
    }
}
