package com.example.macbook.ear4music;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;


/**
 * Created by macbook on 23.02.2018.
 */

public class CountDownDialog extends DialogFragment {

    private Runnable action;

    public static CountDownDialog newInstance(Runnable action) {
        CountDownDialog fragment = new CountDownDialog();
        fragment.action = action;
        fragment.setCancelable(false);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.count_down_dialog, container, false);
        final TextView tvCountDown = view.findViewById(R.id.tvCountDown);

        Observable.intervalRange(1, 4, 0, 1, TimeUnit.SECONDS)
                .doOnNext((cnt) -> getActivity().runOnUiThread(() -> tvCountDown.setText(cnt < 4 ? Long.toString(4-cnt) : "")))
                .doOnComplete(() -> getActivity().runOnUiThread(this::dismiss))
                .subscribe();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (action != null) {
            getActivity().runOnUiThread(action);
        }
    }
}
