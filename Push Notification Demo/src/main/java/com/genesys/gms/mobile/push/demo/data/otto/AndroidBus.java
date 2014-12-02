package com.genesys.gms.mobile.push.demo.data.otto;

import android.os.Handler;
import android.os.Looper;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by stau on 11/28/2014.
 *
 * Subclassing Otto bus for cross-thread use (specifically, for the IntentService)
 * See solution as provided by Jake Wharton: https://github.com/square/otto/issues/38
 */
public class AndroidBus extends Bus {
    private final Handler handler = new Handler(Looper.getMainLooper());

    public AndroidBus(ThreadEnforcer enforcer) {
        super(enforcer);
    }

    @Override public void post(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    post(event);
                }
            });
        }
    }
}
