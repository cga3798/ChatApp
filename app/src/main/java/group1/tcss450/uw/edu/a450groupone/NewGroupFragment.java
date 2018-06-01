package group1.tcss450.uw.edu.a450groupone;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import group1.tcss450.uw.edu.a450groupone.utils.ListViewAdapter;


public class NewGroupFragment extends Fragment {

    private JSONArray contactsList;
    public static ArrayList<String> contactsListView;
    private ListViewAdapter adapter;
    private ListView list;
    private ArrayList<Integer> groupIds;
    private LayoutInflater inflater;
    private String groupName;
    private JSONArray groupMemberIds;

    public NewGroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        View v = inflater.inflate(R.layout.fragment_new_group, container, false);
        list = (ListView) v.findViewById(R.id.newGroupListView);
        //store list of friends in shared preferences
        SharedPreferences prefs =
                this.getActivity().getSharedPreferences(
                        getString(R.string.keys_prefs_contacts),
                        Context.MODE_PRIVATE);
        String contacts = prefs.getString("contacts_list", "");
        displayContacts(contacts);

        Button button = (Button) v.findViewById(R.id.newGroupButtonDone);
        button.setOnClickListener(view -> onCreateGroup());

        button = (Button) v.findViewById(R.id.newGroupButtonCancel);
        button.setOnClickListener(view -> onCancelCreateGroup());
        return v;
    }

    /**
     * display the list of your friends
     *
     * @param contacts a string with all the current user's contacts.
     */
    private void displayContacts(String contacts) {
        contactsListView = new ArrayList<>();

        try {
            contactsList = new JSONArray(contacts);
            for (int i = 0; i < contactsList.length(); i++) {
                JSONObject friend = contactsList.getJSONObject(i);
                String info = friend.getString("username") + "\n" +
                    friend.getString("firstname") + " "
                    + friend.getString("lastname");
                contactsListView.add(info);
            }

            adapter = new ListViewAdapter(getActivity(), "newGroup");
            list.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates the group. If there are more than one member selected a dialog
     * asking for group name. If there are no members selected, a toast will appear
     * with a warning.
     */
    private void onCreateGroup() {
        groupIds = new ArrayList<>();
        for (int i = 0; i < contactsList.length(); i++) {
            try {
                if (adapter.isChecked(i)) {
                    int memberid = contactsList.getJSONObject(i).getInt("memberid");
                    groupIds.add(memberid);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (groupIds.size() != 0) {
            showInputDialog();
            groupMemberIds = new JSONArray(this.groupIds);
        } else {
            Toasty.error(getActivity(), "Please select at least one participant", Toast.LENGTH_LONG).show();
        }

        // add current user id
        groupIds.add(getActivity().getSharedPreferences(
                getString(R.string.keys_shared_prefs),
                Context.MODE_PRIVATE).getInt(getString(R.string.keys_prefs_id), 0));
    }

    /**
     * Handles the cancel button group.
     */
    private void onCancelCreateGroup() {
        getFragmentManager().popBackStack();
    }


    /**
     * The dialog to ask for group chat name.
     */
    protected void showInputDialog() {
        // get prompts.xml view
        View promptView = inflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", (dialog, id) -> {
                    groupName = editText.getText().toString();
                    createNewGroupChat();
                })
                .setNegativeButton("Cancel",
                        (dialog, id) -> dialog.cancel());

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();

    }

    /**
     * Start a chat activity with all the members that were selected.
     */
    private void createNewGroupChat() {
        Log.e("GroupMmeberID: ", "" + groupMemberIds); //JSONArray
        Log.e("GroupName: ", groupName); //String

        Intent intent = new Intent(getContext(), ChatActivity.class);
        //send friend id to chat activity
        intent.putExtra(getString(R.string.keys_open_chat_source), R.id.makeGroupChatFragment);
        intent.putExtra(getString(R.string.keys_group_chat_name), groupName);
        intent.putIntegerArrayListExtra(getString(R.string.keys_group_chat_member_ids), groupIds);
        startActivity(intent);
    }
}
