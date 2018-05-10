package group1.tcss450.uw.edu.a450groupone;

import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
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

    private ListenManager mListenManager;

    private OnHomeFragmentInteractionListener mListener;

    private TextView cityTv, tempTv, weatherDescTv;

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

        setWeatherData();
        try {
            getChats(v);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return v;
    }

    private void getChats ( View v ) throws JSONException {
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        if (!prefs.contains(getString(R.string.keys_prefs_username))) {
            throw new IllegalStateException("No username in prefs!");
        }
        mMemberId = prefs.getInt(getString(R.string.keys_prefs_id), 0);

        Uri retrieve = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath("getChatMembers")
                .build();


        JSONObject body = new JSONObject();
        // provide current user id
        try {
            body.put("memberId",
                    getActivity().getSharedPreferences(getString(R.string.keys_shared_prefs),
                            Context.MODE_PRIVATE)
                            .getInt(getString(R.string.keys_prefs_id), 0) );
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
        Log.d("GOTCONTACTS", res);
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        if (!prefs.contains(getString(R.string.keys_prefs_username))) {
            throw new IllegalStateException("No username in prefs!");
        }

        for (int i = -1; i < 5; i++) {

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout container = new LinearLayout(this.getActivity());
            container.setOrientation(LinearLayout.HORIZONTAL);

            container.setId(i);
            final int id_ = container.getId();


            Button button = new Button(this.getActivity());

            button.setId(i);
            button.setText("button " + id_);
            //button.setBackgroundColor(this.getActivity().getColor(R.color.colorAccent));

            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    mListener.onNewChat();
                }});

            container.addView(button, params);
            try {
                String text = getLastMessage();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            TextView text = new TextView(this.getActivity());
            text.setText("hello");
            container.addView(text);
            buttonContainer.addView(container, params);

        }


//        try {
//            JSONObject response = new JSONObject(res);
//            if (response.getBoolean("success")) {
//                JSONArray chatList = response.getJSONArray("name");
//
//                for (int i = -1; i < chatList.length(); i++) {
//                    JSONObject chat = chatList.getJSONObject(i);
//                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.WRAP_CONTENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT);
//                    Button button = new Button(this.getActivity());
//
//                    button.setId(i);
//                    final int id_ = button.getId();
//                    button.setText("button " + id_);
//                    button.setBackgroundColor(this.getActivity().getColor(R.color.colorAccent));
//
//                    button.setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View view) {
//                        mListener.onNewChat();
//                    }});
//                    buttonContainer.addView(button, params);
//
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }
    private void handleError(final Exception e) {
        Log.e("LISTEN ERROR!!!", e.getMessage());
    }


//    private String getLastMessage(String res) {
//        String message;
//
//        try {
//            JSONObject response = new JSONObject(res);
//            if (response.getBoolean("success")) {
//                JSONArray msg = response.getJSONArray("messsage");
//                msg
//
//
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return message;
//    }
    private String getLastMessage() {
        String message = "hello";

        return message;
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
                Log.d("WEATHER_FRAG", "setting data");
                cityTv.setText(args.getString(Weather.K_CITY));
                weatherDescTv.setText(args.getString(Weather.K_WEATHER_DESC));
                tempTv.setText(args.getString(Weather.K_CURRENT_TEMP));
            }
        });
        asyncTask.execute("47.25288","-122.44429");
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
