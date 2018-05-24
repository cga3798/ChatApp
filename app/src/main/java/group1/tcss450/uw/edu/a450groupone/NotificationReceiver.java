package group1.tcss450.uw.edu.a450groupone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * This class is used to communicate, A broadcast receiver can be used to
 * listen to different things like rebooting, battery down, etc. We are
 * configuring a receiver to listen to rebooting and to start the service
 * automatically on restart.
 */
public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = "NotificationReceiver";


    public NotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");

        if (intent.getAction().equals("new")) {
            Log.e("onReceive: ", "new notification");
        } else {
            Log.e("onReceive: ", "no new notification");
        }
    }
}
