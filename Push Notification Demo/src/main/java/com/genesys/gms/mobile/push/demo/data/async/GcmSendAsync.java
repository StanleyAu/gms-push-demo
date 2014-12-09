package com.genesys.gms.mobile.push.demo.data.async;

import android.os.AsyncTask;
import android.os.Bundle;
import com.genesys.gms.mobile.push.demo.data.otto.GcmErrorEvent;
import com.genesys.gms.mobile.push.demo.data.otto.GcmSendDoneEvent;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.squareup.otto.Bus;

import java.io.IOException;

/**
 * Created by Stan on 11/30/2014.
 *
 * A send() call is only useful when the GCM-implementing server supports
 * upstream messages from the GCM clients.
 */
public class GcmSendAsync extends AsyncTask<Void, Void, Boolean> {
    private final GoogleCloudMessaging googleCloudMessaging;
    private final String senderId;
    private final int msgId;
    private final Bundle data;
    private final Bus bus;
    private IOException savedException;

    public GcmSendAsync(GoogleCloudMessaging googleCloudMessaging, String senderId, int msgId, Bundle data, Bus bus) {
        super();
        this.googleCloudMessaging = googleCloudMessaging;
        this.senderId = senderId;
        this.msgId = msgId;
        this.data = data;
        this.bus = bus;
    }
    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            googleCloudMessaging.send(senderId + "@gcm.googleapis.com", Integer.toString(msgId), data);
            return true;
        } catch (IOException e) {
            savedException = e;
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if(result==true){
            bus.post(new GcmSendDoneEvent());
        } else {
            bus.post(new GcmErrorEvent(savedException));
        }
    }
}
