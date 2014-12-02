package com.genesys.gms.mobile.push.demo.data;

import android.app.Application;
import android.content.SharedPreferences;
import com.genesys.gms.mobile.push.demo.data.api.ApiModule;
import com.genesys.gms.mobile.push.demo.data.otto.AndroidBus;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by stau on 11/27/2014.
 */
@Module(
    includes = ApiModule.class,
    complete = false,
    library = true
)
public class DataModule {
    static final int DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB
    @Provides @Singleton
    Bus provideBus() {
        // Allows Otto bus registration and posting to occur on any thread
        return new AndroidBus(ThreadEnforcer.ANY);
    }

    @Provides @Singleton
    OkHttpClient provideOkHttpClient(Application app) {
        return createOkHttpClient(app);
    }

    @Provides @Singleton
    SharedPreferences provideSharedPreferences(Application app) {
        return app.getSharedPreferences("com.genesys.gms.mobile.push.demo", MODE_PRIVATE);
    }

    static OkHttpClient createOkHttpClient(Application app) {
        OkHttpClient client = new OkHttpClient();

        // Install an HTTP cache in the application cache directory.
        try {
            File cacheDir = new File(app.getCacheDir(), "http");
            Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);
            client.setCache(cache);
        } catch (IOException e) {
            //Timber.e(e, "Unable to install disk cache.");
        }

        return client;
    }
}
