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
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import group1.tcss450.uw.edu.a450groupone.utils.SendPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment implements View.OnClickListener{

    String str_testDeleteChat_chatid = "";
    private String memberid;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        SharedPreferences prefs =
                this.getActivity().getSharedPreferences(getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);

        memberid =  String.valueOf(prefs.getInt(getString(R.string.keys_prefs_id), -1));

        Button b = (Button) v.findViewById(R.id.settingsBTNDelete);
        b.setOnClickListener(this);
        /*
        * this was code for passing the chatname on creation
         */
//        if (getArguments() != null) {
//            if (getArguments().getBoolean("chatname"))  {
//                str_testDeleteChat_chatname = getArguments().getString("chatname");
//            }
//        } else {
//            Log.e("getArguments: ", "NULL");
//        }

        return v;
    }

    //when clicked
    void sendDelete() {




        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_delete_chat))
                .build();

        JSONObject msg = new JSONObject();

        try {
            msg.put("chatid", str_testDeleteChat_chatid);
            msg.put("memberid", memberid);
        } catch (JSONException e) {
            Log.e("DELETECHAT", "Error creating JSON: " + e.getMessage());
        }

        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleDeleteOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    void handleDeleteOnPost(String result){
        ((EditText) getView().findViewById(R.id.settingsETChatID)).setText("");
    }

    void handleErrorsInTask(String result){

        ((EditText) getView().findViewById(R.id.settingsETChatID)).setError("Error");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.settingsBTNDelete:
                Log.e("Settings", "onClick: ");
                str_testDeleteChat_chatid = ((EditText) getView().findViewById(R.id.settingsETChatID)).getText().toString();
                sendDelete();
                break;
            default:
                break;
        }
    }
}
