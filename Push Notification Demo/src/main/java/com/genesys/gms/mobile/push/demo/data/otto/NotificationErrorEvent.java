package com.genesys.gms.mobile.push.demo.data.otto;

import retrofit.RetrofitError;

/**
 * Created by Stan on 11/30/2014.
 */
public class NotificationErrorEvent {
    public final RetrofitError error;

    public NotificationErrorEvent(RetrofitError error) {
        this.error = error;
    }

    @Override public String toString() {
        return getClass().getName() + "@" + hashCode() +
                "[" +
                "error=" + error +
                "]";
    }
}
