package group1.tcss450.uw.edu.a450groupone;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import group1.tcss450.uw.edu.a450groupone.utils.SendPostAsyncTask;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MessageIntentService extends IntentService {

    private static final int POLL_INTERVAL = 60_000;
    private static final String TAG = "MessageIntentService";
    private int mMemberId;

    private SharedPreferences prefs;

    public MessageIntentService() {
        super("MessageIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

        }
    }

    // creates the AlarmManager instance to start service.
    public static void startServiceAlarm(Context context, boolean isInForeground) {
        Log.d(TAG, "start() - messageIntentService");
        Intent i = new Intent(context, MyIntentService.class);
        i.putExtra(context.getString(R.string.keys_is_foreground), isInForeground);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, i, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        int startAfter = isInForeground ? POLL_INTERVAL : POLL_INTERVAL * 2;

        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP
                , startAfter
                , POLL_INTERVAL, pendingIntent);
    }

    // creates the AlarmManager instance to stop service.
    public static void stopServiceAlarm(Context context) {
        Log.d(TAG, "stop" +
                "() - messageIntentService");
        Intent i = new Intent(context, MyIntentService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, i, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    /**
     * method to get all chatrooms for user and start building chatroom buttons
     *
     * author: Casey Anderson
     */
    private void getChats ( View v ) throws JSONException {
        prefs = getSharedPreferences(
                getString(R.string.keys_shared_prefs),
                Context.MODE_PRIVATE);

        if (!prefs.contains(getString(R.string.keys_prefs_username))) {
            throw new IllegalStateException("No username in prefs!");
        }

        mMemberId = prefs.getInt(getString(R.string.keys_prefs_id), 0);
        Uri retrieve = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_get_chatMembers))
                .build();

        JSONObject body = new JSONObject();

        // provide current user id
        try {
            body.put("memberId", mMemberId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SendPostAsyncTask.Builder(retrieve.toString(), body)
                .onPostExecute(this::populateChats)
                //TODO: add onCancelled handler.
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

}
