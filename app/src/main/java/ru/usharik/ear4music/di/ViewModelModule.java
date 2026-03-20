package ru.usharik.ear4music.di;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import ru.usharik.ear4music.activity.ExecuteTaskViewModel;
import ru.usharik.ear4music.activity.SubTaskSelectViewModel;
import ru.usharik.ear4music.activity.TaskSelectViewModel;

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
