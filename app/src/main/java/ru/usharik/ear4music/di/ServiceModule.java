package ru.usharik.ear4music.di;

import android.app.Application;

import ru.usharik.ear4music.model.room.AppDatabase;
import ru.usharik.ear4music.repository.SubTaskRepository;
import ru.usharik.ear4music.repository.TaskRepository;
import ru.usharik.ear4music.service.AppState;
import ru.usharik.ear4music.service.MidiSupport;
import ru.usharik.ear4music.service.StatisticsStorage;

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
    AppDatabase provideAppDatabase(Application application) {
        return AppDatabase.create(application);
    }

    @Provides
    @Singleton
    TaskRepository provideTaskRepository(AppDatabase appDatabase) {
        return new TaskRepository(appDatabase);
    }

    @Provides
    @Singleton
    SubTaskRepository provideSubTaskRepository(AppDatabase appDatabase) {
        return new SubTaskRepository(appDatabase);
    }

    @Provides
    @Singleton
    MidiSupport provideMidiSupport() {
        return new MidiSupport();
    }

    @Provides
    @Singleton
    StatisticsStorage provideStatisticsStorage() {
        return new StatisticsStorage();
    }

    @Provides
    @Singleton
    AppState provideAppState() {
        return new AppState();
    }
}
