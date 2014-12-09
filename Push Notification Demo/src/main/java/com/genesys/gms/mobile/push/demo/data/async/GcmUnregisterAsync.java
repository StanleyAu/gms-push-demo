package com.genesys.gms.mobile.push.demo.data.async;

import android.os.AsyncTask;
import com.genesys.gms.mobile.push.demo.data.otto.GcmErrorEvent;
import com.genesys.gms.mobile.push.demo.data.otto.GcmUnregisterDoneEvent;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.squareup.otto.Bus;

import java.io.IOException;

/**
 * Created by Stan on 11/30/2014.
 *
 * GCM unregister() is an expensive call that should normally never be
 * used in a GCM client. However, we want to be able to hot-swap
 * Sender IDs during runtime.
 */
public class GcmUnregisterAsync extends AsyncTask<Void, Void, Boolean> {
    private final GoogleCloudMessaging googleCloudMessaging;
    private final Bus bus;
    private IOException savedException;

    public GcmUnregisterAsync(GoogleCloudMessaging googleCloudMessaging, Bus bus) {
        this.googleCloudMessaging = googleCloudMessaging;
        this.bus = bus;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            googleCloudMessaging.unregister();
            return true;
        } catch (IOException e) {
            savedException = e;
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if(result == true){
            bus.post(new GcmUnregisterDoneEvent());
        } else {
            bus.post(new GcmErrorEvent(savedException));
        }
    }
}
