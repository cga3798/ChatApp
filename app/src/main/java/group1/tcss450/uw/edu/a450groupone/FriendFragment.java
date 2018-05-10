package group1.tcss450.uw.edu.a450groupone;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.SearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import group1.tcss450.uw.edu.a450groupone.utils.SendPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendFragment extends Fragment {

    private OnFriendFragmentInteractionListener mListener;


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
        //populateContactsList(v);

//        SearchView searchView = v.findViewById(R.id.friendSearchView);
//        searchView.setActivated(true);

//        v.findViewById(R.id.friendButtonAddNewFriend).setOnClickListener(view -> onAddNewFriend(v));

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
                if (friendsList.length() == 0) {
                    contactsListContainer.addView(
                            getContactView("", "There are no contacts to display"));
                } else {
                    for (int i = 0; i < friendsList.length(); i++) {
                        JSONObject friend = friendsList.getJSONObject(i);
                        contactsListContainer.addView(
                                getContactView(friend.getString("username"),
                                        friend.getString("firstname") + " "
                                                + friend.getString("lastname")));
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


//    private void onAddNewFriend(View v) {
//        mListener.onAddNewFriend();
//    }

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

    private View getContactView(String nickname, String fullName) {
        View v = LayoutInflater.from(getContext())
                .inflate(R.layout.contact_row, null, false);

        TextView tv = v.findViewById(R.id.friendsTextViewNickname);
        tv.setText(nickname);
        tv = v.findViewById(R.id.friendsTextViewFullName);
        tv.setText(fullName);

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO: change menu options
        super.onCreateOptionsMenu(menu, inflater);
    }
}
