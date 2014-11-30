package com.genesys.gms.mobile.push.demo.data.otto;

/**
 * Created by stau on 30/11/2014.
 */
public class NotificationDeleteEvent {
    public final String subscriberId;

    public NotificationDeleteEvent(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    @Override public String toString() {
        return getClass().getName() + "@" + hashCode() +
                "[" +
                "subscriberId=" + subscriberId +
                "]";
    }
}
