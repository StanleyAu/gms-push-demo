package com.genesys.gms.mobile.push.demo.data.api;

import android.content.SharedPreferences;
import com.genesys.gms.mobile.push.demo.BuildConfig;
import com.genesys.gms.mobile.push.demo.data.retrofit.GmsEndpoint;
import com.genesys.gms.mobile.push.demo.ui.SettingsFragment;
import com.squareup.okhttp.OkHttpClient;
import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.client.Client;
import retrofit.client.OkClient;
import timber.log.Timber;

import javax.inject.Singleton;

/**
 * Created by stau on 11/27/2014.
 */
@Module(
    complete = false,
    library = true
)
public class ApiModule {
    @Provides @Singleton
    GmsEndpoint provideEndpoint(SharedPreferences sharedPreferences) {
        GmsEndpoint gmsEndpoint = new GmsEndpoint();
        String host = sharedPreferences.getString(SettingsFragment.PROPERTY_HOST, "");
        String port = sharedPreferences.getString(SettingsFragment.PROPERTY_PORT, "");
        int apiVersion = sharedPreferences.getInt(SettingsFragment.PROPERTY_API_VERSION, 1);
        if(!host.isEmpty() && !port.isEmpty()) {
            gmsEndpoint.setUrl(host, port, apiVersion);
        }
        return gmsEndpoint;
    }

    @Provides @Singleton
    Client provideClient(OkHttpClient client) {
        return new OkClient(client);
    }

    @Provides @Singleton
    RestAdapter provideRestAdapter(GmsEndpoint endpoint, Client client) {
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setClient(client)
                .setEndpoint(endpoint);
        if(BuildConfig.DEBUG) {
            builder.setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(new RestAdapter.Log() {
                    @Override
                    public void log(String msg) {
                        Timber.tag("Retrofit");
                        Timber.d(msg);
                    }
                });
        }
        return builder.build();
    }

    @Provides @Singleton
    NotificationService provideNotificationService(RestAdapter restAdapter) {
        return restAdapter.create(NotificationService.class);
    }
}
