package com.genesys.gms.mobile.push.demo.data.api.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by stau on 11/27/2014.
 */
public class PushEvent {
    public String message;
    public String tag;
    public MediaType mediaType;
    public NotificationDetails notificationDetails = new NotificationDetails();

    public enum MediaType {
        @SerializedName("string") STRING,
        @SerializedName("localizestring") LOCALIZESTRING
    }
}
