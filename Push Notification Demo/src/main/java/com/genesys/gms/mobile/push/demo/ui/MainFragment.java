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
import com.genesys.gms.mobile.push.demo.data.api.pojo.NotificationEvent;
import com.genesys.gms.mobile.push.demo.data.api.pojo.NotificationSubscription;
import com.genesys.gms.mobile.push.demo.data.api.pojo.SubscriptionResponse;
import com.genesys.gms.mobile.push.demo.data.otto.*;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by stau on 11/27/2014.
 */
public class MainFragment extends BaseFragment {
    @InjectView(R.id.editSenderId) EditText editSenderId;
    @InjectView(R.id.txtRegistration) TextView txtRegistration;
    @InjectView(R.id.editExpire) EditText editExpire;
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

    public final static String PROPERTY_SENDER_ID = "sender_id";
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
        checkPlayServicesAndGcm();
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
        checkPlayServicesAndGcm();
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

    private static String getUniqueId(Context context) {
        return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
    }

    private void storeRegistrationId(Context context, String regId) {
        int appVersion = getAppVersion(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putString(PROPERTY_SENDER_ID, getSenderId());
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.apply();
    }

    private void checkPlayServicesAndGcm() {
        if(checkPlayServices()){
            btnRegister.setEnabled(true);
            m_strGcmRegId = getRegistrationId(context);
            if(!m_strGcmRegId.isEmpty()) {
                btnTest.setEnabled(true);
                txtRegistration.setText(m_strGcmRegId);
            }
            editSenderId.setText(sharedPreferences.getString(PROPERTY_SENDER_ID, ""));
        }
    }

    /**     FIELD GETTERS **/
    private String getSenderId() {
        return editSenderId.getText().toString();
    }

    private int getExpire() {
        String tmp = editExpire.getText().toString();
        try {
            return Integer.parseInt(tmp);
        } catch (NumberFormatException e) {
            editExpire.setText("60");
            return 60;
        }
    }

    private String getFilter() {
        String tmp = editFilter.getText().toString();
        if(tmp.isEmpty()) {
            tmp = "*";
            editFilter.setText(tmp);
        }
        return tmp;
    }

    private String getMessage() {
        String tmp = editMessage.getText().toString();
        if(tmp.isEmpty()) {
            tmp = "Hello world!";
            editMessage.setText(tmp);
        }
        return tmp;
    }

    private String getMessageTag() {
        String tmp = editTag.getText().toString();
        if(tmp.isEmpty()) {
            tmp = "push.demo";
            editTag.setText(tmp);
        }
        return tmp;
    }
    /** END FIELD GETTERS **/

    /**     BUTTON HANDLERS **/
    @OnClick(R.id.btnRegister)
    public void handleRegister() {
        bus.post(new GcmRegisterEvent(getSenderId()));
    }
    @OnClick(R.id.btnTest)
    public void handleTest() {
        Bundle data = new Bundle();
        data.putString("my_message", "Hello world!");
        bus.post(new GcmSendEvent(getSenderId(), data));
    }
    @OnClick(R.id.btnSubscribe)
    public void handleSubscribe() {
        String identifier = getUniqueId(context);
        NotificationSubscription notificationSubscription = new NotificationSubscription(
                identifier,
                null,
                m_strGcmRegId,
                null,
                NotificationDetails.ClientType.GCM,
                getExpire(),
                getFilter()
        );
        bus.post(new NotificationSubscribeEvent(identifier, notificationSubscription));
    }
    @OnClick(R.id.btnCancel)
    public void handleCancel() {
        bus.post(new NotificationDeleteEvent(m_strGmsSubId));
    }
    @OnClick(R.id.btnPublish)
    public void handlePublish() {
        NotificationEvent notificationEvent = new NotificationEvent(
                getMessage(),
                getMessageTag(),
                NotificationEvent.MediaType.STRING,
                m_strGcmRegId,
                NotificationDetails.ClientType.GCM,
                null
        );
        bus.post(new NotificationPublishEvent(notificationEvent));
    }
    /** END BUTTON HANDLERS **/

    @Subscribe public void onGcmRegisterDone(GcmRegisterDoneEvent event) {
        m_strGcmRegId = event.registrationId;
        storeRegistrationId(context, m_strGcmRegId);
        txtRegistration.setText(event.registrationId);
        Toast.makeText(context, "Registered (id:" + event.registrationId + ")", Toast.LENGTH_SHORT).show();
    }
    @Subscribe public void onGcmSendDone(GcmSendDoneEvent event) {
        Toast.makeText(context, "Upstream message delivered.", Toast.LENGTH_SHORT).show();
    }
    @Subscribe public void onGcmUnregisterDone(GcmUnregisterDoneEvent event) {
        Toast.makeText(context, "Unregistered", Toast.LENGTH_SHORT).show();
    }
    @Subscribe public void onGcmError(GcmErrorEvent event) {
        Toast.makeText(context, "GCM Error: " + event.error.getMessage(), Toast.LENGTH_SHORT).show();
    }
    @Subscribe public void gcmReceived(MyGcmReceiveEvent event) {
        txtReceipt.setText(event.extras.toString());
        Toast.makeText(context, "Message received!", Toast.LENGTH_SHORT).show();
    }

    @Subscribe public void onNotificationSubscribeDone(NotificationSubscribeDoneEvent event) {
        m_strGmsSubId = event.subscriptionResponse.getId();
        Toast.makeText(context, "Subscribed (id:" + event.subscriptionResponse.getId() + ")", Toast.LENGTH_SHORT).show();
    }
    @Subscribe public void onNotificationDeleteDone(NotificationDeleteDoneEvent event) {
        m_strGmsSubId = null;
        Toast.makeText(context, "Subscription deleted.", Toast.LENGTH_SHORT).show();
    }
    @Subscribe public void onNotificationPublishDone(NotificationPublishDoneEvent event) {
        Toast.makeText(context, "Message published.", Toast.LENGTH_SHORT).show();
    }
    @Subscribe public void onNotificationError(NotificationErrorEvent event) {
        Toast.makeText(context, "Notification API Error: " + event.error.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
