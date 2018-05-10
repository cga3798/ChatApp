package group1.tcss450.uw.edu.a450groupone;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import group1.tcss450.uw.edu.a450groupone.utils.MyLocation;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import group1.tcss450.uw.edu.a450groupone.utils.ListenManager;
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

    private TextView cityTv, tempTv, weatherDescTv;
    //private MyLocation location;

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

        // these are temporary buttons
        Button b = (Button) v.findViewById(R.id.HomeButtonNewChat);
        b.setOnClickListener(this);
        b = (Button) v.findViewById(R.id.weatherButton1);
        b.setOnClickListener(this);

        TextView tv = (TextView) v.findViewById(R.id.HomeTextViewCurrentDate);
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy h:mm a");
        String dateString = sdf.format(date);
        tv.setText(dateString);

        cityTv = v.findViewById(R.id.HomeTextViewCity);
        tempTv = v.findViewById(R.id.HomeTextViewTemperature);
        weatherDescTv = v.findViewById(R.id.HomeTextViewWeatherDesc);


//        Timer myTimer = new Timer();
//        myTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                Log.d("TIMER_GPS_CHECK", "checking gps ready");
//                if (location.isReady() ) {
//                    Log.d("TIOMERCHECKGPS", "setting data");
                    setWeatherData();
//                    myTimer.cancel();
//                }
//            }
//        }, 0, 500);



        // call to populate users chat rooms
        try {
            getChats(v);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return v;
    }

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
    private void populateChats(String res) {

        LinearLayout buttonContainer = getActivity().findViewById(R.id.HomeLinearLayoutButtonContainer);

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
                    button.setText(name.getString("name") );
                    button.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            try {
                                prefs.edit().putString(
                                        getString(R.string.keys_prefs_chatId),
                                        name.getString("chatid"))
                                        .apply();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mListener.onNewChat();
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


    private void getLastMessage(View v) throws JSONException {

        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_get_last_message))
                .build();

        JSONObject body = new JSONObject();

        // provide current chat id and a timestamp to get all messages
        body.put("chatId", 1);
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

            JSONObject message = response.getJSONObject("messages");
            text = message.getString("message");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // set id for text view
        TextView textView = getActivity().findViewById(R.id.chat_text_button_on);
        textView.setId(R.id.chat_text_button_off);

        // turns off prior id
        textView.setText(text);
    }

    /**
     * Handle errors that may occur during the AsyncTask.
     * @param result the error message provide from the AsyncTask
     */
    private void handleErrorsInTask(String result) {
        Log.wtf("ASYNCT_TASK_ERROR", result);
    }


    private void setWeatherData() {
        Weather.RetrieveData asyncTask = new Weather.RetrieveData(R.id.fragmentWeather ,new Weather.AsyncResponse() {
            public void processFinish(Bundle args) {
                cityTv.setText(args.getString(Weather.K_CITY));
                weatherDescTv.setText(args.getString(Weather.K_WEATHER_DESC));
                tempTv.setText(args.getString(Weather.K_CURRENT_TEMP));
            }
        });
        // get current location
        asyncTask.execute( //location.getLatitude(), location.getLongitude());
                     "47.25288","-122.44429");
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
              case R.id.HomeButtonNewChat:
                    mListener.onNewChat();
                    break;
                case R.id.weatherButton1:
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
        void NewWeather();
    }
}
