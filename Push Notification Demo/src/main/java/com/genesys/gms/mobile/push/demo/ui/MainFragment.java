package com.genesys.gms.mobile.push.demo.ui;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.ButterKnife.Setter;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;
import com.genesys.gms.mobile.push.demo.BaseFragment;
import com.genesys.gms.mobile.push.demo.ForActivity;
import com.genesys.gms.mobile.push.demo.R;
import com.genesys.gms.mobile.push.demo.data.api.NotificationService;
import com.genesys.gms.mobile.push.demo.data.api.pojo.NotificationDetails;
import com.genesys.gms.mobile.push.demo.data.api.pojo.PushEvent;
import com.genesys.gms.mobile.push.demo.data.api.pojo.SubscribeParams;
import com.genesys.gms.mobile.push.demo.data.otto.GcmPublishEvent;
import com.genesys.gms.mobile.push.demo.data.otto.GcmReceiveEvent;
import com.genesys.gms.mobile.push.demo.data.otto.GcmRegisterEvent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

import javax.inject.Inject;
import java.awt.font.NumericShaper;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by stau on 11/27/2014.
 */
public class MainFragment extends BaseFragment {
    @InjectView(R.id.editSenderId) EditText editSenderId;
    @InjectView(R.id.txtRegistration) TextView txtRegistration;
    @InjectView(R.id.editExpiry) EditText editExpiry;
    @InjectView(R.id.editFilter) EditText editFilter;
    @InjectView(R.id.editMessage) EditText editMessage;
    @InjectView(R.id.editTag) EditText editTag;
    @InjectView(R.id.txtReceipt) TextView txtReceipt;
    @InjectView(R.id.btnRegister) Button btnRegister;
    @InjectView(R.id.btnTest) Button btnTest;
    @InjectView(R.id.progressBar) ProgressBar progressBar;
    @InjectViews({R.id.btnSubscribe, R.id.btnCancel, R.id.btnPublish}) List<View> listGmsButtons;

    @Inject @ForActivity Context context;
    @Inject SharedPreferences sharedPreferences;
    @Inject GoogleCloudMessaging googleCloudMessaging;
    @Inject Bus bus;
    @Inject NotificationService notificationService;
    @Inject NotificationManager notificationManager;

    public final static String EXTRA_MESSAGE = "message";
    public final static String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private String m_strGcmRegId;
    private String m_strGmsSubId;
    AtomicInteger m_msgId = new AtomicInteger();
    AtomicInteger m_notifyId = new AtomicInteger();
    private NotificationCompat.Builder mBuilder;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.inject(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        handleInstanceState(savedInstanceState);
        checkPlayAndGcm();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
        checkPlayAndGcm();
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    // TODO: Restore onActivityCreated
    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Called last
        super.onSaveInstanceState(outState);
    }

    private void handleInstanceState(Bundle inState) {
        if(inState==null) {
            return;
        }
    }

    @OnClick(R.id.btnRegister)
    public void handleRegister() {
        btnRegister.setEnabled(false);
        new RegisterGcmTask().execute(getSenderId());
    }

    @OnClick(R.id.btnTest)
    public void handleTest() {
        btnTest.setEnabled(false);
        new PublishGcmTask().execute(getSenderId());
    }

    @OnClick(R.id.btnSubscribe)
    public void handleSubscribe() {
        ButterKnife.apply(listGmsButtons, ENABLED, false);
        SubscribeParams params = new SubscribeParams();
        String strExpiry = editExpiry.getText().toString();
        try {
            params.expire = Integer.parseInt(strExpiry);
        } catch (NumberFormatException e) {
            editExpiry.setText("60");
            params.expire = 60;
        }
        String strFilter = editFilter.getText().toString();
        if(strFilter.isEmpty()) {
            strFilter = "*";
            editFilter.setText("*");
        }
        params.filter = strFilter;
        params.notificationDetails.deviceId = m_strGcmRegId;
        params.notificationDetails.type = NotificationDetails.ClientType.GCM;
        params.subscriberId = getUniqueId(context);
        // Create an async task (unless we use an rxJava observer)
        new GmsSubscribeTask().execute(params);
    }

    @OnClick(R.id.btnCancel)
    public void handleCancel() {
        ButterKnife.apply(listGmsButtons, ENABLED, false);
        new GmsCancelTask().execute(m_strGmsSubId);
    }

    @OnClick(R.id.btnPublish)
    public void handlePublish() {
        ButterKnife.apply(listGmsButtons, ENABLED, false);
        PushEvent params = new PushEvent();
        String strMessage = editMessage.getText().toString();
        if(strMessage.isEmpty()) {
            editMessage.setText("Hello world!");
            strMessage = "Hello world!";
        }
        params.message = strMessage;
        String strTag = editTag.getText().toString();
        if(strTag.isEmpty()) {
            editTag.setText("push.demo");
            strTag = "push.demo";
        }
        params.tag = strTag;
        params.mediaType = PushEvent.MediaType.STRING;
        // Create an async task
        new GmsPublishTask().execute(params);
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if(resultCode!= ConnectionResult.SUCCESS){
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(), PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                // TODO: Use Otto to tell activity to kill itself.
                getActivity().finish();
            }
            return false;
        }
        return true;
    }

    private String getRegistrationId(Context context) {
        String registrationId = sharedPreferences.getString(PROPERTY_REG_ID, "");
        if(registrationId.isEmpty()) {
            // No registration found
            return "";
        }
        int registeredVersion = sharedPreferences.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if(registeredVersion!=currentVersion) {
            // Version changed
            return "";
        }
        return registrationId;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private NotificationCompat.Builder buildNotificationCommon(Context context) {
        if(mBuilder == null) {
            mBuilder = new NotificationCompat.Builder(context)
                .setContentTitle("GMS Push Demo")
                .setContentText("Notification received!");
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mBuilder.setSound(alarmSound);
        }
        return mBuilder;
    }

    private static String getUniqueId(Context context) {
        return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
    }

    private void registerInBackground() {
        new RegisterGcmTask().execute(getSenderId());
    }

    private void publishInBackground() {
        new PublishGcmTask().execute(getSenderId());
    }

    private void storeRegistrationId(Context context, String regId) {
        int appVersion = getAppVersion(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.apply();
    }

    private void checkPlayAndGcm() {
        if(checkPlayServices()){
            btnRegister.setEnabled(true);
            m_strGcmRegId = getRegistrationId(context);
            txtRegistration.setText(m_strGcmRegId);
            if(!m_strGcmRegId.isEmpty()) {
                btnTest.setEnabled(true);
            }
        }
    }

    private String getSenderId() {
        if(editSenderId!=null) {
            return editSenderId.getText().toString();
        }
        return "";
    }

    @Subscribe public void gcmRegistered(GcmRegisterEvent event) {
        if(event.result==true) {
            txtRegistration.setText(event.registrationId);
            btnTest.setEnabled(true);
        } else {
            txtRegistration.setText(getResources().getString(R.string.error));
        }
        Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show();
        btnRegister.setEnabled(true);
    }

    @Subscribe public void gcmPublished(GcmPublishEvent event) {
        Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show();
        btnTest.setEnabled(true);
    }

    @Subscribe public void gcmReceived(GcmReceiveEvent event) {
        txtReceipt.setText(event.extras.toString());
        //notificationManager.notify(m_notifyId.incrementAndGet(), buildNotificationCommon(context).build());
        Toast.makeText(context, "Message received!", Toast.LENGTH_SHORT).show();
    }

    private class RegisterGcmTask extends AsyncTask<String, Void, GcmRegisterEvent> {
        @Override
        protected GcmRegisterEvent doInBackground(String... params) {
            GcmRegisterEvent event = new GcmRegisterEvent();
            if(params.length==0) {
                event.message = "No parameters provided.";
                event.result = false;
                return event;
            }
            String gcmRegId;
            String senderId = params[0];
            if(senderId.isEmpty()) {
                event.message = "Sender ID is not set.";
                event.result = false;
                return event;
            }
            try {
                event.registrationId = googleCloudMessaging.register(senderId);
                event.message = "Device registered, registration ID=" + event.registrationId;
                event.result = true;
                storeRegistrationId(context, event.registrationId);
            } catch (IOException e) {
                event.message = "Failed to register: " + e.getMessage();
                event.result = false;
            }
            return event;
        }

        @Override
        protected void onPostExecute(GcmRegisterEvent result) {
            bus.post(result);
        }
    }

    /** This Publish goes back to server **/
    private class PublishGcmTask extends AsyncTask<String, Void, GcmPublishEvent> {
        @Override
        protected GcmPublishEvent doInBackground(String... params) {
            GcmPublishEvent event = new GcmPublishEvent();
            if(params.length==0) {
                event.message = "No parameters provided.";
                event.result = false;
                return event;
            }
            String senderId = params[0];
            if(senderId.isEmpty()) {
                event.message = "Sender ID is not set.";
                event.result = false;
                return event;
            }
            try {
                Bundle data = new Bundle();
                data.putString("my_message", "Hello World");
                String id = Integer.toString(m_msgId.incrementAndGet());
                googleCloudMessaging.send(getSenderId() + "@gcm.googleapis.com", id, data);
                event.message = "Sent message";
                event.result = true;
            } catch (IOException e) {
                event.message = "Failed to send: " + e.getMessage();
                event.result = false;
            }
            return event;
        }
        @Override
        protected void onPostExecute(GcmPublishEvent result) {
            bus.post(result);
        }
    }

    private class GmsSubscribeTask extends AsyncTask<SubscribeParams, Void, Void> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected Void doInBackground(SubscribeParams... params) {
            SubscribeParams param = params[0];
            try {
                Response response = notificationService.subscribe(getUniqueId(context), param);
                Log.d("TEST", response.toString());
            } catch (RetrofitError e) {
                if (e.getResponse() != null) {
                    String json = new String(((TypedByteArray) e.getResponse().getBody()).getBytes());
                    Log.d("TEST", json);
                } else {
                    Log.d("TEST", "Null response");
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            progressBar.setVisibility(View.GONE);
            ButterKnife.apply(listGmsButtons, ENABLED, true);
        }
    }

    private class GmsCancelTask extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected Void doInBackground(String... params) {
            String subscriptionId = params[0];
            try {
                Response response = notificationService.delete(subscriptionId);
                Log.d("TEST", response.toString());
            } catch (RetrofitError e) {
                if (e.getResponse() != null) {
                    String json = new String(((TypedByteArray) e.getResponse().getBody()).getBytes());
                    Log.d("TEST", json);
                } else {
                    Log.d("TEST", "Null response");
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            progressBar.setVisibility(View.GONE);
            ButterKnife.apply(listGmsButtons, ENABLED, true);
        }
    }

    private class GmsPublishTask extends AsyncTask<PushEvent, Void, Void> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected Void doInBackground(PushEvent... params) {
            PushEvent param = params[0];
            try {
                Response response = notificationService.publish(param);
                Log.d("TEST", response.toString());
            } catch (RetrofitError e) {
                if (e.getResponse() != null) {
                    String json = new String(((TypedByteArray) e.getResponse().getBody()).getBytes());
                    Log.d("TEST", json);
                } else {
                    Log.d("TEST", "Null response");
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            progressBar.setVisibility(View.GONE);
            ButterKnife.apply(listGmsButtons, ENABLED, true);
        }
    }

    static final Setter<View, Boolean> ENABLED = new Setter<View, Boolean>() {
        @Override
        public void set(View view, Boolean value, int index) {
            view.setEnabled(value);
        }
    };
}
