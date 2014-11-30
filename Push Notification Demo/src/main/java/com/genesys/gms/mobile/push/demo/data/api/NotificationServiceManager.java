package com.genesys.gms.mobile.push.demo.data.api;

import com.genesys.gms.mobile.push.demo.data.api.pojo.SubscriptionResponse;
import com.genesys.gms.mobile.push.demo.data.otto.NotificationDeleteEvent;
import com.genesys.gms.mobile.push.demo.data.otto.NotificationPublishEvent;
import com.genesys.gms.mobile.push.demo.data.otto.NotificationSubscribeEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by stau on 30/11/2014.
 */
@Singleton
public class NotificationServiceManager {
    private final NotificationService mNotificationService;
    private final Bus mBus;

    @Inject
    public NotificationServiceManager(NotificationService notificationService, Bus bus) {
        mNotificationService = notificationService;
        mBus = bus;
    }

    // Define your interface
    @Subscribe public void onSubscribe(NotificationSubscribeEvent event) {
        mNotificationService.subscribe(event.gmsUser, event.notificationSubscription, new Callback<SubscriptionResponse>(){
            @Override
            public void success(SubscriptionResponse subscriptionResponse, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @Subscribe public void onDelete(NotificationDeleteEvent event) {
        mNotificationService.delete(event.subscriberId, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @Subscribe public void onPublish(NotificationPublishEvent event) {
        mNotificationService.publish(event.notificationEvent, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
