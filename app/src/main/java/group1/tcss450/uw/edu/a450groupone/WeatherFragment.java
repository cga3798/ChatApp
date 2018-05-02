package group1.tcss450.uw.edu.a450groupone;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

import group1.tcss450.uw.edu.a450groupone.utils.Weather;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WeatherFragment.OnWeatherFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class WeatherFragment extends Fragment implements View.OnClickListener {

    private OnWeatherFragmentInteractionListener mListener;

    //private TextView city, weather, currentTemp, weatherIcon;



    private Bundle data;


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

        getWeatherData(v);

        return v;
    }

    private void makeHourlyScrollView(View v) {
        LinearLayout hourlyBar = v.findViewById(R.id.weatherHourlyBar);

        for (int i = 0; i < 10; i++) {
            hourlyBar.addView(makeHourContainer());
        }
    }

    private View makeHourContainer() {
        //LinearLayout dayWeatherView = new LinearLayout(getContext());
        View v = LayoutInflater.from(getContext())
                .inflate(R.layout.hour_weather_box, null, false);
        // fill data
        TextView tv = v.findViewById(R.id.weatherTextViewTime);

        tv = v.findViewById(R.id.weatherTextViewIcon);

        tv = v.findViewById(R.id.weatherTextViewTemp);


        return v;
    }

    private void makeDailyScrollView(View v) {
        TableLayout table = v.findViewById(R.id.weatherDailyTable);

        for (int i = 0; i < 10; i++) {
            table.addView(makeDayRow());
        }
    }

    private View makeDayRow() {
        //TableRow tr = new TableRow(getContext());
        View v = LayoutInflater.from(getContext())
                .inflate(R.layout.day_weather_row, null, false);
        // fill values
//        TextView cell = v.findViewById(R.id.weatherTextViewRowDay);
//        cell.setText("day");
//        cell = v.findViewById(R.id.weatherTextViewRowIcon);
//        cell.setText("icon");
//        cell = v.findViewById(R.id.weatherTextViewRowMaxTemp);
//        cell.setText("max");
//        cell = v.findViewById(R.id.weatherTextViewRowMinTemp);
//        cell.setText("min");

        return v;
    }

    private void getWeatherData(View fragmentView) {
        Weather.RetrieveData asyncTask = new Weather.RetrieveData(R.id.fragmentWeather ,new Weather.AsyncResponse() {
            public void processFinish(Bundle args) {
                data = args;
                setWeatherData(fragmentView);
            }
        });
        // TODO:_________ provide selected city coordinates
        asyncTask.execute("47.25288","-122.44429");
    }

    private void makeTopWeatherData(View v) {
        Typeface weatherFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/weathericons-regular-webfont.ttf");

        TextView city = v.findViewById(R.id.weatherCityTextview);
        TextView weather = v.findViewById(R.id.weatherDesc);
        TextView currentTemp = v.findViewById(R.id.weatherTemp);
        TextView weatherIcon = v.findViewById(R.id.weatherIcon);
        weatherIcon.setTypeface(weatherFont);

        city.setText(data.getString(Weather.K_CITY));

        weather.setText(data.getString(Weather.K_WEATHER_DESC));
        currentTemp.setText(data.getString(Weather.K_CURRENT_TEMP));
        weatherIcon.setText(Html.fromHtml(data.getString(Weather.K_ICON)));
    }

    private void makeBottomWeatherData(View v) {
        //updatedField.setText(weather_updatedOn);
        //humidity_field.setText("Humidity: "+weather_humidity);
        //pressure_field.setText("Pressure: "+weather_pressure);

    }

    private void setWeatherData(View v) {
        Log.d("WEATHER_FRAG", "setting data");


        makeTopWeatherData(v);
        makeHourlyScrollView(v);
        makeDailyScrollView(v);
        makeBottomWeatherData(v);

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
