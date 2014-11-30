package com.genesys.gms.mobile.push.demo.data.api.pojo;

import com.genesys.gms.mobile.push.demo.data.api.NotificationService;

import java.net.NoRouteToHostException;
import java.util.Map;

/**
 * Created by stau on 11/27/2014.
 */
public class NotificationSubscription {
    private final String subscriberId;
    private final String providerName;
    private final NotificationDetails notificationDetails;
    private final int expire;
    private final String filter;

    public NotificationSubscription(String subscriberId,
                                    String providerName,
                                    String deviceId,
                                    Map<String, String> properties,
                                    NotificationDetails.ClientType clientType,
                                    int expire,
                                    String filter) {
        this.subscriberId = subscriberId;
        this.providerName = providerName;
        this.notificationDetails = new NotificationDetails(deviceId, properties, clientType);
        this.expire = expire;
        this.filter = filter;
    }

    public String getSubscriberId() {
        return subscriberId;
    }

    public String getProviderName() {
        return providerName;
    }

    public NotificationDetails getNotificationDetails() {
        return notificationDetails;
    }

    public int getExpire() {
        return expire;
    }

    public String getFilter() {
        return filter;
    }

    @Override public String toString() {
        return getClass().getName() + "@" + hashCode() +
                "[" +
                "subscriberId=" + subscriberId +
                ",providerName=" + providerName +
                ",notificationDetails=" + notificationDetails +
                ",expire=" + expire +
                ",filter=" + filter +
                "]";
    }
}
