package group1.tcss450.uw.edu.a450groupone.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import group1.tcss450.uw.edu.a450groupone.R;

/**
 * This class is used to communicate, A broadcast receiver can be used to 
 * listen to different things like rebooting, battery down, etc. We are
 * configuring a receiver to listen to rebooting and to start the service
 * automatically on restart.
 */
public class BootUpReceiver extends BroadcastReceiver {

    private static final String TAG = "BootUpReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");

        SharedPreferences sharedPreferences =
                context.getSharedPreferences(
                        context.getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean(context.getString(
                R.string.keys_sp_on), false)) {
            Log.d(TAG, "starting service");
        }
        else {
            Log.d(TAG, "Did NOT start the service");
        }
    }


    public BootUpReceiver() {
    }

}
