package com.example.macbook.ear4music.di;

import com.example.macbook.ear4music.App;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * Created by macbook on 09.02.18.
 */

@Singleton
@Component(modules = {AndroidSupportInjectionModule.class,
        AppModule.class,
        TaskSelectActivityModule.class,
        SubTaskSelectActivityModule.class,
        ExecuteTaskActivityModule.class,
        ServiceModule.class})
public interface AppComponent extends AndroidInjector<App> {

    @Component.Builder
    abstract class Builder extends AndroidInjector.Builder<App> {
    }
}