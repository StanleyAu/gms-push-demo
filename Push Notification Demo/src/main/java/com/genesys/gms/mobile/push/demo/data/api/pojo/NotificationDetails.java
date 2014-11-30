package com.genesys.gms.mobile.push.demo.data.api.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by stau on 11/27/2014.
 */
public class NotificationDetails {
    private final String deviceId;
    private final Map<String, String> properties;
    private final ClientType type;

    public enum ClientType {
        @SerializedName("android") ANDROID,
        @SerializedName("gcm") GCM,
        @SerializedName("ios") IOS,
        @SerializedName("httpcb") HTTPCB,
        @SerializedName("orscb") ORSCB,
        UNKNOWN
    }

    public NotificationDetails(String deviceId, Map<String, String> properties, ClientType type) {
        this.deviceId = deviceId;
        this.properties = properties;
        this.type = type;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public Map<String, String> getProperties() {
        // Potential for modification here
        return properties;
    }

    public ClientType getClientType() {
        return type;
    }

    @Override public String toString() {
        return getClass().getName() + "@" + hashCode() +
                "[" +
                "deviceId=" + deviceId +
                ",properties=" + properties +
                ",type=" + type.toString() +
                "]";
    }
}
