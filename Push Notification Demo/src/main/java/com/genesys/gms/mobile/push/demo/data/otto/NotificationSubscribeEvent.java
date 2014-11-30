package com.genesys.gms.mobile.push.demo.data.otto;

import com.genesys.gms.mobile.push.demo.data.api.pojo.NotificationSubscription;

/**
 * Created by stau on 30/11/2014.
 */
public class NotificationSubscribeEvent {
    public final String gmsUser;
    public final NotificationSubscription notificationSubscription;

    public NotificationSubscribeEvent(String gmsUser, NotificationSubscription notificationSubscription) {
        this.gmsUser = gmsUser;
        this.notificationSubscription = notificationSubscription;
    }

    @Override public String toString() {
        return getClass().getName() + "@" + hashCode() +
                "[" +
                "gmsUser=" + gmsUser +
                ",notificationSubscription=" + notificationSubscription +
                "]";
    }
}
