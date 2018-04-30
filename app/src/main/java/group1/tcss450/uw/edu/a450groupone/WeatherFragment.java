package group1.tcss450.uw.edu.a450groupone;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import group1.tcss450.uw.edu.a450groupone.model.Weather;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WeatherFragment.OnWeatherFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class WeatherFragment extends Fragment implements View.OnClickListener {

    private OnWeatherFragmentInteractionListener mListener;

    private TextView city, weather, currentTemp;

    public WeatherFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_weather, container, false);
        //Bundle args = new Bundle();
        Button b = v.findViewById(R.id.selectCityButton);
        b.setOnClickListener(this::onSelectCClicked);

        city = v.findViewById(R.id.weatherCityTextview);
        weather = v.findViewById(R.id.weatherDesc);
        currentTemp = v.findViewById(R.id.weatherTemp);

        setWeatherData();

        return v;
    }

    private void setWeatherData() {
        Weather.RetrieveData asyncTask = new Weather.RetrieveData(R.id.fragmentWeather ,new Weather.AsyncResponse() {
            public void processFinish(Bundle args) {
                Log.d("WEATHER_FRAG", "setting data");
                city.setText(args.getString(Weather.K_CITY));
                //updatedField.setText(weather_updatedOn);
                weather.setText(args.getString(Weather.K_WEATHER_DESC));
                currentTemp.setText(args.getString(Weather.K_CURRENT_TEMP));
                //humidity_field.setText("Humidity: "+weather_humidity);
                //pressure_field.setText("Pressure: "+weather_pressure);
                //weatherIcon.setText(Html.fromHtml(weather_iconText));

            }
        });
        asyncTask.execute("47.25288","-122.44429");
    }

    public void onSelectCClicked(View v) {
        Log.d("In weather: ", "select city clicked");
        mListener.onSelectCityButtonClicked();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnWeatherFragmentInteractionListener) {
            mListener = (OnWeatherFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnWeatherFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        mListener.onSelectCityButtonClicked();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnWeatherFragmentInteractionListener {
        void onSelectCityButtonClicked();
    }
}
