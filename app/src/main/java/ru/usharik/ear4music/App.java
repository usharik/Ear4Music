package ru.usharik.ear4music;

import android.app.Application;

import com.google.android.gms.ads.MobileAds;

import ru.usharik.ear4music.di.DaggerAppComponent;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

/**
 * Created by macbook on 08.02.18.
 */

public class App extends Application implements HasAndroidInjector {

    @Inject
    DispatchingAndroidInjector<Object> dispatchingAndroidInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        DaggerAppComponent.builder().create(this).inject(this);
        new Thread(() -> MobileAds.initialize(this, initializationStatus -> {})).start();
    }

    @Override
    public AndroidInjector<Object> androidInjector() {
        return dispatchingAndroidInjector;
    }
}
