package com.genesys.gms.mobile.push.demo.data.api;

import com.genesys.gms.mobile.push.demo.data.api.pojo.SubscriptionResponse;
import com.genesys.gms.mobile.push.demo.data.otto.*;
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
    private final NotificationService notificationService;
    private final Bus bus;

    @Inject
    public NotificationServiceManager(NotificationService notificationService, Bus bus) {
        this.notificationService = notificationService;
        this.bus = bus;
    }

    // Define your interface
    @Subscribe public void onSubscribe(NotificationSubscribeEvent event) {
        notificationService.subscribe(event.gmsUser, event.notificationSubscription, new Callback<SubscriptionResponse>() {
            @Override
            public void success(SubscriptionResponse subscriptionResponse, Response response) {
                bus.post(new NotificationSubscribeDoneEvent(subscriptionResponse));
            }

            @Override
            public void failure(RetrofitError error) {
                bus.post(new NotificationErrorEvent(error));
            }
        });
    }

    @Subscribe public void onDelete(NotificationDeleteEvent event) {
        notificationService.delete(event.subscriberId, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                bus.post(new NotificationDeleteDoneEvent());
            }

            @Override
            public void failure(RetrofitError error) {
                bus.post(new NotificationErrorEvent(error));
            }
        });
    }

    @Subscribe public void onPublish(NotificationPublishEvent event) {
        notificationService.publish(event.notificationEvent, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                bus.post(new NotificationPublishDoneEvent());
            }

            @Override
            public void failure(RetrofitError error) {
                bus.post(new NotificationErrorEvent(error));
            }
        });
    }
}
