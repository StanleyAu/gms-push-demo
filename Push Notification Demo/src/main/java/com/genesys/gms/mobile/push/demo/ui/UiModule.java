package com.genesys.gms.mobile.push.demo.ui;

import android.content.Context;
import com.genesys.gms.mobile.push.demo.AppModule;
import com.genesys.gms.mobile.push.demo.BaseActivity;
import com.genesys.gms.mobile.push.demo.ForActivity;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

/**
 * Created by stau on 11/27/2014.
 */
@Module(
    injects = {
        MainActivity.class,
        MainFragment.class,
        SettingsFragment.class
    },
    addsTo = AppModule.class,
    library = true
)
public class UiModule {
    private final BaseActivity activity;

    public UiModule(BaseActivity activity) {
        this.activity = activity;
    }

    @Provides @Singleton @ForActivity
    Context provideActivityContext() {
        return activity;
    }
}
