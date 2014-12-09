package com.genesys.gms.mobile.push.demo.ui;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.view.*;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.genesys.gms.mobile.push.demo.BaseFragment;
import com.genesys.gms.mobile.push.demo.ForActivity;
import com.genesys.gms.mobile.push.demo.R;
import com.genesys.gms.mobile.push.demo.data.api.pojo.NotificationDetails;
import com.genesys.gms.mobile.push.demo.data.api.pojo.NotificationEvent;
import com.genesys.gms.mobile.push.demo.data.api.pojo.NotificationSubscription;
import com.genesys.gms.mobile.push.demo.data.otto.*;
import com.genesys.gms.mobile.push.demo.data.retrofit.GmsEndpoint;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import hugo.weaving.DebugLog;
import timber.log.Timber;

import javax.inject.Inject;

// TODO: Consider shipping off some of this code into a Presenter

/**
 * Created by stau on 11/27/2014.
 *
 * Most of the action occurs in the MainFragment, which houses the controls
 * for GCM registration and interacting with the GMS Notification API.
 */
public class MainFragment extends BaseFragment {
    // ButterKnife helps do all the findViewById work
    @InjectView(R.id.editSenderId) EditText editSenderId;
    @InjectView(R.id.txtRegistration) TextView txtRegistration;
    @InjectView(R.id.editExpire) EditText editExpire;
    @InjectView(R.id.editFilter) EditText editFilter;
    @InjectView(R.id.editMessage) EditText editMessage;
    @InjectView(R.id.editTag) EditText editTag;
    @InjectView(R.id.checkBoxPublishToSelf) CheckBox checkBoxPublishToSelf;
    @InjectView(R.id.txtReceipt) TextView txtReceipt;
    @InjectView(R.id.btnRegister) Button btnRegister;
    @InjectView(R.id.btnUnregister) Button btnUnregister;
    @InjectView(R.id.btnTest) Button btnTest;

    @Inject @ForActivity Context context;
    @Inject SharedPreferences sharedPreferences;
    @Inject Bus bus;
    @Inject GmsEndpoint gmsEndpoint;

    public final static String PROPERTY_SENDER_ID = "sender_id";
    public final static String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "app_version";
    // private static final String PROPERTY_GMS_SUB_ID = "subscription_id";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private String m_strGcmRegId;
    private String m_strGmsSubId;
    private Menu menu;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // Tells the activity to use the fragment's Menu items in onCreateOptionsMenu()
        setHasOptionsMenu(true);
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
        // checkPlayServicesAndGcm();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        this.menu = menu;
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @DebugLog
    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
        checkPlayServicesAndGcm();
    }

    @DebugLog
    @Override
    public void onPause() {
        bus.unregister(this);
        super.onPause();
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
            Timber.w("Google Play Services are not available.");
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
            Timber.d("No saved Registration ID found.");
            return "";
        }
        int registeredVersion = sharedPreferences.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if(registeredVersion!=currentVersion) {
            // Version changed
            Timber.d("Version ID has changed and Registration ID is no longer valid.");
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
            Timber.e(e, "Failed to obtain application version code.");
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private static String getUniqueId(Context context) {
        // TODO: Move this out for injection
        return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
    }

    /**
     * Saves/Clears the GCM Registration ID in SharedPreferences.
     * Synchronized to prevent two threads from somehow simultaneously
     * mucking around with the GCM Registration ID.
     *
     * @param context Application context for retrieving application version code.
     * @param regId GCM Registration ID to store. Empty if clearing persisted data.
     */
    @DebugLog
    private synchronized void storeRegistrationId(Context context, String regId) {
        m_strGcmRegId = regId;
        int appVersion = getAppVersion(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(regId.isEmpty()) {
            Timber.i("Clearing persisted Registration ID.");
            btnRegister.setEnabled(true);
            editor.remove(PROPERTY_REG_ID);
            editor.remove(PROPERTY_APP_VERSION);
        } else {
            Timber.i("Saving new Registration ID: " + regId);
            btnUnregister.setEnabled(true);
            editor.putString(PROPERTY_REG_ID, regId);
            editor.putString(PROPERTY_SENDER_ID, getSenderId());
            editor.putInt(PROPERTY_APP_VERSION, appVersion);
        }
        // apply() tells the editor to perform the save asynchronously.
        editor.apply();
    }

    @DebugLog
    private void checkPlayServicesAndGcm() {
        if(checkPlayServices()){
            m_strGcmRegId = getRegistrationId(context);
            if(!m_strGcmRegId.isEmpty()) {
                btnTest.setEnabled(true);
                btnUnregister.setEnabled(true);
                txtRegistration.setText(m_strGcmRegId);
            } else {
                btnRegister.setEnabled(true);
            }
            editSenderId.setText(sharedPreferences.getString(PROPERTY_SENDER_ID, ""));
        }
    }

    private boolean checkHostAndPortSet() {
        if(!gmsEndpoint.isUrlSet()) {
            // Lazy approach to getting the user to the settings page
            Timber.w("Attempted to use GMS Notification API before configuring Host and Port settings.");
            getActivity().onOptionsItemSelected(this.menu.findItem(R.id.action_settings));
            Toast.makeText(context, "Warning: Set GMS settings first!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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
        Timber.i("Register button was clicked!");
        btnRegister.setEnabled(false);
        bus.post(new GcmRegisterEvent(getSenderId()));
    }
    @OnClick(R.id.btnUnregister)
    public void handleUnregister() {
        Timber.i("Unregister button was clicked!");
        btnUnregister.setEnabled(false);
        bus.post(new GcmUnregisterEvent());
    }
    @OnClick(R.id.btnTest)
    public void handleTest() {
        Timber.i("Test button was clicked!");
        Bundle data = new Bundle();
        data.putString("my_message", "Hello world!");
        bus.post(new GcmSendEvent(getSenderId(), data));
    }
    @OnClick(R.id.btnSubscribe)
    public void handleSubscribe() {
        if(checkHostAndPortSet() == false)
        {
            Timber.i("Subscribe button was clicked but Host and Port were not configured.");
            return;
        }
        if(m_strGcmRegId.isEmpty()){
            Timber.i("Subscribe button was clicked but GCM is not registered.");
            Toast.makeText(context, "Warning: Register GCM first!", Toast.LENGTH_SHORT).show();
            return;
        }
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
        Timber.i("Subscribe button was clicked and request is posted: " + notificationSubscription.toString());
        bus.post(new NotificationSubscribeEvent(identifier, notificationSubscription));
    }
    @OnClick(R.id.btnCancel)
    public void handleCancel() {
        if(checkHostAndPortSet() == false)
        {
            Timber.i("Cancel button was clicked but Host and Port were not configured.");
            return;
        }
        Timber.i("Cancel button was clicked for Subscription ID: " + m_strGmsSubId);
        bus.post(new NotificationDeleteEvent(m_strGmsSubId));
    }
    @OnClick(R.id.btnPublish)
    public void handlePublish() {
        if(checkHostAndPortSet() == false)
        {
            Timber.i("Publish button was clicked but Host and Port were not configured.");
            return;
        }
        NotificationEvent notificationEvent;
        if(checkBoxPublishToSelf.isChecked()) {
            notificationEvent = new NotificationEvent(
                    getMessage(),
                    getMessageTag(),
                    NotificationEvent.MediaType.STRING,
                    m_strGcmRegId,
                    NotificationDetails.ClientType.GCM,
                    null
            );
            Timber.i("Publish button was clicked with self as target: " + notificationEvent.toString());
        } else {
            notificationEvent = new NotificationEvent(
                    getMessage(),
                    getMessageTag(),
                    NotificationEvent.MediaType.STRING
            );
            Timber.i("Publish button was clicked for broadcast: " + notificationEvent.toString());
        }
        bus.post(new NotificationPublishEvent(notificationEvent));
    }
    @TargetApi(11)
    @OnClick(R.id.txtRegistration)
    public void handleRegistrationTouch() {
        try {
            Timber.i("Copying Registration ID to clipboard.");
            int sdk = Build.VERSION.SDK_INT;
            if (sdk < Build.VERSION_CODES.HONEYCOMB) {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
                clipboard.setText(txtRegistration.getText().toString());
            } else {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Registration ID", txtRegistration.getText().toString());
                clipboard.setPrimaryClip(clip);
            }
            Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Timber.e(e, "Encountered unknown exception while copying Registration ID.");
        }
    }
    /** END BUTTON HANDLERS **/

    @Subscribe public void onGcmRegisterDone(GcmRegisterDoneEvent event) {
        storeRegistrationId(context, event.registrationId);
        txtRegistration.setText(event.registrationId);
        Toast.makeText(context, "Registered (id:" + event.registrationId + ")", Toast.LENGTH_SHORT).show();
    }
    @Subscribe public void onGcmSendDone(GcmSendDoneEvent event) {
        Toast.makeText(context, "Upstream message delivered.", Toast.LENGTH_SHORT).show();
    }
    @Subscribe public void onGcmUnregisterDone(GcmUnregisterDoneEvent event) {
        storeRegistrationId(context, "");
        txtRegistration.setText(getResources().getString(R.string.not_registered));
        Toast.makeText(context, "Unregistered", Toast.LENGTH_SHORT).show();
    }
    @Subscribe public void onGcmError(GcmErrorEvent event) {
        // Reset the buttons just in case
        btnRegister.setEnabled(true);
        btnUnregister.setEnabled(true);
        Toast.makeText(context, "GCM Error: " + event.error.getMessage(), Toast.LENGTH_SHORT).show();
    }
    @Subscribe public void gcmReceived(GcmReceiveEvent event) {
        if(event.extras != null) {
            txtReceipt.setText(event.extras.toString());
        } else {
            txtReceipt.setText(event.toString());
        }
        if(!event.isProduced()) {
            Toast.makeText(context, "Message received!", Toast.LENGTH_SHORT).show();
        }
        Timber.d("Received a GCM message: " + event.toString());
    }

    @Subscribe public void onNotificationSubscribeDone(NotificationSubscribeDoneEvent event) {
        m_strGmsSubId = event.subscriptionResponse.getId();
        Toast.makeText(context, "Subscribed (id:" + event.subscriptionResponse.getId() + ")", Toast.LENGTH_SHORT).show();
    }
    @Subscribe public void onNotificationDeleteDone(NotificationDeleteDoneEvent event) {
        m_strGmsSubId = "";
        Toast.makeText(context, "Subscription deleted.", Toast.LENGTH_SHORT).show();
    }
    @Subscribe public void onNotificationPublishDone(NotificationPublishDoneEvent event) {
        Toast.makeText(context, "Message published.", Toast.LENGTH_SHORT).show();
    }
    @Subscribe public void onNotificationError(NotificationErrorEvent event) {
        Toast.makeText(context, "Notification API Error: " + event.error.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
