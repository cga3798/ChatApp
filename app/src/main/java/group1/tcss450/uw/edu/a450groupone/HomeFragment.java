package group1.tcss450.uw.edu.a450groupone;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import group1.tcss450.uw.edu.a450groupone.utils.Weather;

public class HomeFragment extends Fragment implements View.OnClickListener{


    private OnHomeFragmentInteractionListener mListener;

    private TextView cityTv, tempTv, weatherDescTv;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_home, container, false);
//        Button b = (Button) v.findViewById(R.id.HomeButtonNewChat);
//        b.setOnClickListener(this);
        Button b = (Button) v.findViewById(R.id.tempChat3);
        b.setOnClickListener(this);
        b = (Button) v.findViewById(R.id.tempChat1);
        b.setOnClickListener(this);
        b = (Button) v.findViewById(R.id.tempChat2);
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

        return v;
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        if (mListener != null) {
            switch (view.getId()) {
//                case R.id.HomeButtonNewChat:
//                    mListener.onNewChat();
//                    break;
                case R.id.weatherButton1:
                    Log.v("hello", "hello");
                    mListener.NewWeather();
                    break;
                case R.id.tempChat1:
                    mListener.onNewChat();
                    break;
                case R.id.tempChat2:
                    mListener.onNewChat();
                    break;
                case R.id.tempChat3:
                    mListener.onNewChat();
                    break;
                default:
                    Log.wtf("", "Didn't expect to see me...");
            }
        }
    }

    public interface OnHomeFragmentInteractionListener {
        void onNewChat();
        void NewWeather();
    }
}
