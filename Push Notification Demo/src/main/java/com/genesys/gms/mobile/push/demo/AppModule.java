package com.genesys.gms.mobile.push.demo;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import com.genesys.gms.mobile.push.demo.data.DataModule;
import com.genesys.gms.mobile.push.demo.data.push.GcmIntentService;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

/**
 * Created by stau on 11/27/2014.
 */
@Module(
    injects = {
        App.class,
        GcmIntentService.class
    },
    includes = {
        DataModule.class
    },
    library = true
)
public class AppModule {
    private final App application;
    public AppModule(App application) {
        this.application = application;
    }

    @Provides @Singleton
    GoogleCloudMessaging provideGoogleCloudMessaging(@ForApplication Context context) {
        return GoogleCloudMessaging.getInstance(context);
    }

    @Provides @Singleton
    NotificationManager provideNotificationManager(@ForApplication Context context) {
        return (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Provides @Singleton @ForApplication
    Context provideApplicationContext() {
        return application;
    }

    @Provides @Singleton
    Application provideApplication() {
        return application;
    }
}
