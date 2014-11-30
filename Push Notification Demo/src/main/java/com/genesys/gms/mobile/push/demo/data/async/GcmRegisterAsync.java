package com.genesys.gms.mobile.push.demo.data.async;

import android.os.AsyncTask;
import com.genesys.gms.mobile.push.demo.data.otto.GcmErrorEvent;
import com.genesys.gms.mobile.push.demo.data.otto.GcmRegisterDoneEvent;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.squareup.otto.Bus;

import java.io.IOException;

/**
 * Created by Stan on 11/30/2014.
 */
public class GcmRegisterAsync extends AsyncTask<Void, Void, String> {
    private final GoogleCloudMessaging googleCloudMessaging;
    private final String senderId;
    private final Bus bus;
    private IOException savedException;

    public GcmRegisterAsync(GoogleCloudMessaging googleCloudMessaging, String senderId, Bus bus) {
        super();
        this.googleCloudMessaging = googleCloudMessaging;
        this.senderId = senderId;
        this.bus = bus;
    }
    @Override
    protected String doInBackground(Void... params) {
        try {
            return googleCloudMessaging.register(senderId);
        } catch (IOException e) {
            savedException = e;
            return null;
        }
    }
    @Override
    protected void onPostExecute(String result) {
        if(result!=null && !result.isEmpty()) {
            bus.post(new GcmRegisterDoneEvent(result));
        } else if(savedException!=null) {
            bus.post(new GcmErrorEvent(savedException));
        } else {
            // Unknown error
        }
    }
}
