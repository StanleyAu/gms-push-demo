package com.genesys.gms.mobile.push.demo.data.otto;

import android.os.Bundle;

/**
 * Created by stau on 30/11/2014.
 */
public class GcmSendEvent {
    public final String senderId;
    public final Bundle data;

    public GcmSendEvent(String senderId, Bundle data) {
        this.senderId = senderId;
        this.data = data;
    }

    @Override public String toString(){
        return getClass().getName() + "@" + hashCode() +
                "[" +
                "senderId=" + senderId +
                ",data=" + data +
                "]";
    }
}
