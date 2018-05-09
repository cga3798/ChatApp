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




       // LinearLayout buttonContainer = v.findViewById(R.id.HomeLinearLayoutButtonContainer);
//        for (int i  = 1; i < 20; i++) {
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.WRAP_CONTENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT);
//            Button button = new Button(this.getActivity());
//
//            button.setId(i);
//            final int id_ = button.getId();
//            button.setText("button " + id_);
//            button.setBackgroundColor(this.getActivity().getColor(R.color.colorAccent));
//            buttonContainer.addView(button, params);
//            button = ((Button) v.findViewById(id_));
//            button.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View view) {
//                    mListener.onNewChat();
//                }
//            });
//        }



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
        LinearLayout chatListContainer = getActivity().findViewById(R.id.HomeLinearLayoutButtonContainer);
        Log.wtf("GOTCHATS", res);

        try {
            JSONObject response = new JSONObject(res);
            if (response.getBoolean("success")) {
                JSONArray chatList = response.getJSONArray("name");

                for (int i = 0; i < chatList.length(); i++) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
                    Button button = new Button(this.getActivity());

                    button.setId(i);
                    final int id_ = button.getId();
                    button.setText("button " + id_);
                    button.setBackgroundColor(this.getActivity().getColor(R.color.colorAccent));
                    chatListContainer.addView(button, params);
                    button = ((Button) this.getActivity().findViewById(id_));
                    button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    mListener.onNewChat();
                }
            });
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
