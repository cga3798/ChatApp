package group1.tcss450.uw.edu.a450groupone;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import es.dmoral.toasty.Toasty;
import group1.tcss450.uw.edu.a450groupone.utils.SendPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class SentRequestsFragment extends Fragment {

    String memberidA;
    LinearLayout sentInvitesContainer;

    public SentRequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sent_requests, container, false);
        sentInvites();
        return v;
    }


    /**
     * Make a call to the server to get all the invites that were sent.
     */
    private void sentInvites() {
        // current user's member id is needed to send the request
        // to the server.
        SharedPreferences prefs =
                this.getActivity().getSharedPreferences(getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        memberidA =  String.valueOf(prefs.getInt(getString(R.string.keys_prefs_id), -1));

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

    /**
     * Handle the response we get back from the server.
     * @param result string response from the seerver.
     */
    private void handleSentInviteOnPost(String result) {
        sentInvitesContainer = (LinearLayout) getActivity().findViewById(R.id.sentRequestLinearLayout);

        try {
            JSONObject response = new JSONObject(result);
            Boolean success = response.getBoolean("success");

            if (success) {
                JSONArray requestsSent = response.getJSONArray("sent");
                int length = requestsSent.length();
                if (length == 0) {
                    sentInvitesContainer.addView(
                            getSentView( "", "There are no sent invites", length));
                } else {
                    for (int i = 0; i < requestsSent.length(); i++) {
                        JSONObject request = requestsSent.getJSONObject(i);
                        // add the views to the linear layout for the fragment.
                        sentInvitesContainer.addView(
                                getSentView(request.getString("username"),
                                        request.getString("firstname")
                                                + " " + request.getString("lastname"), length));
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Create the container for each invite sent.
     * @param nickname username of invite receiver.
     * @param fullName full name of the receiver of the invite.
     * @param length length of the list of sent invites
     * @return a view with the about data.
     */
    private View getSentView(String nickname, String fullName, int length) {
        View v;

        if (length == 0) {
            v = LayoutInflater.from(getContext())
                    .inflate(R.layout.request_row, null, false);
            TextView tv = (TextView) v.findViewById(R.id.requestTextViewNickname);
            tv.setText(nickname);
            tv = (TextView) v.findViewById(R.id.requestTextViewFullName);
            tv.setText(fullName);
        } else {
            v = LayoutInflater.from(getContext())
                    .inflate(R.layout.sent_request_row, null, false);

            TextView tvUsername = (TextView) v.findViewById(R.id.sentRequestLinearLayoutTextViewNickname);
            tvUsername.setText(nickname);
            TextView tv = (TextView) v.findViewById(R.id.sentRequestLinearLayoutTextViewFullName);
            tv.setText(fullName);

            Button b  = (Button) v.findViewById(R.id.sentRequestCancelInvite);
            b.setOnClickListener(view -> onCancelInvite(v, tvUsername.getText().toString()));
        }

        return v;
    }


    /**
     * Method to handle when the invite sender chooses to cancel the request.
     * Send a request to the server to cancel invite.
     * @param view currnet view.
     * @param username_b the username of the receiver of the invite.
     */
    private void onCancelInvite(View view, String username_b) {

        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_connections_ops))
                .build();

        JSONObject searchJSON = asJSONObject(memberidA, username_b, "cancel");

        new SendPostAsyncTask.Builder(uri.toString(), searchJSON)
                .onPostExecute(this::handleCancelOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();

    }

    /**
     * Handles the resonse from the server by updating the UI and removing
     * the sent invite from the list.
     * @param result string containing the response from the server.
     */
    private void handleCancelOnPost(String result) {
        try {
            JSONObject response = new JSONObject(result);
            String username = response.getString("names");
            try {
                View views;
                for (int i = 0; i < sentInvitesContainer.getChildCount(); i++) {
                       views = sentInvitesContainer.getChildAt(i);
                        if (views instanceof android.support.constraint.ConstraintLayout) {
                            android.support.constraint.ConstraintLayout cl =
                                    (android.support.constraint.ConstraintLayout) views.findViewById(R.id.sentRequestRow);
                            TextView tv = (TextView) cl.findViewById(R.id.sentRequestLinearLayoutTextViewNickname);
                            if (tv.getText().toString().equals(username)) {
                                Log.e("ELSE ", "IN IT " + i);
                                sentInvitesContainer.removeView(views);
                                Toasty.normal(getActivity(), "Invitation Canceled.", Toast.LENGTH_SHORT).show();
                            }
                        }
               }
            } catch (NullPointerException e) {
                Log.e("handleCancelOnPost: ", "NullPointerException");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Hnadle any errors from the request sent.
     * @param result the cause of the error.
     */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
    }

    /**
     * Create JSONObject for sending an invite.
     *
     * @param memberidA sender of the invite
     * @param memberidB receiver of the invite
     * @return a JSONObject with the member ids.
     */
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

    /**
     * Create a JSONObject to send with the request to cancel a sent invite.
     * @param memberidA id of invite sended.
     * @param username_b username of the receiver of the invite.
     * @param op operation we wish to perform
     * @return a JSONObject with the above data.
     */
    private JSONObject asJSONObject(String memberidA, String username_b, String op) {
        //build the JSONObject
        JSONObject msg = new JSONObject();
        try {
            msg.put("memberid_a", memberidA);
            msg.put("username_b", username_b);
            msg.put("op", op);

        } catch (JSONException e) {
            Log.wtf("QUERY ", "Error creating JSON: " + e.getMessage());
        }
        return msg;
    }


}
