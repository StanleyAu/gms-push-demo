package com.genesys.gms.mobile.push.demo.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.genesys.gms.mobile.push.demo.BaseFragment;
import com.genesys.gms.mobile.push.demo.ForApplication;
import com.genesys.gms.mobile.push.demo.R;
import com.genesys.gms.mobile.push.demo.data.retrofit.GmsEndpoint;
import com.squareup.otto.Bus;

import javax.inject.Inject;

/**
 * Created by stau on 11/27/2014.
 */
public class SettingsFragment extends BaseFragment {
    @InjectView(R.id.editHost) EditText editHost;
    @InjectView(R.id.editPort) EditText editPort;
    @InjectView(R.id.editApiVersion) EditText editApiVersion;

    @Inject GmsEndpoint gmsEndpoint;
    @Inject @ForApplication Context context;
    @Inject SharedPreferences sharedPreferences;

    public static final String PROPERTY_HOST = "endpoint_host";
    public static final String PROPERTY_PORT = "endpoint_port";
    public static final String PROPERTY_API_VERSION = "api_version";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.inject(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle inState) {
        super.onActivityCreated(inState);
        editHost.setText(sharedPreferences.getString(PROPERTY_HOST, ""));
        editPort.setText(sharedPreferences.getString(PROPERTY_PORT, ""));
        editApiVersion.setText(Integer.toString(sharedPreferences.getInt(PROPERTY_API_VERSION, 1)));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_settings, menu);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick(R.id.btnSave)
    public void handleSave() {
        String host = editHost.getText().toString().trim();
        String port = editPort.getText().toString().trim();
        String strApiVersion = editApiVersion.getText().toString().trim();
        if(host.isEmpty()||port.isEmpty()||strApiVersion.isEmpty()) {
            // Invalid, should notify
            return;
        }
        int version;
        try {
            version = Integer.parseInt(strApiVersion);
        } catch (NumberFormatException e) {
            // Invalid API version
            return;
        }
        gmsEndpoint.setUrl(host, port, version);
        // TODO: Notify activity to change fragments
        storeHostSettings(host, port, version);
        Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
    }

    private void storeHostSettings(String host, String port, int version) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PROPERTY_HOST, host);
        editor.putString(PROPERTY_PORT, port);
        editor.putInt(PROPERTY_API_VERSION, version);
        editor.apply();
    }
}
