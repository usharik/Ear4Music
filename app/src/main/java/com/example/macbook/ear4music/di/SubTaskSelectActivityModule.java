package com.example.macbook.ear4music.di;

import com.example.macbook.ear4music.SubTaskSelectActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by macbook on 18.02.2018.
 */

@Module
public abstract class SubTaskSelectActivityModule {
    @ContributesAndroidInjector
    abstract SubTaskSelectActivity contributeSubTaskSelectActivity();
}
