package com.genesys.gms.mobile.push.demo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import com.genesys.gms.mobile.push.demo.ui.UiModule;
import dagger.ObjectGraph;

import java.util.Arrays;
import java.util.List;

/**
 * Created by stau on 11/27/2014.
 */
public class BaseActivity extends ActionBarActivity {
    private ObjectGraph activityGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App application = (App)getApplication();
        activityGraph = application.getApplicationGraph().plus(getModules().toArray());
        activityGraph.inject(this);
    }

    @Override
    protected void onDestroy() {
        activityGraph = null;
        super.onDestroy();
    }

    protected List<Object> getModules() {
        return Arrays.<Object>asList(new UiModule(this));
    }

    public void inject(Object object) {
        activityGraph.inject(object);
    }
}
