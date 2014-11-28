package com.genesys.gms.mobile.push.demo.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    @Inject Bus bus;
    @Inject GmsEndpoint gmsEndpoint;
    @Inject @ForApplication Context context;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.inject(this, view);

        return view;
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
        int version = Integer.MIN_VALUE;
        try {
            version = Integer.parseInt(strApiVersion);
        } catch (NumberFormatException e) {
            // Invalid API version
            return;
        }
        gmsEndpoint.setUrl(host, port, version);
        // TODO: Notify activity to change fragments
        // TODO: Persist configuration in SharedPreferences
        Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
    }
}
