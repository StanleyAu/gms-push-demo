package com.genesys.gms.mobile.push.demo.data.async;

import android.os.AsyncTask;
import com.genesys.gms.mobile.push.demo.data.otto.GcmErrorEvent;
import com.genesys.gms.mobile.push.demo.data.otto.GcmUnregisterDoneEvent;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.squareup.otto.Bus;

import java.io.IOException;

/**
 * Created by Stan on 11/30/2014.
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
