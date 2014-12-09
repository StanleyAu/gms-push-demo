package com.genesys.gms.mobile.push.demo.data.api;

import com.genesys.gms.mobile.push.demo.data.api.pojo.SubscriptionResponse;
import com.genesys.gms.mobile.push.demo.data.otto.*;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import hugo.weaving.DebugLog;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by stau on 30/11/2014.
 * Manages GMS Notification API calls through Otto bus events.
 * Note that almost all of the (HTTP) work is done by Retrofit.
 */
@Singleton
public class NotificationServiceManager {
    private final NotificationService notificationService;
    private final Bus bus;

    @DebugLog @Inject
    public NotificationServiceManager(NotificationService notificationService, Bus bus) {
        this.notificationService = notificationService;
        this.bus = bus;
    }

    // Define your interface
    @Subscribe public void onSubscribe(NotificationSubscribeEvent event) {
        Timber.i("Handling GMS Notification Subscribe request: " + event.toString());
        notificationService.subscribe(event.gmsUser, event.notificationSubscription, new Callback<SubscriptionResponse>() {
            @Override
            public void success(SubscriptionResponse subscriptionResponse, Response response) {
                bus.post(new NotificationSubscribeDoneEvent(subscriptionResponse));
            }

            @Override
            public void failure(RetrofitError error) {
                Timber.e("Failed GMS Notification Subscribe: " + error.toString());
                bus.post(new NotificationErrorEvent(error));
            }
        });
    }

    @Subscribe public void onDelete(NotificationDeleteEvent event) {
        Timber.i("Handling GMS Notification Delete request: " + event.toString());
        notificationService.delete(event.subscriberId, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                bus.post(new NotificationDeleteDoneEvent());
            }

            @Override
            public void failure(RetrofitError error) {
                Timber.e("Failed GMS Notification Delete: " + error.toString());
                bus.post(new NotificationErrorEvent(error));
            }
        });
    }

    @Subscribe public void onPublish(NotificationPublishEvent event) {
        Timber.i("Handling GMS Notification Publish request: " + event.toString());
        notificationService.publish(event.notificationEvent, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                bus.post(new NotificationPublishDoneEvent());
            }

            @Override
            public void failure(RetrofitError error) {
                Timber.e("Failed GMS Notification Publish: " + error.toString());
                bus.post(new NotificationErrorEvent(error));
            }
        });
    }
}
