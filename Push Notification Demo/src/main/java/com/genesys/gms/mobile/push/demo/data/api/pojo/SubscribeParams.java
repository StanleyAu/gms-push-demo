package com.genesys.gms.mobile.push.demo.data.api.pojo;

/**
 * Created by stau on 11/27/2014.
 */
public class SubscribeParams {
    public String subscriberId;
    public String providerName;
    public NotificationDetails notificationDetails = new NotificationDetails();
    public int expire;
    public String filter;
}
