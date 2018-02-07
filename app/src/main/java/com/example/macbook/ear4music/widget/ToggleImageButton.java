package com.example.macbook.ear4music.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.widget.Checkable;

import com.example.macbook.ear4music.R;

/**
 * Created by macbook on 22.02.2018.
 */

public class ToggleImageButton extends AppCompatImageButton implements Checkable {
    private OnCheckedChangeListener onCheckedChangeListener;

    public ToggleImageButton(Context context) {
        super(context);
    }

    public ToggleImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCheckedAttr(attrs);
    }

    public ToggleImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCheckedAttr(attrs);
    }

    private void setCheckedAttr(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ToggleImageButton);
        setChecked(a.getBoolean(R.styleable.ToggleImageButton_checked, false));
        a.recycle();
    }

    @Override
    public boolean isChecked() {
        return isSelected();
    }

    @BindingAdapter("checked")
    public static void setCheckedValue(ToggleImageButton view, boolean checked) {
        view.setChecked(checked);
    }

    @BindingAdapter(value = "checkedAttrChanged")
    public static void setBindingListener(ToggleImageButton button, final InverseBindingListener listener) {
        if (listener != null) {
            button.setOnCheckedChangeListener((b, s) -> listener.onChange());
        }
    }

    @InverseBindingAdapter(attribute = "checked")
    public static boolean getCheckedValue(ToggleImageButton button) {
        return button.isChecked();
    }

    @Override
    public void setChecked(boolean checked) {
        setSelected(checked);

        if (onCheckedChangeListener != null) {
            onCheckedChangeListener.onCheckedChanged(this, checked);
        }
    }

    @Override
    public void toggle() {
        setChecked(!isChecked());
    }

    @Override
    public boolean performClick() {
        toggle();
        return super.performClick();
    }

    public OnCheckedChangeListener getOnCheckedChangeListener() {
        return onCheckedChangeListener;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(ToggleImageButton buttonView, boolean isChecked);
    }
}
