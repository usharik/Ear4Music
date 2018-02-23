package com.example.macbook.ear4music.di;

import android.app.Application;

import com.example.macbook.ear4music.App;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.android.AndroidInjectionModule;

/**
 * Created by macbook on 09.02.18.
 */

@Module(includes = {AndroidInjectionModule.class, ViewModelModule.class})
abstract class AppModule {

    @Binds
    @Singleton
    abstract Application application(App app);
}