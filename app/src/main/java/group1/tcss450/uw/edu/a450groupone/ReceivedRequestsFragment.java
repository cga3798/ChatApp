package group1.tcss450.uw.edu.a450groupone;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import es.dmoral.toasty.Toasty;
import group1.tcss450.uw.edu.a450groupone.utils.SendPostAsyncTask;


/**
 * A fragment that will handle requests and all the operations related to it.
 *
 */
public class ReceivedRequestsFragment extends Fragment {

    String memberidA;
    LinearLayout receivedInvitesContainer;

    public ReceivedRequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_received_requests, container, false);
        receivedInvites();
        return v;
    }

    /**
     * Send a request to the server to retrieve a list of
     * received invites.
     *
     */
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

    /**
     * Parse the response from the server and display it on the
     * screen.
     * @param result response from server.
     */
    private void handleReceivedInviteOnPost(String result) {
        receivedInvitesContainer = (LinearLayout) getActivity().findViewById(R.id.receivedRequestLinearLayout);

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

    /**
     * Create the text view for the each connection and add it to the list
     * of conncention.
     * @param nickname nickname of the connection
     * @param fullName full name of the connection
     * @param length the number of connections a user have.
     *
     * @return A view to display the list of received connections.
     */
    private View getReceivedView(String nickname, String fullName, int length) {
        View v;
        if ( length == 0) {
            v = LayoutInflater.from(getContext())
                    .inflate(R.layout.request_row, null, false);
            TextView tv = (TextView) v.findViewById(R.id.requestTextViewNickname);
            tv.setText(nickname);
            tv = (TextView) v.findViewById(R.id.requestTextViewFullName);
            tv.setText(fullName);


        } else {
            v = LayoutInflater.from(getContext())
                    .inflate(R.layout.received_request_row, null, false);

            TextView tvUsername = (TextView) v.findViewById(R.id.receivedRequestLinearLayoutTextViewNickname);
            tvUsername.setText(nickname);
            TextView tv = (TextView) v.findViewById(R.id.receivedRequestLinearLayoutTextViewFullName);
            tv.setText(fullName);

            ImageView im = (ImageView) v.findViewById(R.id.receivedRequestAccept);
            im.setOnClickListener(view -> onAcceptInvite(v, tvUsername.getText().toString()));

            im = (ImageView) v.findViewById(R.id.receivedRequestDecline);
            im.setOnClickListener(view -> onDeclineInvite(v, tvUsername.getText().toString()));
        }

        return v;
    }

    /**
     * Handles when current user press on accept invitation.
     *
     * @param v current view.
     * @param username_b username of the person who sent an invite.
     */
    private void onAcceptInvite(View v, String username_b) {
        Log.e("onAcceptInvite: ", username_b);

        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_connections_ops))
                .build();

        JSONObject searchJSON = asJSONObject(memberidA, username_b, "accept");

        new SendPostAsyncTask.Builder(uri.toString(), searchJSON)
                .onPostExecute(this::handleAcceptOnPost)
                //TODO: add onCancelled handler.
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    /**
     * Updates the current fragment to display the
     * new list of of received requests.
     *
     * @param result
     */
    private void handleAcceptOnPost(String result) {
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        ft.detach(ReceivedRequestsFragment.this).attach(ReceivedRequestsFragment.this).commit();
        try {
            JSONObject response = new JSONObject(result);
            String username = response.getString("names");
            try {
                View views;
                for (int i = 0; i < receivedInvitesContainer.getChildCount(); i++) {
                    views = receivedInvitesContainer.getChildAt(i);
                    if (views instanceof android.support.constraint.ConstraintLayout) {
                        android.support.constraint.ConstraintLayout cl =
                                (android.support.constraint.ConstraintLayout) views.findViewById(R.id.receivedRequestRow);
                        TextView tv = (TextView) cl.findViewById(R.id.receivedRequestLinearLayoutTextViewNickname);
                        if (tv.getText().toString().equals(username)) {
                            Log.e("ELSE ", "IN IT " + i);
                            receivedInvitesContainer.removeView(views);
                            Toasty.normal(getActivity(), "Invitation Accepted.", Toast.LENGTH_SHORT).show();
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
     * Handles when the user chooses to decline an invitation.
     * Sends a request to the server.
     *
     * @param v current view
     * @param username_b username of the connection who sent the request
     */
    private void onDeclineInvite(View v, String username_b) {
        Log.e("onDeclineInvite: ", username_b);

        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_connections_ops))

                .build();

        JSONObject searchJSON = asJSONObject(memberidA, username_b, "decline");

        new SendPostAsyncTask.Builder(uri.toString(), searchJSON)
                .onPostExecute(this::handleDeclineOnPost)
                //TODO: add onCancelled handler.
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    /**
     * Updates the fragment to display the list without the
     * connection that declines the request.
     *
     * @param result the response from the server.
     */
    private void handleDeclineOnPost(String result) {
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        ft.detach(ReceivedRequestsFragment.this).attach(ReceivedRequestsFragment.this).commit();
        try {
            JSONObject response = new JSONObject(result);
            String username = response.getString("names");
            try {
                View views;
                for (int i = 0; i < receivedInvitesContainer.getChildCount(); i++) {
                    views = receivedInvitesContainer.getChildAt(i);
                    if (views instanceof android.support.constraint.ConstraintLayout) {
                        android.support.constraint.ConstraintLayout cl =
                                (android.support.constraint.ConstraintLayout) views.findViewById(R.id.receivedRequestRow);
                        TextView tv = (TextView) cl.findViewById(R.id.receivedRequestLinearLayoutTextViewNickname);
                        if (tv.getText().toString().equals(username)) {
                            Log.e("ELSE ", "IN IT " + i);
                            receivedInvitesContainer.removeView(views);
                            Toasty.normal(getActivity(), "Invitation Declined.", Toast.LENGTH_SHORT).show();
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
     * Handles when the request to the server fails.
     *
     * @param result the error that occurred.
     */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
    }


    /**
     * Create the JSONObject that will be sent to the server and contains
     * the following parameters:
     *
     * @param memberidA the current member of the
     * @param username_b the username of the connection that sent the request.
     * @param op the operation that we want to preform
     * @return a JSON object with the parameters passed in.
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

    /**
     * Create a JSONObject that will be sent to server and
     * contains the following parameters:
     *
     * @param memberidA member id of the current app user.
     * @param memberidB member id of the request sender.
     *
     * @return A JSONObject with the above parameters.
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

}