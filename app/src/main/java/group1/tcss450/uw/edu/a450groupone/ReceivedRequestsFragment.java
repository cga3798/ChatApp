package group1.tcss450.uw.edu.a450groupone;


import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import group1.tcss450.uw.edu.a450groupone.utils.SendPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReceivedRequestsFragment extends Fragment {

    String memberidA;
    public ReceivedRequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        receivedInvites();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_received_requests, container, false);
    }

    private void receivedInvites() {

        SharedPreferences prefs =
                this.getActivity().getSharedPreferences(getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        memberidA =  String.valueOf(prefs.getInt(getString(R.string.keys_prefs_id), -1));

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
//        Log.e("Received invite: ", result);
        LinearLayout receivedInvitesContainer = getActivity().findViewById(R.id.receivedRequestLinearLayout);

        try {
            JSONObject response = new JSONObject(result);
            Boolean success = response.getBoolean("success");

            if (success) {
                JSONArray requestsSent = response.getJSONArray("received");
                int length = requestsSent.length();
                if (length == 0) {
                    receivedInvitesContainer.addView(
                            getReceivedView("", "There are no received invites", length));
                } else {
                    for (int i = 0; i < requestsSent.length(); i++) {
                        JSONObject request = requestsSent.getJSONObject(i);
                        receivedInvitesContainer.addView(
                                getReceivedView(request.getString("username"),
                                        request.getString("firstname")
                                                + " " + request.getString("lastname"), length));
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private View getReceivedView(String nickname, String fullName, int length) {
        View v;
        if ( length == 0) {
            v = LayoutInflater.from(getContext())
                    .inflate(R.layout.contact_row, null, false);
            TextView tv = v.findViewById(R.id.friendsTextViewNickname);
            tv.setText(nickname);
            tv = v.findViewById(R.id.friendsTextViewFullName);
            tv.setText(fullName);

        } else {
            v = LayoutInflater.from(getContext())
                    .inflate(R.layout.received_request_row, null, false);

            TextView tv = v.findViewById(R.id.receivedRequestLinearLayoutTextViewNickname);
            tv.setText(nickname);
            tv = v.findViewById(R.id.receivedRequestLinearLayoutTextViewFullName);
            tv.setText(fullName);

            ImageView im = (ImageView) v.findViewById(R.id.receivedRequestAccept);
            im.setOnClickListener(view -> onAcceptInvite(v));

            im = (ImageView) v.findViewById(R.id.receivedRequestDecline);
            im.setOnClickListener(view -> onDeclineInvite(v));
        }

        return v;
    }

    private void onAcceptInvite(View v) {
        Log.e("onAcceptInvite: ", "Clicked");
    }

    private void onDeclineInvite(View v) {
        Log.e("onDeclineInvite: ", "Clicked");
    }

    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
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

}
