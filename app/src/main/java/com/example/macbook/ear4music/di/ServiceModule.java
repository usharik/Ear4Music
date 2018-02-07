package com.example.macbook.ear4music.di;

import android.app.Application;

import com.example.macbook.ear4music.service.AppState;
import com.example.macbook.ear4music.service.DbService;
import com.example.macbook.ear4music.service.MidiSupport;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by macbook on 09.02.18.
 */

@Module(includes = {AppModule.class})
class ServiceModule {
    @Provides
    @Singleton
    DbService provideDbService(Application application) {
        return new DbService(application);
    }

    @Provides
    @Singleton
    MidiSupport provideMidiSupport() {
        return new MidiSupport();
    }

    @Provides
    @Singleton
    AppState provideAppState() {
        return new AppState();
    }
}
