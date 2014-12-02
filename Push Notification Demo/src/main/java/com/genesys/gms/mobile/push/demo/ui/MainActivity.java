package com.genesys.gms.mobile.push.demo.ui;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import com.genesys.gms.mobile.push.demo.BaseActivity;
import com.genesys.gms.mobile.push.demo.R;
import com.genesys.gms.mobile.push.demo.data.push.GcmIntentService;
import com.squareup.otto.Bus;
import timber.log.Timber;

import javax.inject.Inject;

/**
 * The main application activity is only responsible for inflating fragments
 * and handling Option Menu selections.
 */
public class MainActivity extends BaseActivity {
    @Inject Bus bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            // Determine if activity was started as a result of GCM notification
            int notificationId = extras.getInt(GcmIntentService.GCM_NOTIFICATION_ID, -1);
            if(notificationId != -1) {
                Timber.i("Clearing notification with ID %d from drawer.", notificationId);
                // Remove the notification from the Notification Drawer
                NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(notificationId);
            }
        }

        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MainFragment())
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    // TODO: Restore in onCreate
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Timber.i("Switching over to Settings page.");
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
            transaction.replace(R.id.container, new SettingsFragment());
            transaction.addToBackStack(null);
            transaction.commit();
            return true;
        } else if (id == R.id.action_done) {
            Timber.i("Leaving Settings page.");
            getSupportFragmentManager().popBackStack();
        }

        return super.onOptionsItemSelected(item);
    }
}
