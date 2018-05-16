package group1.tcss450.uw.edu.a450groupone;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import es.dmoral.toasty.Toasty;
import group1.tcss450.uw.edu.a450groupone.utils.SendPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendFragment extends Fragment {

    private OnFriendFragmentInteractionListener mListener;
    private String fullname;

    public FriendFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_friend, container, false);

        // make request for contacts
        // TODO: save in shared prefs
        try {
            getContacts(v);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return v;
    }

    private void getContacts ( View v ) throws JSONException {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_contacts))
                .build();

        JSONObject body = new JSONObject();
        // provide current user id
        body.put("userid",
                getActivity().getSharedPreferences(getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE)
                        .getInt(getString(R.string.keys_prefs_id), 0) );


        new SendPostAsyncTask.Builder(uri.toString(), body)
                .onPostExecute(this::populateContacts)
                //TODO: add onCancelled handler.
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    /** Example response of a single contact
     * {
            "success": true,
            "friends": [{
                 "firstname": "fname",
                 "lastname": "lname",
                 "username": "kamal"
                "memberid" : 565
            }]
     }
        TODO: find a way to store contact id, might be used later
     * @param res
     */
    private void populateContacts(String res) {
        LinearLayout contactsListContainer = getActivity().findViewById(R.id.friendsLinearLayoutContactsList);
        Log.d("GOTCONTACTS", res);

        try {
            JSONObject response = new JSONObject(res);
            if (response.getBoolean("success")) {
                JSONArray friendsList = response.getJSONArray("friends");
                int length = friendsList.length();

                if (length == 0) {
                    contactsListContainer.addView(
                            getContactView("", "There are no contacts to display", 0, length));
                } else {
                    for (int i = 0; i < friendsList.length(); i++) {
                        JSONObject friend = friendsList.getJSONObject(i);
                        contactsListContainer.addView(
                                getContactView(friend.getString("username"),
                                        friend.getString("firstname") + " "
                                                + friend.getString("lastname"),
                                                friend.getInt("memberid"), length
                                ));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle errors that may occur during the AsyncTask.
     * @param result the error message provide from the AsyncTask
     */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FriendFragment.OnFriendFragmentInteractionListener) {
            mListener = (FriendFragment.OnFriendFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFriendFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFriendFragmentInteractionListener {
        void onAddNewFriend();
    }

    private View getContactView(String nickname, String fullName, int friendID, int length) {
        View v = LayoutInflater.from(getContext())
                .inflate(R.layout.contact_row, null, false);


        TextView tvUsername = v.findViewById(R.id.friendsTextViewNickname);
        tvUsername.setText(nickname);
        TextView tv = v.findViewById(R.id.friendsTextViewFullName);
        tv.setText(fullName);

        if (length != 0) {
            fullname = tv.getText().toString();
            // long click listener to delete contact
            v.setOnLongClickListener(view -> onShowDeleteButton(v, tvUsername.getText().toString()));

            // clicklistenrr to start chat with contact
            v.setOnClickListener(view -> startChat(friendID, fullName) );
        }

        return v;
    }

    // TODO: we can change to use fragment listener
    private void startChat(int theFriendId, String theFullName) {
        Intent intent = new Intent(getContext(), ChatActivity.class);
        //send friend id to chat activity
        intent.putExtra(getString(R.string.keys_open_chat_source), R.id.fragmentFriend);
        intent.putExtra(getString(R.string.keys_friend_id), theFriendId);
        intent.putExtra(getString(R.string.keys_friend_full_name), theFullName);
        startActivity(intent);
    }

    /**
     * This method will show the delete button after a user long-presses
     * on the connection name.
     *
     * @param v the current view.
     * @param username_b the username of the connection that will be removed.
     * @return true
     */
    private boolean onShowDeleteButton(View v, String username_b) {
        Log.d("onShowDeleteButton: ", username_b);

        ImageButton im = v.findViewById(R.id.friendImageButtonDelete);
        im.setVisibility(View.VISIBLE);
        // listener to the delete button.
        im.setOnClickListener(view -> onDeleteFriend(username_b));

        return true;
    }

    /**
     * Handle deleting a friend by showing dialog to confirm
     * deleting a connection.
     *
     * @param username_b username of the connection that will be removed.
     */
    private void onDeleteFriend(String username_b) {
        //listener for user's click
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    Log.d("YES: ", "clicked");
                    deleteFriend(username_b);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    Log.d("NO: ", "clicked");
                    break;
            }
        };

        // display an alert dialog to confirm deleting a connection.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you wnt to delete your friend?" )
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    /**
     * Sends a request to the server to delete a connection.
     *
     * @param username_b username of the connection to delete.
     */
    private void deleteFriend(String username_b) {
        SharedPreferences prefs =
                this.getActivity().getSharedPreferences(getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        String memberidA =  String.valueOf(prefs.getInt(getString(R.string.keys_prefs_id), -1));

        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_connections_ops))
                .build();

        JSONObject searchJSON = asJSONObject(memberidA, username_b, "delete");

        new SendPostAsyncTask.Builder(uri.toString(), searchJSON)
                .onPostExecute(this::handleDeleteOnPost)
                //TODO: add onCancelled handler.
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    /**
     * Updates the fragment to show the new connection's list after
     * removing a connection.
     *
     * @param result response from server.
     */
    private void handleDeleteOnPost(String result) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(FriendFragment.this).attach(FriendFragment.this).commit();

        Toasty.info(getActivity(), fullname + " has been successfully deleted.", Toast.LENGTH_SHORT, true).show();

    }

    /**
     * Creates a JSONObject to send it with the request.
     *
     * @param memberid_a member id for the user sending the delete request.
     * @param username_b username of the user that is being removed from the list.
     * @param op detele connection operation.
     * @return a JSONObject that holds memberid_a, username_b, and operation.
     */
    private JSONObject asJSONObject(String memberid_a, String username_b, String op) {
        //build the JSONObject
        JSONObject msg = new JSONObject();
        try {
            msg.put("memberid_a", memberid_a);
            msg.put("username_b", username_b);
            msg.put("op", op);

        } catch (JSONException e) {
            Log.wtf("QUERY ", "Error creating JSON: " + e.getMessage());
        }
        return msg;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO: change menu options
        super.onCreateOptionsMenu(menu, inflater);
    }
}
