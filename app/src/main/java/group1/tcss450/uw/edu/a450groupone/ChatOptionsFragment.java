package group1.tcss450.uw.edu.a450groupone;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import org.w3c.dom.Text;

import java.util.ArrayList;

import group1.tcss450.uw.edu.a450groupone.utils.SendPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatOptionsFragment extends Fragment {

    private SharedPreferences prefs;
    private View v;
    private LayoutInflater inflater;

    public ChatOptionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.inflater = inflater;
        v = inflater.inflate(R.layout.fragment_chat_options, container, false);

        Button button = v.findViewById(R.id.chatOptionLeaveGroup);
        button.setOnClickListener(view -> {

        });

        button = v.findViewById(R.id.chatOptionViewMembers);
        button.setOnClickListener(view -> {
            try {
                getMembers(v);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        return v;
    }

        /**
     * method to get all members that are in chatroom
     *
     * author: Casey Anderson
     */
    private void getMembers ( View v ) throws JSONException {
        int chatId;

        prefs = getActivity().getSharedPreferences(
                getString(R.string.keys_shared_prefs),
                Context.MODE_PRIVATE);
        if (!prefs.contains(getString(R.string.keys_prefs_chatId))) {
            throw new IllegalStateException("No chatId in prefs!");
        }
        chatId = prefs.getInt(getString(R.string.keys_prefs_chatId), 0);
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

            Button button = v.findViewById(R.id.chatOptionLeaveGroup);
            button.setVisibility(View.GONE);
            LinearLayout linearLayout = v.findViewById(R.id.chatOptionLinearLayout);
            button = v.findViewById(R.id.chatOptionViewMembers);
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

    /**
     * Handle errors that may occur during the AsyncTask.
     * @param result the error message provide from the AsyncTask
     */
    private void handleErrorsInTask(String result) {
        Log.wtf("ASYNCT_TASK_ERROR", result);
    }

}
