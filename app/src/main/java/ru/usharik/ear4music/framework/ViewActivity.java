package ru.usharik.ear4music.framework;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.DispatchingAndroidInjector;

/**
 * Created by macbook on 17.02.2018.
 */

public abstract class ViewActivity<T extends ViewModel> extends AppCompatActivity {
    private T viewModel;

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    @Inject
    ViewModelProvider.Factory appViewModelFactory;

    protected abstract Class<T> getViewModelClass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
    }

    protected void applySystemBarInsets(View view,
                                         boolean applyLeft,
                                         boolean applyTop,
                                         boolean applyRight,
                                         boolean applyBottom) {
        final int initialLeft = view.getPaddingLeft();
        final int initialTop = view.getPaddingTop();
        final int initialRight = view.getPaddingRight();
        final int initialBottom = view.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(view, (targetView, windowInsets) -> {
            Insets systemBars = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            targetView.setPadding(
                    applyLeft ? initialLeft + systemBars.left : initialLeft,
                    applyTop ? initialTop + systemBars.top : initialTop,
                    applyRight ? initialRight + systemBars.right : initialRight,
                    applyBottom ? initialBottom + systemBars.bottom : initialBottom);
            return windowInsets;
        });
        ViewCompat.requestApplyInsets(view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AndroidInjection.inject(this);
        viewModel = new ViewModelProvider(this.getViewModelStore(), appViewModelFactory)
                .get(getViewModelClass());
    }

    public T getViewModel() {
        return viewModel;
    }
}
