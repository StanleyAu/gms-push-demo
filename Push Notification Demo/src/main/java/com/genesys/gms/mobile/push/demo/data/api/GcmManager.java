package com.genesys.gms.mobile.push.demo.data.api;

import com.genesys.gms.mobile.push.demo.data.async.GcmRegisterAsync;
import com.genesys.gms.mobile.push.demo.data.async.GcmSendAsync;
import com.genesys.gms.mobile.push.demo.data.async.GcmUnregisterAsync;
import com.genesys.gms.mobile.push.demo.data.otto.GcmRegisterEvent;
import com.genesys.gms.mobile.push.demo.data.otto.GcmSendEvent;
import com.genesys.gms.mobile.push.demo.data.otto.GcmUnregisterEvent;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by stau on 30/11/2014.
 */
@Singleton
public class GcmManager {
    private final GoogleCloudMessaging googleCloudMessaging;
    private final Bus bus;
    private AtomicInteger idGen = new AtomicInteger();

    @Inject
    public GcmManager(GoogleCloudMessaging googleCloudMessaging, Bus bus) {
        this.googleCloudMessaging = googleCloudMessaging;
        this.bus = bus;
    }

    @Subscribe public void onRegister(GcmRegisterEvent event) {
        // Cache should be consulted prior to delivering GcmRegisterEvent
        GcmRegisterAsync async = new GcmRegisterAsync(googleCloudMessaging, event.senderId, bus);
        async.execute();
    }

    @Subscribe public void onSend(GcmSendEvent event) {
        // TODO: Handle event ID generation
        // This isn't very useful to us since the server doesn't handle upstream messages
        GcmSendAsync async = new GcmSendAsync(
                googleCloudMessaging,
                event.senderId,
                idGen.getAndIncrement(),
                event.data,
                bus
        );
        async.execute();
    }

    @Subscribe public void onUnregister(GcmUnregisterEvent event) {
        GcmUnregisterAsync async = new GcmUnregisterAsync(googleCloudMessaging, bus);
        async.execute();
    }
}
