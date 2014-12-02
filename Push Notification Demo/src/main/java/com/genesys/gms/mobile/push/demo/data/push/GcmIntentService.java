package com.genesys.gms.mobile.push.demo.data.push;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import com.genesys.gms.mobile.push.demo.BaseIntentService;
import com.genesys.gms.mobile.push.demo.R;
import com.genesys.gms.mobile.push.demo.data.otto.GcmReceiveEvent;
import com.genesys.gms.mobile.push.demo.ui.MainActivity;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.squareup.otto.Bus;
import com.squareup.otto.DeadEvent;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;
import hugo.weaving.DebugLog;

import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by stau on 11/27/2014.
 */
public class GcmIntentService extends BaseIntentService {
    @Inject Bus bus;
    public static final String GCM_NOTIFICATION_ID = "gcm_notification_id";

    @DebugLog
    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        GcmReceiveEvent event = new GcmReceiveEvent(intent, false);
        if(event.extras != null) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                // Send error
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                // Messages deleted on server
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // Actual message to process
                // Send to Activity via Bus (attn to threading)
            }
            Log.d("GcmIntentService", event.extras.toString());
        }
        bus.post(event);

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
}
