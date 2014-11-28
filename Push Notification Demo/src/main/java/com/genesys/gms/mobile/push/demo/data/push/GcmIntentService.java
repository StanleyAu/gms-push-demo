package com.genesys.gms.mobile.push.demo.data.push;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import com.genesys.gms.mobile.push.demo.BaseIntentService;
import com.genesys.gms.mobile.push.demo.data.otto.GcmReceiveEvent;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.squareup.otto.Bus;

import javax.inject.Inject;

/**
 * Created by stau on 11/27/2014.
 */
public class GcmIntentService extends BaseIntentService {
    @Inject Bus bus;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if(!extras.isEmpty()) {
            GcmReceiveEvent event = new GcmReceiveEvent();
            event.extras = extras;
            if(GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                // Send error
            }
            else if(GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                // Messages deleted on server
            }
            else if(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // Actual message to process
                // Send to Activity via Bus (attn to threading)
            }
            bus.post(event);
        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
}
