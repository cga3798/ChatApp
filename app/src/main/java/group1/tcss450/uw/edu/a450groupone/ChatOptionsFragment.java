package group1.tcss450.uw.edu.a450groupone;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import group1.tcss450.uw.edu.a450groupone.utils.SendPostAsyncTask;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatOptionsFragment extends Fragment {

    private SharedPreferences prefs;
    private View v;
    private LayoutInflater inflater;
    private int mMemberId, chatId, numOfMembers;
    private Toolbar mTopToolbar;
    private int accentColor;

    public ChatOptionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.inflater = inflater;
        v = inflater.inflate(R.layout.fragment_chat_options, container, false);

        prefs = getActivity().getSharedPreferences(
                getString(R.string.keys_shared_prefs),
                Context.MODE_PRIVATE);

        mMemberId = prefs.getInt(getString(R.string.keys_prefs_id), 0);
        chatId = prefs.getInt(getString(R.string.keys_prefs_chatId), 0);

        Button button = (Button) v.findViewById(R.id.chatOptionLeaveGroup);
        button.setOnClickListener(view -> confirmDelete(chatId));

        button = (Button) v.findViewById(R.id.chatOptionViewMembers);
        button.setOnClickListener(view -> {
            try {
                getMembers(v);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        SharedPreferences theme = getActivity().getSharedPreferences("themePrefs", MODE_PRIVATE);
        int themeId = theme.getInt("themePrefs", 5);

        mTopToolbar = (Toolbar) v.findViewById(R.id.toolbar_top);
        switch (themeId) {
            case 1:
                mTopToolbar.setBackgroundColor(getActivity()
                        .getColor(R.color.colorPrimaryTheme1));
                accentColor = getActivity().getColor(R.color.colorAccentTheme1);
                break;
            case 2:
                mTopToolbar.setBackgroundColor(getActivity()
                        .getColor(R.color.colorPrimaryTheme2));
                accentColor = getActivity().getColor(R.color.colorAccentTheme2);

                break;
            case 3:
                mTopToolbar.setBackgroundColor(getActivity()
                        .getColor(R.color.colorPrimaryTheme3));
                accentColor = getActivity().getColor(R.color.colorAccentTheme3);

                break;
            default:
                mTopToolbar.setBackgroundColor(getActivity()
                        .getColor(R.color.colorPrimary));
                accentColor = getActivity().getColor(R.color.colorAccent);

                break;
        }

        TextView mTitle = (TextView) mTopToolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(String.valueOf(prefs.getString(getString(R.string.keys_prefs_chatName), "Chat Room")));

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mTopToolbar);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);

        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setDisplayShowHomeEnabled(true);

        setHasOptionsMenu(true);

        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                FragmentManager fm = getFragmentManager();
                fm.popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

        /**
     * method to get all members that are in chatroom
     *
     * author: Casey Anderson
     */
    private void getMembers ( View v ) throws JSONException {
        if (!prefs.contains(getString(R.string.keys_prefs_chatId))) {
            throw new IllegalStateException("No chatId in prefs!");
        }


        Uri retrieve = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_get_chatMembers_list))
                .build();
        JSONObject body = new JSONObject();
        try {
            body.put("chatId", chatId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new SendPostAsyncTask.Builder(retrieve.toString(), body)
                .onPostExecute(this::populateMembers)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    private void populateMembers(String res) {
        ArrayList<String> members = new ArrayList<>();
        try {
            JSONObject response = new JSONObject(res);
            if (response.getBoolean("success")) {
                JSONArray memberList = response.getJSONArray("name");
                for (int i = 0; i < memberList.length(); i++) {
                    JSONObject member = memberList.getJSONObject(i);
                    Log.e("Members: ", member.getString("name"));
                    members.add(member.getString("name"));
                }
            }

            numOfMembers = members.size();
            Button button = (Button) v.findViewById(R.id.chatOptionLeaveGroup);
            button.setVisibility(View.GONE);
            LinearLayout linearLayout = (LinearLayout) v.findViewById(R.id.chatOptionLinearLayout);
            button = (Button) v.findViewById(R.id.chatOptionViewMembers);
            button.setText("Group members ");
            button.setClickable(false);
            for (int i = 0; i < members.size(); i++) {
                TextView tv = new TextView(getActivity());
                String str = "\t\t\t\t"+ (i+1) + ". "+ members.get(i);
                tv.setText(str);
                tv.setTextSize(18);
                tv.setTextColor(Color.parseColor("#4C4C4C"));
                if(tv.getParent()!=null)
                    ((ViewGroup)tv.getParent()).removeView(tv);
                linearLayout.addView(tv);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /*
    * Prompts user for confirmation to delete the chat.
    */
    private void confirmDelete(int chatid) {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    Log.d("YES: ", "clicked");
                    deleteChat(chatid);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    Log.d("NO: ", "clicked");
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you wnt to delete your chat?" )
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    /*
     * Sends the delete chat json to the server.
     */
    private void deleteChat(int chatid){
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_delete_chat))
                .build();

        JSONObject msg = new JSONObject();

        try {
            msg.put("chatid", chatid);
            msg.put("memberid", mMemberId);
        } catch (JSONException e) {
            Log.e("DELETECHAT", "Error creating JSON: " + e.getMessage());
        }

        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleDeleteOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    /*
    *   Reloads home and displays a confirmation that chat was deleted.
     */
    private void handleDeleteOnPost(String result) {
        Intent intent = new Intent(getActivity(), NavigationActivity.class);
        startActivity(intent);
    }

    /**
     * Handle errors that may occur during the AsyncTask.
     * @param result the error message provide from the AsyncTask
     */
    private void handleErrorsInTask(String result) {
        Log.wtf("ASYNCT_TASK_ERROR", result);
    }

    public int getNumOfMembers() {
        return numOfMembers;
    }

}
