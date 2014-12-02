package com.genesys.gms.mobile.push.demo.data.otto;

import android.content.Intent;
import android.os.Bundle;

/**
 * Created by stau on 11/28/2014.
 */
public class GcmReceiveEvent {
    public final Bundle extras;
    private boolean produced;

    public GcmReceiveEvent(Intent intent) {
        this.extras = intent.getExtras();
        this.produced = false;
    }

    public GcmReceiveEvent(Intent intent, boolean produced) {
        this.extras = intent.getExtras();
        this.produced = produced;
    }

    public boolean isProduced() {
        return produced;
    }
    public GcmReceiveEvent setProduced(boolean produced) {
        this.produced = produced;
        return this;
    }

    @Override public String toString() {
        return getClass().getName() + "@" + hashCode() +
            "[" +
            "extras=" + extras.toString() +
            ",produced=" + produced +
            "]";
    }
}
