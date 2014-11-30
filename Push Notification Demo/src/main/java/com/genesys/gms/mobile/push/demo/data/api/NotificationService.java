package com.genesys.gms.mobile.push.demo.data.api;

import com.genesys.gms.mobile.push.demo.data.api.pojo.NotificationEvent;
import com.genesys.gms.mobile.push.demo.data.api.pojo.NotificationSubscription;
import com.genesys.gms.mobile.push.demo.data.api.pojo.SubscriptionResponse;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.*;

/**
 * Created by stau on 11/27/2014.
 */
public interface NotificationService {
    @POST("/notification/subscription")
    public SubscriptionResponse subscribe(@Header("gms_user") String gmsUser,
                                          @Body NotificationSubscription notificationSubscription);

    @POST("/notification/subscription")
    public void subscribe(@Header("gms_user") String gmsUser,
                          @Body NotificationSubscription notificationSubscription,
                          Callback<SubscriptionResponse> callback);

    @DELETE("/notification/subscription/{subscriptionId}")
    public Response delete(@Path("subscriptionId") String subscriptionId);

    @DELETE("/notification/subscription/{subscriptionId}")
    public void delete(@Path("subscriptionId") String subscriptionId,
                       Callback<Response> callback);

    @POST("/notification/publish")
    public Response publish(@Body NotificationEvent notificationEvent);

    @POST("/notification/publish")
    public void publish(@Body NotificationEvent notificationEvent,
                        Callback<Response> callback);
}
