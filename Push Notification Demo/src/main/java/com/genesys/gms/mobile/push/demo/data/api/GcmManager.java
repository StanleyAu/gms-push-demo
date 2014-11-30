package com.genesys.gms.mobile.push.demo.data.api;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by stau on 30/11/2014.
 */
@Singleton
public class GcmManager {
    private final GoogleCloudMessaging googleCloudMessaging;

    @Inject
    public GcmManager(GoogleCloudMessaging googleCloudMessaging) {
        this.googleCloudMessaging = googleCloudMessaging;
    }

    @Subscribe public void onRegister() {

    }

    @Subscribe public void onSend() {

    }
}
