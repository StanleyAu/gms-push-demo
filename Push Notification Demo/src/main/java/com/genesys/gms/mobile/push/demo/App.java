package com.genesys.gms.mobile.push.demo;

import android.app.Application;
import com.genesys.gms.mobile.push.demo.data.api.GcmManager;
import com.genesys.gms.mobile.push.demo.data.api.NotificationServiceManager;
import com.squareup.otto.Bus;
import dagger.ObjectGraph;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

/**
 * Created by stau on 11/27/2014.
 */
public class App extends Application {
    private ObjectGraph applicationGraph;
    @Inject GcmManager gcmManager;
    @Inject NotificationServiceManager notificationServiceManager;
    @Inject Bus bus;

    @Override public void onCreate() {
        super.onCreate();
        applicationGraph = ObjectGraph.create(getModules().toArray());
        applicationGraph.inject(this);

        bus.register(gcmManager);
        bus.register(notificationServiceManager);
    }

    protected List<Object> getModules() {
        return Arrays.<Object>asList(new AppModule(this));
    }

    ObjectGraph getApplicationGraph() {
        return applicationGraph;
    }
}
