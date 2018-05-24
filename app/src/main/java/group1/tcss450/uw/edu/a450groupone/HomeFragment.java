package group1.tcss450.uw.edu.a450groupone;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import es.dmoral.toasty.Toasty;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import group1.tcss450.uw.edu.a450groupone.utils.SendPostAsyncTask;
import group1.tcss450.uw.edu.a450groupone.utils.Weather;

/**
 * Fragment to be landing view after user logs in.
 *
 *
 * @author Casey Anderson
 * @version 1 April 2018
 */
public class HomeFragment extends Fragment implements View.OnClickListener{

    private int mMemberId;

    private OnHomeFragmentInteractionListener mListener;

    private TextView cityTv, tempTv, weatherDescTv, weatherIcon;

    private SharedPreferences prefs;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * setting up fragment
     *
     * author: Casey Anderson
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_home, container, false);

        //location = new MyLocation(getContext(), null);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);

        // set click listener on weather view
        v.findViewById(R.id.homeCurrentWeatherDisplay).setOnClickListener(this);

        TextView tv = (TextView) v.findViewById(R.id.HomeTextViewCurrentDate);
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone(Weather.GMT_PACIFIC));
        String dateString = sdf.format(date);
        tv.setText(dateString);

        cityTv = (TextView) v.findViewById(R.id.HomeTextViewCity);
        tempTv = (TextView) v.findViewById(R.id.HomeTextViewTemperature);
        weatherDescTv = (TextView) v.findViewById(R.id.HomeTextViewWeatherDesc);

        Typeface weatherFont = Typeface.createFromAsset(getContext().getAssets(), Weather.FONT_PATH);
        weatherIcon = (TextView) v.findViewById(R.id.homeTextViewWeatherIcon);
        weatherIcon.setTypeface(weatherFont);

        setWeatherData();

        // call to populate users chat rooms
        try {
            getChats(v);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return v;
    }

    /**
     * method to get all chatrooms for user and start building chatroom buttons
     *
     * author: Casey Anderson
     */
    private void getChats ( View v ) throws JSONException {
        prefs = getActivity().getSharedPreferences(
                getString(R.string.keys_shared_prefs),
                Context.MODE_PRIVATE);

        if (!prefs.contains(getString(R.string.keys_prefs_username))) {
            throw new IllegalStateException("No username in prefs!");
        }

        mMemberId = prefs.getInt(getString(R.string.keys_prefs_id), 0);
        Uri retrieve = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_get_chatMembers))
                .build();

        JSONObject body = new JSONObject();

        // provide current user id
        try {
            body.put("memberId", mMemberId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new SendPostAsyncTask.Builder(retrieve.toString(), body)
                .onPostExecute(this::populateChats)
                //TODO: add onCancelled handler.
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    /**
     * method to create chat room list from users available chatrooms
     *
     * author: Casey Anderson
     */
    private void populateChats(String res) {
        LinearLayout buttonContainer = (LinearLayout) getActivity().findViewById(R.id.HomeLinearLayoutButtonContainer);

        if (!prefs.contains(getString(R.string.keys_prefs_username))) {
            throw new IllegalStateException("No username in prefs!");
        }

        try {
            JSONObject response = new JSONObject(res);
            if (response.getBoolean("success")) {
                JSONArray chatList = response.getJSONArray("name");
                for (int i = 0; i < chatList.length(); i++) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);

                    // layout to hold chatroom buttons and textviews
                    LinearLayout container = new LinearLayout(this.getActivity());
                    container.setOrientation(LinearLayout.HORIZONTAL);

                    // button for chatrooms
                    Button button = new Button(this.getActivity(), null, android.R.attr.buttonBarButtonStyle);
                    JSONObject name = chatList.getJSONObject(i);
                    try {
                        prefs.edit().putInt(
                                getString(R.string.keys_prefs_chatId),
                                name.getInt("chatid"))
                                .apply();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // parse name string here...
                    String chatName = parseChatName(name.getString("name"));

                    button.setText(chatName);
                    button.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            try {
                                prefs.edit().putInt(
                                        getString(R.string.keys_prefs_chatId),
                                        name.getInt("chatid"))
                                        .apply();
                                prefs.edit().putString(
                                        getString(R.string.keys_prefs_chatName),
                                        chatName)
                                        .apply();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mListener.onOpenChat();
                        }});

                    container.addView(button, params);
                    // textView to display chatrooms last message
                    TextView textView = new TextView(this.getActivity());
                    textView.setId(R.id.chat_text_button_on);
                    // method to get messages for textView
                    try {
                        getLastMessage(textView);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // adding textView to layout
                    container.addView(textView);
                    // adding layout to container
                    buttonContainer.addView(container, params);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parses chat name from composed response chatname.
     * @param responseName response chatname
     * @return parsed chat name
     */
    public String parseChatName(String responseName) {
        String result;
        // chat is a group
        if (responseName.charAt(0) == '_') {
            // name of chat is whatever after "_" char
            result = responseName.substring(1);
        } else { // chat between two people
            String userFullName = prefs.getString(getString(R.string.keys_prefs_first_name), "")
                    + " " + prefs.getString(getString(R.string.keys_prefs_last_name), "");

            String name1 = responseName.split("_")[0];
            String name2 = responseName.split("_")[1];

            // if name1 is current user -> friend name is name2
            result = (name1.equals(userFullName)) ? name2 : name1;
        }

        return result;
    }

    private void getLastMessage(View v) throws JSONException {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_get_last_message))
                .build();

        JSONObject body = new JSONObject();

        // provide current chat id and a timestamp to get all messages
        body.put("chatId", prefs.getInt("chatId", R.string.keys_prefs_chatId));
        body.put("after", "1970-01-01 00:00:00.000000");

        new SendPostAsyncTask.Builder(uri.toString(), body)
                .onPostExecute(this::populateChatText)
                //TODO: add onCancelled handler.
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    private void populateChatText(String res) {
        String text = "";
        try {
            JSONObject response = new JSONObject(res);
            if (response.getBoolean("success")) {
                JSONObject message = response.getJSONObject("messages");
                text = message.getString("message");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // set id for text view
        TextView textView = (TextView) getActivity().findViewById(R.id.chat_text_button_on);
        textView.setId(R.id.chat_text_button_off);

        // turns off prior id
        textView.setText(text);
    }

    /*
     * Displays the delete button next to chats.
     */
    private boolean onDeleteChat(View v, String name, int chatid) {
        Log.d("onDeleteChat: ", name);
        ImageButton im = (ImageButton) v.findViewById(R.id.chatImageButtonDelete);
        im.setVisibility(View.VISIBLE);
        im.setOnClickListener(view -> confirmDelete(chatid));

        return true;
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
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(HomeFragment.this).attach(HomeFragment.this).commit();

        Toasty.info(getActivity(),"Chat successfully deleted.", Toast.LENGTH_SHORT, true).show();

    }


    /**
     * Handle errors that may occur during the AsyncTask.
     * @param result the error message provide from the AsyncTask
     */
    private void handleErrorsInTask(String result) {
        Log.wtf("ASYNCT_TASK_ERROR", result);
    }

    public void setWeatherData() {
        Location currentLocation = null;

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            currentLocation = LocationServices.FusedLocationApi.getLastLocation(
                    ((NavigationActivity)getActivity()).getmGoogleApiClient());
            if (currentLocation != null) {
                // first log we should see
                Log.i("HOME_CURRENT_LOCATION", currentLocation.toString());
            }

        }

        Weather.RetrieveData asyncTask = new Weather.RetrieveData(getContext(), R.id.fragmentHome, new Weather.AsyncResponse() {
            public void processFinish(Bundle args) {
                cityTv.setText(args.getString(Weather.K_CITY));
                weatherDescTv.setText(args.getString(Weather.K_WEATHER_DESC));
                tempTv.setText(args.getString(Weather.K_CURRENT_TEMP));
                weatherIcon.setText(Html.fromHtml(args.getString(Weather.K_ICON)));
            }
        });

        if(currentLocation == null) {
            // use Tacoma as default
            // TODO: change to default/preferred city later
            asyncTask.execute("47.25288", "-122.44429");

        } else {
            // use current location
            asyncTask.execute( String.valueOf(currentLocation.getLatitude()),
                    String.valueOf(currentLocation.getLongitude()) );
        }
    }

    /**
     * onAtttach method for home fragment listener
     *
     * author: Casey Anderson
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnHomeFragmentInteractionListener) {
            mListener = (OnHomeFragmentInteractionListener) context;
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

    /**
     * temperary onClick method for temp buttons
     *
     * author: Casey Anderson
     */
    @Override
    public void onClick(View view) {
        if (mListener != null) {
            switch (view.getId()) {
                case R.id.homeCurrentWeatherDisplay:
                    mListener.NewWeather(); // temp weather button for navigation
                    break;
                default:
                    Log.wtf("", "Didn't expect to see me...");
            }
        }
    }

    /**
     * listener interfaces for temp buttons
     *
     * author: Casey Anderson
     */
    public interface OnHomeFragmentInteractionListener {
        void onNewChat();
        void onOpenChat();
        void NewWeather();
    }
}