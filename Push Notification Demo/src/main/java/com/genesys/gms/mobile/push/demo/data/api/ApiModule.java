package com.genesys.gms.mobile.push.demo.data.api;

import com.genesys.gms.mobile.push.demo.data.retrofit.GmsEndpoint;
import com.squareup.okhttp.OkHttpClient;
import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.client.Client;
import retrofit.client.OkClient;

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
    GmsEndpoint provideEndpoint() {
        return new GmsEndpoint();
    }

    @Provides @Singleton
    Client provideClient(OkHttpClient client) {
        return new OkClient(client);
    }

    @Provides @Singleton
    RestAdapter provideRestAdapter(GmsEndpoint endpoint, Client client) {
        return new RestAdapter.Builder()
            .setClient(client)
            .setEndpoint(endpoint)
            .build();
    }

    @Provides @Singleton
    NotificationService provideNotificationService(RestAdapter restAdapter) {
        return restAdapter.create(NotificationService.class);
    }
}
