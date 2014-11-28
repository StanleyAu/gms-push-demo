package com.genesys.gms.mobile.push.demo.data.api;

import com.genesys.gms.mobile.push.demo.data.api.pojo.PushEvent;
import com.genesys.gms.mobile.push.demo.data.api.pojo.SubscribeParams;
import retrofit.client.Response;
import retrofit.http.*;

/**
 * Created by stau on 11/27/2014.
 */
public interface NotificationService {
    @POST("/notification/subscription")
    public Response subscribe(@Header("gms_user") String gmsUser, @Body SubscribeParams subscribeParams);

    @DELETE("/notification/subscription/{subscriptionId}")
    public Response delete(@Path("subscriptionId") String subscriptionId);

    @POST("/notification/publish")
    public Response publish(@Body PushEvent pushEvent);
}
