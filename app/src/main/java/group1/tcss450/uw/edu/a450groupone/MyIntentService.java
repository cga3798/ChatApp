package group1.tcss450.uw.edu.a450groupone;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import group1.tcss450.uw.edu.a450groupone.utils.SendPostAsyncTask;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * @author Kamalpreet Singh
 */
public class MyIntentService extends IntentService {

    public static final String RECEIVED_UPDATE = "new show from phish.net!";

    //60 seconds - 1 minute is the minimum...
    private static final int POLL_INTERVAL = 60_000;

    private static final String TAG = "MyIntentService";

    private int initialState = 0;
    private int currentlState = 0;
    private int count = 0;
    private String userToDisplay = "empty";
    public boolean newRequest = false;
    private SharedPreferences prefs;
    private int mMemberId;

    public MyIntentService() {
        super("MyIntentService");
        Log.d(TAG, "creating service");

    }

    @Override
    protected void onHandleIntent(Intent intent) {
//        count = 0;
        if (intent != null) {
            Log.d(TAG, "Performing the service");
            checkIfToPostNotification(intent.getBooleanExtra(getString(R.string.keys_is_foreground), false));
            getChats();
        }
        prefs = getSharedPreferences(
                getString(R.string.keys_shared_prefs),
                Context.MODE_PRIVATE);
    }


    // creates the AlarmManager instance to start service.
    public static void startServiceAlarm(Context context, boolean isInForeground) {
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
        Intent i = new Intent(context, MyIntentService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, i, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    private boolean checkIfToPostNotification(boolean isInForeground) {
        // app in background, check webservice
        if (!isInForeground) {
            Log.d(TAG, "checkIfToPostNotification() - in background");

            SharedPreferences prefs =
                    this.getSharedPreferences(getString(R.string.keys_shared_prefs),
                            Context.MODE_PRIVATE);
            String memberidA = String.valueOf(prefs.getInt(getString(R.string.keys_prefs_id), -1));
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.ep_base_url))
                    .appendPath(getString(R.string.ep_invites_received))
                    .build();

            JSONObject msg = asJSONObject(memberidA, null);

            new SendPostAsyncTask.Builder(uri.toString(), msg)
                    .onPostExecute(this::handleReceivedInviteOnPost)
                    .build().execute();
        } else { // don't need to do anything because app is in foreground
            Log.d(TAG, "checkIfToPostNotification() - in foreground");
        }
        return true;
    }

    private void handleReceivedInviteOnPost(String result) {
        try {
            JSONObject response = new JSONObject(result);
            Boolean success = response.getBoolean("success");

            if (success) {
                JSONArray requestsSent = response.getJSONArray("received");
                int length = requestsSent.length();

                // update current state (how many request there are now vs. initially)
                currentlState = length;

                // store the state when app is in foreground
                if (count == 0) {
                    initialState = length;
//                    Log.d(TAG, "initial state: " + initialState + ", current: " + requestsSent.length());
                }
                count++;
                Log.d(TAG, "count: " + count + ", initial state: " + initialState + ", current: " + requestsSent.length());

                if (length == 0) {
                    Log.d(TAG, "length is 0");

                } else {
                    JSONObject request2 = requestsSent.getJSONObject(requestsSent.length() - 1);

                    userToDisplay = request2.getString("username");

//                    for (int i = 0; i < requestsSent.length(); i++) {
//                        JSONObject request = requestsSent.getJSONObject(i);
//                        Log.d(TAG,  " user is: " + userToDisplay);
//                        userToDisplay = request.getString("username");
//                    }
                }

            } else {
                newRequest = false;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // this needs to be here because we need to wait until we've received user data
        buildNotification();

    }

    private JSONObject asJSONObject(String memberidA, String memberidB) {
        //build the JSONObject
        JSONObject msg = new JSONObject();
        try {
            msg.put("memberidA", memberidA);
            msg.put("memberidB", memberidB);
        } catch (JSONException e) {
            Log.wtf("QUERY ", "Error creating JSON: " + e.getMessage());
        }
        return msg;
    }

    private void buildNotification() {
        // only build notification if something request in background was received

        if (initialState == currentlState && !userToDisplay.equals("empty")) {

            Log.d(TAG, "buildNotification() - " + userToDisplay);
            //IMPORT V4 not V7
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setContentTitle("Connection Request")
                            .setSmallIcon(R.mipmap.chatapplogo)
                            .setContentTitle("New Request")
                            .setContentText(userToDisplay + " sent you a request.");

            // Creates an Intent for the Activity
            Intent notifyIntent =
                    new Intent(this, NavigationActivity.class);
            notifyIntent.putExtra("friendFragment", "FriendFragment");

            // Sets the Activity to start in a new, empty task
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            // Creates the PendingIntent
            PendingIntent notifyPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            notifyIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            // Puts the PendingIntent into the notification builder
            mBuilder.setContentIntent(notifyPendingIntent);
            mBuilder.setAutoCancel(true);

            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // mId allows you to update the notification later on.
            mNotificationManager.notify(1, mBuilder.build());

            Intent RTReturn = new Intent(NavigationActivity.RECEIVE_JSON);
            RTReturn.putExtra("json", "jsonString");
            LocalBroadcastManager.getInstance(this).sendBroadcast(RTReturn);

        } else {
            Log.d(TAG, "buildNotification() - nothing new");

        }
    }


    /**
     * method to get all chatrooms for user and start building chatroom buttons
     *
     * author: Casey Anderson
     */
    private void getChats() {
        Log.d(TAG, "inside getChats");
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
                .onPostExecute(this::getChatIds)
                //TODO: add onCancelled handler.
//                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    private void getChatIds(String res) {
        Log.d(TAG, "result is : " + res);
        if (!prefs.contains(getString(R.string.keys_prefs_username))) {
            throw new IllegalStateException("No username in prefs!");
        }

        try {
            JSONObject response = new JSONObject(res);
            if (response.getBoolean("success")) {
                JSONArray chatList = response.getJSONArray("name");
                Log.d(TAG, "chatID are : " + chatList.toString());

                for (int i = 0; i < chatList.length(); i++) {
                    // do something with chatIDs
                    JSONObject currentChat = chatList.getJSONObject(i);
                    Log.d(TAG, "\nchatid is -> = " + currentChat.getInt("chatid"));
                    //Log.d(TAG, "name are : " + name.toString());

                    // get last message of the chatID
                    getLastMessage(currentChat.getInt("chatid"));


                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getLastMessage(int chatID) throws JSONException {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_get_last_message))
                .build();

        JSONObject body = new JSONObject();

        Log.d(TAG, "ChatID in sharedprefs: " + R.string.keys_prefs_chatId);
        // provide current chat id and a timestamp to get all messages
        //body.put("chatId", prefs.getInt("chatId", R.string.keys_prefs_chatId));
        body.put("chatId", chatID);
        body.put("after", "1970-01-01 00:00:00.000000");

        new SendPostAsyncTask.Builder(uri.toString(), body)
                .onPostExecute(this::populateChatText)
                //TODO: add onCancelled handler.
                //.onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    private void populateChatText(String res) {
        String text = "";
        try {
            JSONObject response = new JSONObject(res);
            if (response.getBoolean("success")) {
                JSONObject message = response.getJSONObject("messages");
                Log.d(TAG, "Last message: " + message.getString("message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isThereNewRequest() {
        return newRequest;
    }
}