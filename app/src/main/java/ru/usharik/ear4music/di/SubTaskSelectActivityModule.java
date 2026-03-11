package ru.usharik.ear4music.di;

import ru.usharik.ear4music.SubTaskSelectActivity;

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
