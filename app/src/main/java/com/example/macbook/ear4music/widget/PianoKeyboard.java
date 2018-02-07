package com.example.macbook.ear4music.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.macbook.ear4music.NoteInfo;
import com.example.macbook.ear4music.NotesEnum;
import com.example.macbook.ear4music.R;
import com.example.macbook.ear4music.listner.PianoKeyboardListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by macbook on 03.07.17.
 */
public class PianoKeyboard extends View {
    private Paint paint;
    private Paint textPaint;
    private List<Rect> whiteKeys;
    private List<Rect> blackKeys;
    private HashMap<NotesEnum, Rect> notes2rect;
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

        int blackKeySize = whiteKeySize/2;
        for (int blackNum : new int[] {0, 1, 3, 4, 5, 7}) {
            Rect key = whiteKeys.get(blackNum);
            blackKeys.add(new Rect(
                    key.right - blackKeySize/2,
                    0,
                    key.right + (blackNum != 7 ? blackKeySize/2 : 0),
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
        noteNameWidth = textRect.height();
        super.onSizeChanged(width, height, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int i=0;
        List<NotesEnum> whiteNotes = NotesEnum.getWhite();
        for (Rect r : this.whiteKeys) {
            NotesEnum note = whiteNotes.get(i++);
            paint.setColor(getKeyColor(r, note, Color.WHITE));
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(r, paint);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(r, paint);
            textPaint.setColor(Color.BLACK);
            if (showNoteNames) {
                canvas.drawText(note.name(), r.centerX() - noteNameWidth / 2, r.centerY() * 5.0f / 3.0f, textPaint);
            }
        }
        for (Rect r : blackKeys) {
            paint.setColor(getKeyColor(r, null, Color.BLACK));
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(r, paint);
        }
    }

    private int getKeyColor(Rect r, NotesEnum note, int defaultColor) {
        if (currentNoteInfo != null && note != null && currentNoteInfo.isHighlighted && currentNoteInfo.note == note) {
            return Color.GREEN;
        }
        if (!r.equals(pressedNoteRect)) {
            return defaultColor;
        }
        if (currentNoteInfo == null) {
            return Color.GRAY;
        }
        return currentNoteInfo.note == pressedNote ? Color.GREEN : Color.RED;
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//
//        int desiredWidth = 200;
//        int desiredHeight = 200;
//
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
//
//        int width;
//        int height;
//
//        //Measure Width
//        if (widthMode == MeasureSpec.EXACTLY) {
//            //Must be this size
//            width = widthSize;
//        } else if (widthMode == MeasureSpec.AT_MOST) {
//            //Can't be bigger than...
//            width = Math.min(desiredWidth, widthSize);
//        } else {
//            //Be whatever you want
//            width = desiredWidth;
//        }
//
//        //Measure Height
//        if (heightMode == MeasureSpec.EXACTLY) {
//            //Must be this size
//            height = heightSize;
//        } else if (heightMode == MeasureSpec.AT_MOST) {
//            //Can't be bigger than...
//            height = Math.min(desiredHeight, heightSize);
//        } else {
//            //Be whatever you want
//            height = desiredHeight;
//        }
//
//        //MUST CALL THIS
//        setMeasuredDimension(width, height);
//    }

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
                        if (currentNoteInfo.pressedNote != currentNoteInfo.note) {
                            currentNoteInfo.pressedNote=note;
                        }
                        pressedNoteRect = rect;
                        currentNoteInfo.pressedNote=pressedNote;
                        break;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (currentNoteInfo != null) {
                    pianoKeyboardListener.onNotePressed(currentNoteInfo);
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

    public boolean isShowNoteNames() {
        return showNoteNames;
    }

    public void setShowNoteNames(boolean showNoteNames) {
        this.showNoteNames = showNoteNames;
        invalidate();
    }
}
