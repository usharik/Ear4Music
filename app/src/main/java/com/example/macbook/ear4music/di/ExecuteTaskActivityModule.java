package com.example.macbook.ear4music.di;

import com.example.macbook.ear4music.ExecuteTaskActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by macbook on 10.02.18.
 */

@Module
public abstract class ExecuteTaskActivityModule {
    @ContributesAndroidInjector
    abstract ExecuteTaskActivity contributeExecuteTaskActivityModule();
}
