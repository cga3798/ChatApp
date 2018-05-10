package group1.tcss450.uw.edu.a450groupone;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import group1.tcss450.uw.edu.a450groupone.utils.ListViewAdapter;
import group1.tcss450.uw.edu.a450groupone.utils.SendPostAsyncTask;


public class SearchNewFriendFragment extends Fragment implements SearchView.OnQueryTextListener{

    private OnAddFriendFragmentInteractionListener mListener;

    private ListView list;
    private ListViewAdapter adapter;
    private SearchView searchView;
    private String queryEntered;
    private boolean showInvite = true;
    public static ArrayList<String> connectionResultList, memberIds, fullnames;


    public SearchNewFriendFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_search_new_friend, container, false);

        searchView = v.findViewById(R.id.searchFriendSearchView);
        searchView.setActivated(true);
        searchView.setOnQueryTextListener(this);
//        v.findViewById(R.id.addFriendButtonInvite).setVisibility(View.GONE);

        return v;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        onSearch(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
//        onSearch(newText);
//        getView().findViewById(R.id.addFriendButtonInvite)
//                .setVisibility(View.GONE);
        return false;
    }

    /**
     * Establish connection to web services to search users.
     *
     * @param query
     */
    private void onSearch(String query) {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_search_app_users))
                .build();

        JSONObject searchJSON = asJSONObject(query);

        queryEntered = query;
        new SendPostAsyncTask.Builder(uri.toString(), searchJSON)
                .onPostExecute(this::handleSearchOnPost)
                //TODO: add onCancelled handler.
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }


    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
    }

    private void handleSearchOnPost(String result) {
        try {
            //results
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");

            Log.d("Name: ", result);

            if (success) {
                list = (ListView) getActivity().findViewById(R.id.searchFriendListView);

                int length = resultsJSON.getJSONArray("names").length();
                memberIds = new ArrayList<>();
                connectionResultList = new ArrayList<>();
                fullnames = new ArrayList<>();

                if (length < 1) {
                    showInvite = false;
                    connectionResultList.add("No results found for \'" + queryEntered + " \' ");
                } else {
                    showInvite = true;
                    for (int i = 0; i < length; i++) {
                        String first = resultsJSON.getJSONArray("names")
                                .getJSONObject(i).getString("firstname");
                        String last = resultsJSON.getJSONArray("names")
                                .getJSONObject(i).getString("lastname");
                        String user = resultsJSON.getJSONArray("names")
                                .getJSONObject(i).getString("username");

                        memberIds.add(resultsJSON.getJSONArray("names")
                                .getJSONObject(i).getString("memberid"));

                        //array that will be displayed in search result
                        connectionResultList.add(first + " " + last + "\n" + "Username: " + user);
                        fullnames.add(first + " " + last);

                    }
                }

                adapter = new ListViewAdapter(getActivity());
                list.setAdapter(adapter);
                list.setOnItemClickListener((parent, view, position, id) -> {
                    onClickOnSearchResult(position);
                });

            } else {
                Toast.makeText(getActivity(),
                        "Search unsuccessful. Please try again", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
        }
    }

    private void onClickOnSearchResult(int position) {
        if (showInvite) {
//            getView().findViewById(R.id.addFriendButtonInvite)
//                    .setVisibility(View.VISIBLE);
            //invite button listener

            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Log.d("YES: ", "clicked");
                        mListener.onInviteNewFriend(memberIds.get(position), fullnames.get(position));
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        Log.d("NO: ", "clicked");

                        break;
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Would you like to send an invitation to " + fullnames.get(position) + "?" )
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();

        }
    }


    public JSONObject asJSONObject(String query) {
        //build the JSONObject
        JSONObject msg = new JSONObject();

        query = query.trim();
        //TODO: DON'T SEND CURRENT USER INFO. CAN'T SEE SELF IN RESULT.
        try {
            if (query.contains("@") ) {
                msg.put("email", query);
            } else if (query.contains(" ")){
                String[] fullname = query.split("\\s+");
                msg.put("first", fullname[0]);
                msg.put("last", fullname[1]);
            }  else {
                msg.put("username", query);
            }

        } catch (JSONException e) {
            Log.wtf("QUERY ", "Error creating JSON: " + e.getMessage());
        }
        return msg;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SearchNewFriendFragment.OnAddFriendFragmentInteractionListener) {
            mListener = (SearchNewFriendFragment.OnAddFriendFragmentInteractionListener) context;
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

    public interface OnAddFriendFragmentInteractionListener {
        void onInviteNewFriend(String memberidB, String fullname);
    }

}
