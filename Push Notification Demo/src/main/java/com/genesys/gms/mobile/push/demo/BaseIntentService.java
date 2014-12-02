package com.genesys.gms.mobile.push.demo;

import android.app.IntentService;
import dagger.ObjectGraph;
import hugo.weaving.DebugLog;

/**
 * Created by stau on 11/27/2014.
 */
public abstract class BaseIntentService extends IntentService {
    private ObjectGraph intentServiceGraph;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public BaseIntentService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        App application = (App)getApplication();
        intentServiceGraph = application.getApplicationGraph();
        intentServiceGraph.inject(this);
    }
}
