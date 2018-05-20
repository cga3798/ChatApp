package group1.tcss450.uw.edu.a450groupone;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import group1.tcss450.uw.edu.a450groupone.utils.ListViewAdapter;
import group1.tcss450.uw.edu.a450groupone.utils.ListenManager;
import group1.tcss450.uw.edu.a450groupone.utils.SendPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

    private OnChatFragmentInteractionListener mListener;
    private int mChatId;
    private String mUsername;
    private String mSendUrl;
    private TextView mOutputTextView;
    private ListenManager mListenManager;
    private SharedPreferences prefs;
    private Toolbar mTopToolbar;



    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat2, container, false);

        prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        v.findViewById(R.id.chatSendButton).setOnClickListener(this::sendMessage);

        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        Log.wtf("CHAT ROOM", "" + prefs.getInt("chatId", R.string.keys_prefs_chatId));

//        TextView chatName = (TextView) v.findViewById(R.id.chatNameOfChatRoomView);
//        chatName.setText(String.valueOf(prefs.getString(getString(R.string.keys_prefs_chatName), "Chat Room")));
//        chatName.setAllCaps(true);
//        chatName.setTextSize(20);
//        chatName.setTextColor(getResources().getColor(R.color.colorAccent));
//        Button button = (Button) v.findViewById(R.id.view_member_list_button);
//        button.setOnClickListener(new View.OnClickListener() {
//
//                        public void onClick(View view) {
//
//                            TextView membersView = getActivity().findViewById(R.id.chatRoomMembers);
//                            if (membersView.getVisibility() == View.VISIBLE){
//                                membersView.setVisibility(View.GONE);
//                            }
//                            else {
//                                // call to populate users chat rooms
//                                membersView.setVisibility(View.VISIBLE);
//                                if (membersView.getText().length() > 0 ){
//
//                                }
//                                else {
//                                    try {
//                                        getMembers(v);
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }
//                        }});

        mTopToolbar = (Toolbar) v.findViewById(R.id.toolbar_top);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
            case R.id.view_chat_members:
                Log.e("view members: ", "clicked");
                return true;
            case R.id.place_holder:
                Log.e("place holder: ", "clicked");
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

        prefs = getActivity().getSharedPreferences(
                getString(R.string.keys_shared_prefs),
                Context.MODE_PRIVATE);
        if (!prefs.contains(getString(R.string.keys_prefs_chatId))) {
            throw new IllegalStateException("No chatId in prefs!");
        }
        mChatId = prefs.getInt(getString(R.string.keys_prefs_chatId), 0);
        Uri retrieve = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_get_chatMembers_list))
                .build();

        JSONObject body = new JSONObject();
        try {
            body.put("chatId", mChatId);

        } catch (JSONException e) {
            e.printStackTrace();

        }

        new SendPostAsyncTask.Builder(retrieve.toString(), body)
                .onPostExecute(this::populateMembers)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();

    }

    private void populateMembers(String res) {
        TextView membersView = getActivity().findViewById(R.id.chatRoomMembers);
        membersView.setSingleLine(false);
        try {

            JSONObject response = new JSONObject(res);

            if (response.getBoolean("success")) {

                JSONArray memberList = response.getJSONArray("name");

                for (int i = 0; i < memberList.length(); i++) {
                    JSONObject member = memberList.getJSONObject(i);
                    membersView.append("\n" + member.getString("name"));
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
        Log.wtf("ASYNCT_TASK_ERROR", result);
    }

    private void sendMessage(final View theButton) {
        JSONObject messageJson = new JSONObject();
        String msg = ((EditText) getView().findViewById(R.id.chatInputEditText))
                .getText().toString();

        try {
            messageJson.put(getString(R.string.keys_json_username), mUsername);
            messageJson.put(getString(R.string.keys_json_message), msg);
            messageJson.put(getString(R.string.keys_json_chat_id), prefs.getInt("chatId", R.string.keys_prefs_chatId));/// update to new chatid
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SendPostAsyncTask.Builder(mSendUrl, messageJson)
                .onPostExecute(this::endOfSendMsgTask)
                .onCancelled(this::handleError)
                .build().execute();

        final ScrollView scrollview = ((ScrollView) getView().findViewById(R.id.scrollViewChat));
        scrollview.post(new Runnable() {
            @Override
            public void run() {
                scrollview.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

    }

    private void handleError(final String msg) {
        Log.e("CHAT ERROR!!!", msg.toString());
    }

    private void endOfSendMsgTask(final String result) {
        try {
            JSONObject res = new JSONObject(result);

            if(res.get(getString(R.string.keys_json_success)).toString()
                    .equals(getString(R.string.keys_json_success_value_true))) {

                ((EditText) getView().findViewById(R.id.chatInputEditText))
                        .setText("");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void handleError(final Exception e) {
        Log.e("LISTEN ERROR!!!", e.getMessage());
    }

    private void publishProgress(JSONObject messages) {
        final String[] msgs;

        if(messages.has(getString(R.string.keys_json_messages))) {
            try {
                JSONArray jMessages = messages.getJSONArray(getString(R.string.keys_json_messages));
                msgs = new String[jMessages.length()];
                for (int i = 0; i < jMessages.length(); i++) {

                    JSONObject msg = jMessages.getJSONObject(i);
                    String username = msg.get(getString(R.string.keys_json_username)).toString();
                    String userMessage = msg.get(getString(R.string.keys_json_message)).toString();
                    msgs[i] = username + ":" + userMessage;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }

            LinearLayout chatContainer = getActivity().findViewById(R.id.chat_layout_to_hold_chat_messages);

            getActivity().runOnUiThread(() -> {
                for (String msg : msgs) {
                    Log.e("Message: ", msg);
                    String msgUsername = msg.substring(0, msg.indexOf(":"));
                    if (msgUsername.equals(mUsername)) {
                        String myMessage = msg.substring(msg.lastIndexOf(":") + 1);
                        Log.e("myUsername: ", msgUsername);
                        Log.e("myMessage: ", myMessage);

                        chatContainer.addView(getChatView ("myMessages", mUsername, myMessage));

                    } else {
                        String theirMessage = msg.substring(msg.lastIndexOf(":") + 1);
                        Log.e("theirUsername: ", msgUsername);
                        Log.e("theirMessage: ", theirMessage);
                        chatContainer.addView(getChatView ("theirMessages", msgUsername, theirMessage));
                    }




//                    LinearLayout.LayoutParams chatParams = new LinearLayout.LayoutParams(
//                            LinearLayout.LayoutParams.WRAP_CONTENT,
//                            LinearLayout.LayoutParams.WRAP_CONTENT);
//
//                    LinearLayout container = new LinearLayout(this.getActivity());
//                    container.setOrientation(LinearLayout.HORIZONTAL);
//                    String temp = msg.substring(0, msg.indexOf(":"));
//                    TextView text = new TextView(this.getActivity());
//                    text.append(msg);
//                    text.append(System.lineSeparator());
//                    container.addView(text);
//
//                    if (temp.equals(mUsername)) {
//                        chatParams.gravity = Gravity.RIGHT;
////                        container.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
//                    }
//                    else {
//                        chatParams.gravity = Gravity.LEFT;
////                        container.setBackgroundColor(getResources().getColor(R.color.colorAccent));
//                    }
//                    container.setLayoutParams(chatParams);
//                    chatContainer.addView(container);
//                    EditText typeText = ((EditText) getView().findViewById(R.id.chatInputEditText));
//                    typeText.requestFocus();
                }
            });
        }
    }

    private View getChatView(String msgSource, String msgUsername, String message) {
        View v;

        if (msgSource.equals("myMessages")) {

            v = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_message_sent, null, false);

            TextView tv = v.findViewById(R.id.text_message_body);
            tv.setText(message);
        } else {

            v = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_message_received, null, false);

            TextView tv = v.findViewById(R.id.text_message_body);
            tv.setText(message);
            tv = v.findViewById(R.id.text_message_name);
            tv.setText(msgUsername);
        }
        return v;
    }


    @Override
    public void onStart() {
        super.onStart();

        if (!prefs.contains(getString(R.string.keys_prefs_username))) {
            throw new IllegalStateException("No username in prefs!");
        }
        mUsername = prefs.getString(getString(R.string.keys_prefs_username), "");

        mSendUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_lab_url))
                .appendPath(getString(R.string.ep_send_message))
                .build()
                .toString();
        Uri retrieve = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_lab_url))
                .appendPath(getString(R.string.ep_get_message))
                .appendQueryParameter("chatId", "" + prefs.getInt("chatId", R.string.keys_prefs_chatId)) // upadate  to new chatid
                .build();
        if (prefs.contains(getString(R.string.keys_prefs_time_stamp))) {
            //ignore all of the seen messages. You may want to store these messages locally
            mListenManager = new ListenManager.Builder(retrieve.toString(),
                    this::publishProgress)
                    .setTimeStamp(prefs.getString(getString(R.string.keys_prefs_time_stamp), "0"))
                    .setExceptionHandler(this::handleError)
                    .setDelay(1000)
                    .build();
        } else {
            //no record of a saved timestamp. must be a first time login
            mListenManager = new ListenManager.Builder(retrieve.toString(),
                    this::publishProgress)
                    .setExceptionHandler(this::handleError)
                    .setDelay(1000)
                    .build();
        }
//        final ScrollView scrollview = ((ScrollView) getView().findViewById(R.id.scrollViewChat));
//        scrollview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                scrollview.post(new Runnable() {
//                    public void run() {
//                        scrollview.fullScroll(View.FOCUS_DOWN);
//                    }
//                });
//            }
//        });
//        EditText typeText = ((EditText) getView().findViewById(R.id.chatInputEditText));
//        typeText.requestFocus();

    }

    /**
     * onAttach method to attach login fragment listener.
     *
     * author: Casey Anderson
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ChatFragment.OnChatFragmentInteractionListener) {
            mListener = (ChatFragment.OnChatFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     * default onDetach method
     *
     * author: Casey Anderson
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        mListenManager.startListening();
    }


    @Override
    public void onStop() {
        super.onStop();
        String latestMessage = mListenManager.stopListening();
        // Save the most recent message timestamp
        prefs.edit().putString(
                getString(R.string.keys_prefs_time_stamp),
                latestMessage)
                .apply();
    }

    public interface OnChatFragmentInteractionListener {
        void onNewChat();
        void onOpenChat();
    }


}
