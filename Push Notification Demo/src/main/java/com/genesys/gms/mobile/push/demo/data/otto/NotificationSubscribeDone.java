package com.genesys.gms.mobile.push.demo.data.otto;

import com.genesys.gms.mobile.push.demo.data.api.pojo.SubscriptionResponse;

/**
 * Created by stau on 30/11/2014.
 */
public class NotificationSubscribeDone {
    public final SubscriptionResponse subscriptionResponse;

    public NotificationSubscribeDone(SubscriptionResponse subscriptionResponse) {
        this.subscriptionResponse = subscriptionResponse;
    }

    @Override public String toString() {
        return getClass().getName() + "@" + hashCode() +
                "[" +
                "subscriptionResponse=" + subscriptionResponse +
                "]";
    }
}
