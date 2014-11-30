package com.genesys.gms.mobile.push.demo.data.api.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by stau on 11/27/2014.
 */
public class NotificationEvent {
    private final String message;
    private final String tag;
    private final MediaType mediaType;
    private final NotificationDetails notificationDetails;

    public enum MediaType {
        @SerializedName("string") STRING,
        @SerializedName("localizestring") LOCALIZESTRING
    }

    public NotificationEvent(String message, String tag, MediaType mediaType) {
        this.message = message;
        this.tag = tag;
        this.mediaType = mediaType;
        this.notificationDetails = null;
    }

    public NotificationEvent(String message,
                             String tag,
                             MediaType mediaType,
                             String deviceId,
                             NotificationDetails.ClientType clientType,
                             Map<String, String> properties) {
        this.message = message;
        this.tag = tag;
        this.mediaType = mediaType;
        this.notificationDetails = new NotificationDetails(deviceId, properties, clientType);
    }

    public String getMessage() {
        return message;
    }

    public String getTag() {
        return tag;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public NotificationDetails getNotificationDetails() {
        return notificationDetails;
    }

    @Override public String toString() {
        return getClass().getName() + "@" + hashCode() +
                "[" +
                "message=" + message +
                ",tag=" + tag +
                ",mediaType=" + mediaType.toString() +
                ",notificationDetails=" + notificationDetails +
                "]";
    }
}
