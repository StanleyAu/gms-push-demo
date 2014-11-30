package com.genesys.gms.mobile.push.demo.data.otto;

/**
 * Created by stau on 30/11/2014.
 */
public class GcmRegisterEvent {
    public final String senderId;

    public GcmRegisterEvent(String senderId) {
        this.senderId = senderId;
    }

    @Override public String toString() {
        return getClass().getName() + "@" + hashCode() +
                "[" +
                "senderId=" + senderId +
                "]";
    }
}
