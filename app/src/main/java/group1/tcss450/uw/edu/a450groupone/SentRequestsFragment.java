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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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


    private void sentInvites() {
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

    private void handleSentInviteOnPost(String result) {
        sentInvitesContainer = getActivity().findViewById(R.id.sentRequestLinearLayout);

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


    private View getSentView(String nickname, String fullName, int length) {
        View v;

        if (length == 0 ) {
            v = LayoutInflater.from(getContext())
                    .inflate(R.layout.request_row, null, false);
            TextView tv = v.findViewById(R.id.requestTextViewNickname);
            tv.setText(nickname);
            tv = v.findViewById(R.id.requestTextViewFullName);
            tv.setText(fullName);
        } else {
            v = LayoutInflater.from(getContext())
                    .inflate(R.layout.sent_request_row, null, false);

            TextView tvUsername = v.findViewById(R.id.sentRequestLinearLayoutTextViewNickname);
            tvUsername.setText(nickname);
            TextView tv = v.findViewById(R.id.sentRequestLinearLayoutTextViewFullName);
            tv.setText(fullName);

            Button b  = (Button) v.findViewById(R.id.sentRequestCancelInvite);
            b.setOnClickListener(view -> onCancelInvite(v, tvUsername.getText().toString()));
        }

        return v;
    }

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


    private void handleCancelOnPost(String result) {
        try {
            JSONObject response = new JSONObject(result);
            String username = response.getString("names");
            try {
                View views;
                for (int i = 0; i < sentInvitesContainer.getChildCount() - 1; i++) {
                       views = sentInvitesContainer.getChildAt(i);
                       TextView tv = views.findViewById(R.id.sentRequestLinearLayoutTextViewNickname);
                       String tvUsername = tv.getText().toString();
                       if (tvUsername.equals(username)) {
                           sentInvitesContainer.removeView(views);
                       }
               }
            } catch (NullPointerException e) {
                Log.e("handleCancelOnPost: ", "NullPointerException");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
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
