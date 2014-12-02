package com.genesys.gms.mobile.push.demo.data.api;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import com.genesys.gms.mobile.push.demo.ForApplication;
import com.genesys.gms.mobile.push.demo.R;
import com.genesys.gms.mobile.push.demo.data.async.GcmRegisterAsync;
import com.genesys.gms.mobile.push.demo.data.async.GcmSendAsync;
import com.genesys.gms.mobile.push.demo.data.async.GcmUnregisterAsync;
import com.genesys.gms.mobile.push.demo.data.otto.GcmReceiveEvent;
import com.genesys.gms.mobile.push.demo.data.otto.GcmRegisterEvent;
import com.genesys.gms.mobile.push.demo.data.otto.GcmSendEvent;
import com.genesys.gms.mobile.push.demo.data.otto.GcmUnregisterEvent;
import com.genesys.gms.mobile.push.demo.ui.MainActivity;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.squareup.otto.Bus;
import com.squareup.otto.DeadEvent;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;
import hugo.weaving.DebugLog;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by stau on 30/11/2014.
 */
@Singleton
public class GcmManager {
    private final GoogleCloudMessaging googleCloudMessaging;
    private final Bus bus;
    private final Context context;
    private AtomicInteger idGen = new AtomicInteger();

    NotificationManager mNotificationManager;
    private static final long[] VIBRATE_PATTERN = {350L, 200L, 350L};
    public static final String GCM_NOTIFICATION_ID = "gcm_notification_id";
    private Object savedEvent;

    @DebugLog @Inject
    public GcmManager(GoogleCloudMessaging googleCloudMessaging, Bus bus, @ForApplication Context context) {
        this.googleCloudMessaging = googleCloudMessaging;
        this.bus = bus;
        this.context = context;
    }

    @Subscribe public void onRegister(GcmRegisterEvent event) {
        // Cache should be consulted prior to delivering GcmRegisterEvent
        GcmRegisterAsync async = new GcmRegisterAsync(googleCloudMessaging, event.senderId, bus);
        async.execute();
    }

    @Subscribe public void onSend(GcmSendEvent event) {
        // TODO: Handle event ID generation
        // This isn't very useful to us since the server doesn't handle upstream messages
        GcmSendAsync async = new GcmSendAsync(
                googleCloudMessaging,
                event.senderId,
                idGen.getAndIncrement(),
                event.data,
                bus
        );
        async.execute();
    }

    @Subscribe public void onUnregister(GcmUnregisterEvent event) {
        GcmUnregisterAsync async = new GcmUnregisterAsync(googleCloudMessaging, bus);
        async.execute();
    }

    @DebugLog
    @Subscribe public void onDeadEvent(DeadEvent event) {
        // TODO: When MainActivity starts, we want to see the notification...
        CharSequence message = "Message received!";
        if(event.event instanceof GcmReceiveEvent) {
            savedEvent = event.event;
            CharSequence extraMessage = ((GcmReceiveEvent) savedEvent).extras.getCharSequence("message");
            if(extraMessage != null) {
                message = extraMessage;
            }
        }

        NotificationCompat.Builder builder =
            getNotificationBuilder(R.drawable.ic_launcher, "GMS Push Demo", message);

        // int notificationId = mNotifyId.getAndIncrement();
        Intent resultIntent = new Intent(context, MainActivity.class);
        //resultIntent.putExtra(GCM_NOTIFICATION_ID, notificationId);
        resultIntent.putExtra(GCM_NOTIFICATION_ID, 0);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
            stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
            );
        builder.setContentIntent(resultPendingIntent);
        //getNotificationManager().notify(notificationId, builder.build());
        getNotificationManager().notify(0, builder.build());
    }

    @Produce public GcmReceiveEvent produceGcmReceiveEvent() {
        if(savedEvent != null) {
            GcmReceiveEvent event = (GcmReceiveEvent)savedEvent;
            event.setProduced(true);
            savedEvent = null;
            return event;
        } else {
            return null;
        }
    }

    protected NotificationManager getNotificationManager() {
        if(mNotificationManager==null) {
            mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mNotificationManager;
    }

    protected NotificationCompat.Builder getNotificationBuilder(int icon, CharSequence title, CharSequence text) {
        // TODO: Is it worthwhile to cache this at all?
        return new NotificationCompat.Builder(context)
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(text)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true);
    }
}
