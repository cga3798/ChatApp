package group1.tcss450.uw.edu.a450groupone;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import group1.tcss450.uw.edu.a450groupone.utils.SendPostAsyncTask;


public class ConnectionRequestsFragment extends Fragment {

    private String memberidA;

    public ConnectionRequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_connection_requests, container, false);
        SharedPreferences prefs =
                this.getActivity().getSharedPreferences(getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        memberidA =  String.valueOf(prefs.getInt(getString(R.string.keys_prefs_id), -1));

        if (getArguments() != null) {
            if (getArguments().getBoolean("invite"))  {
                String memberidB = getArguments().getString("memberidB");
                onInvite(memberidB);
            }
        } else {
            Log.e("getArguments: ", "NULL");
        }

        sentInvites();
        receivedInvites();
        return v;
    }


    public void onInvite(String memberidB) {
        //current user -> sender:A receiver ->  B

        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_invite_new_friend))
                .build();

        JSONObject searchJSON = asJSONObject(memberidA, memberidB);
        new SendPostAsyncTask.Builder(uri.toString(), searchJSON)
                .onPostExecute(this::handleInviteOnPost)
                //TODO: add onCancelled handler.
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    private void handleInviteOnPost(String result) {
        Log.d("invite result", result);
        String fullname = getArguments().getString("fullname");

        Map<Integer, String> messages = new HashMap<Integer, String>();
        messages.put(1, "An invitation to " + fullname + " has been sent!");
        messages.put(2, "You and " + fullname + " are already friends. Please check your friend list");
        messages.put(3, fullname + " has already sent you an invitation. Please check your invitations");
        messages.put(4, "You cannot add yourself as a friend"); // TODO: change this so you don't appear in search.
        messages.put(5, "You have already sent an invitation to " + fullname + ".");

        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");
            if (success) {
                int msg = resultsJSON.getInt("message");
                if (msg == 1) {
                    for (int i=0; i < 1; i++)
                        Toasty.success(getActivity(), messages.get(1), Toast.LENGTH_LONG, true).show();
                } else {
                    for (int i=0; i < 1; i++)
                        Toasty.info(getActivity(), messages.get(msg), Toast.LENGTH_LONG, true).show();
                }
            } else {
                // Not success.
            }
        } catch (JSONException e) {
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
        }
    }


    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
    }

    public JSONObject asJSONObject(String memberidA, String memberidB) {
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

    private void sentInvites() {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_invites_sent))
                .build();

        JSONObject msg = asJSONObject(memberidA, null);

        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleSentInviteOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    private void handleSentInviteOnPost(String result) {
        Log.e("Sent invite: ", result);
    }

    private void receivedInvites() {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_invites_received))
                .build();

        JSONObject msg = asJSONObject(memberidA, null);

        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleReceivedInviteOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    private void handleReceivedInviteOnPost(String result) {
        Log.e("Received invite: ", result);

    }

}
