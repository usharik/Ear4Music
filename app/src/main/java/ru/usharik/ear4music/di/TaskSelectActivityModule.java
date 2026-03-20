package ru.usharik.ear4music.di;

import ru.usharik.ear4music.activity.TaskSelectActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by macbook on 10.02.18.
 */

@Module
public abstract class TaskSelectActivityModule {
    @ContributesAndroidInjector
    abstract TaskSelectActivity contributeTaskSelectActivity();
}
