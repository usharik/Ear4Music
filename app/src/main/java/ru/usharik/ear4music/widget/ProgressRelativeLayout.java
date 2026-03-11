package ru.usharik.ear4music.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;

import ru.usharik.ear4music.R;

/**
 * Created by macbook on 11.02.18.
 */

public class ProgressRelativeLayout extends RelativeLayout {
    private static final float MAX_PERCENT = 100f;

    private int percentValue;
    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

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
            float inset = dp(10);
            float top = dp(8);
            float height = dp(8);
            float radius = height / 2f;
            float availableWidth = Math.max(0f, canvas.getWidth() - inset * 2f);
            float clampedPercent = Math.max(0f, Math.min(MAX_PERCENT, percentValue));
            float progressRight = inset + (availableWidth * clampedPercent / MAX_PERCENT);

            fillPaint.setStyle(Paint.Style.FILL);
            fillPaint.setColor(ContextCompat.getColor(getContext(), R.color.progressTrack));
            canvas.drawRoundRect(inset, top, canvas.getWidth() - inset, top + height, radius, radius, fillPaint);

            fillPaint.setColor(ContextCompat.getColor(getContext(), R.color.progressFillSoft));
            canvas.drawRoundRect(inset, top, progressRight, top + height, radius, radius, fillPaint);

            strokePaint.setStyle(Paint.Style.STROKE);
            strokePaint.setStrokeWidth(dp(1));
            strokePaint.setColor(ContextCompat.getColor(getContext(), R.color.progressFill));
            canvas.drawRoundRect(inset, top, progressRight, top + height, radius, radius, strokePaint);
        }
        super.dispatchDraw(canvas);
    }

    private float dp(int value) {
        return value * getResources().getDisplayMetrics().density;
    }

    public void setPercentValue(int percentValue) {
        this.percentValue = percentValue;
        invalidate();
    }
}
