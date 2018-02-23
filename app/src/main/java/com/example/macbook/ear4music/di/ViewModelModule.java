package com.example.macbook.ear4music.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.example.macbook.ear4music.ExecuteTaskViewModel;
import com.example.macbook.ear4music.SubTaskSelectViewModel;
import com.example.macbook.ear4music.TaskSelectViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

/**
 * Created by macbook on 09.02.18.
 */

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(TaskSelectViewModel.class)
    abstract ViewModel bindTaskSelectViewModel(TaskSelectViewModel userViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SubTaskSelectViewModel.class)
    abstract ViewModel bindSubTaskSelectViewModel(SubTaskSelectViewModel userViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ExecuteTaskViewModel.class)
    abstract ViewModel bindExecuteTaskViewModel(ExecuteTaskViewModel userViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(AppViewModelFactory factory);
}
