package com.genesys.gms.mobile.push.demo.data.otto;

/**
 * Created by Stan on 11/30/2014.
 */
public class GcmRegisterDoneEvent {
    public final String registrationId;

    public GcmRegisterDoneEvent(String registrationId) {
        this.registrationId = registrationId;
    }

    @Override public String toString() {
        return getClass().getName() + "@" + hashCode() +
                "[" +
                "registrationId=" + registrationId +
                "]";
    }
}
