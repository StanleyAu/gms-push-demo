package com.genesys.gms.mobile.push.demo.data.otto;

import com.genesys.gms.mobile.push.demo.data.api.pojo.NotificationEvent;

/**
 * Created by stau on 30/11/2014.
 */
public class NotificationPublishEvent {
    public final NotificationEvent notificationEvent;

    public NotificationPublishEvent(NotificationEvent notificationEvent) {
        this.notificationEvent = notificationEvent;
    }

    @Override public String toString() {
        return getClass().getName() + "@" + hashCode() +
                "[" +
                "notificationEvent=" + notificationEvent +
                "]";
    }
}
