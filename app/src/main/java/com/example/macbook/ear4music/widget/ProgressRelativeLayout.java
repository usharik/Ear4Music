package com.example.macbook.ear4music.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.example.macbook.ear4music.R;

/**
 * Created by macbook on 11.02.18.
 */

public class ProgressRelativeLayout extends RelativeLayout {

    private int percentValue;

    public ProgressRelativeLayout(Context context) {
        super(context);
    }

    public ProgressRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.ProgressRelativeLayout, 0, 0);
        percentValue = arr.getInteger(R.styleable.ProgressRelativeLayout_percentValue, 0);
        arr.recycle();
    }

    public ProgressRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ProgressRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (percentValue > 0) {
            Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(Color.GREEN);
            mPaint.setAlpha(128);
            canvas.drawRect(3, 3, (float) (canvas.getWidth() * percentValue / 100.0) - 3, 15, mPaint);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(getResources().getColor(R.color.darkGreen));
            canvas.drawRect(3, 3, (float) (canvas.getWidth() * percentValue / 100.0) - 3, 15, mPaint);
            canvas.drawRect(3, 3, (float) (canvas.getWidth() - 3), 15, mPaint);
        }
        super.dispatchDraw(canvas);
    }

    public void setPercentValue(int percentValue) {
        this.percentValue = percentValue;
        invalidate();
    }
}
