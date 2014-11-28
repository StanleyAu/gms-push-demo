package com.genesys.gms.mobile.push.demo.data.api.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by stau on 11/27/2014.
 */
public class NotificationDetails {
    public String deviceId;
    public Map<String, String> properties = new HashMap<String, String>();
    public ClientType type;

    public enum ClientType {
        @SerializedName("android") ANDROID,
        @SerializedName("gcm") GCM,
        @SerializedName("ios") IOS,
        @SerializedName("httpcb") HTTPCB,
        @SerializedName("orscb") ORSCB,
        UNKNOWN
    }
}
