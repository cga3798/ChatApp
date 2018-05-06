package group1.tcss450.uw.edu.a450groupone;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Layout;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.sql.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import group1.tcss450.uw.edu.a450groupone.utils.Weather;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WeatherFragment.OnWeatherFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class WeatherFragment extends Fragment implements View.OnClickListener {

    private static final float TEXT_VIEW_WEIGHT = .5f;
    //private static final int TOTAL_HOURS_TO_DISPLAY = 24;


    private OnWeatherFragmentInteractionListener mListener;

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

    private void setWeatherData(View v) {
        Log.d("WEATHER_FRAG", "setting data");
        makeTopWeatherData(v);
        makeHourlyScrollView(v);
        makeDailyScrollView(v);
        makeBottomWeatherData(v);
    }

    private void makeTopWeatherData(View v) {
        Typeface weatherFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/weathericons-regular-webfont.ttf");

        TextView city = v.findViewById(R.id.weatherCityTextview);
        TextView weather = v.findViewById(R.id.weatherDesc);
        TextView currentTemp = v.findViewById(R.id.weatherTemp);
        TextView weatherIcon = v.findViewById(R.id.weatherIcon);
        weatherIcon.setTypeface(weatherFont);
        TextView today = v.findViewById(R.id.weatherToday);
        TextView maxmin = v.findViewById(R.id.weatherTodayMaxMinTemp);

        city.setText(data.getString(Weather.K_CITY));
        weather.setText(data.getString(Weather.K_WEATHER_DESC));
        currentTemp.setText(data.getString(Weather.K_CURRENT_TEMP));
        weatherIcon.setText(Html.fromHtml(data.getString(Weather.K_ICON)));
        today.setText(data.getString(Weather.K_UPDATEDON) + " Today");
        maxmin.setText(data.getString(Weather.K_MAX_TEMP) + "             "
                    + data.getString(Weather.K_MIN_TEMP));

    }

    private void makeHourlyScrollView(View v) {
        LinearLayout hourlyBar = v.findViewById(R.id.weatherHourlyBar);
        JSONArray hourlyList = null;
        try {
            hourlyList = new JSONArray(data.getString(Weather.K_HOURLY_DAILY_LIST));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("ha");
        sdf.setTimeZone(TimeZone.getTimeZone(Weather.GMT_PACIFIC));

        for (int i = 0; i < hourlyList.length(); i++) {
            try {
                JSONObject hourJson = hourlyList.getJSONObject(i);
                hourlyBar.addView(makeHourContainer(hourJson, sdf));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private View makeHourContainer(JSONObject hourJson, SimpleDateFormat sdf) throws JSONException {

        View v = LayoutInflater.from(getContext())
                .inflate(R.layout.hour_weather_box, null, false);

        JSONObject weather = hourJson.getJSONArray("weather").getJSONObject(0);
        JSONObject main = hourJson.getJSONObject("main");

        Typeface weatherFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/weathericons-regular-webfont.ttf");

        // fill data
        TextView tv = v.findViewById(R.id.weatherTextViewTime);

        tv.setText(sdf.format(new Date(hourJson.getLong("dt") * 1000)));
        tv = v.findViewById(R.id.weatherTextViewIcon);
        tv.setTypeface(weatherFont);
        tv.setText(Html.fromHtml(
                Weather.setWeatherIcon(
                        weather.getInt("id"),
                        data.getLong(Weather.K_SUNRISE_LONG),
                        data.getLong(Weather.K_SUNSET_LONG))
        ));
        tv = v.findViewById(R.id.weatherTextViewTemp);
        tv.setText(String.valueOf(main.getInt("temp")));

        return v;
    }

    private void makeDailyScrollView(View v) {
        TableLayout table = v.findViewById(R.id.weatherDailyTable);
        ArrayList<Bundle> days = new ArrayList<>();

        try {
            //final ArrayList<Bundle> days = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("dd");
            sdf.setTimeZone(TimeZone.getTimeZone(Weather.GMT_PACIFIC));
            DateFormat df = new SimpleDateFormat("EEEE");
            df.setTimeZone(TimeZone.getTimeZone(Weather.GMT_PACIFIC));
            JSONArray dailyList = new JSONArray(data.getString(Weather.K_HOURLY_DAILY_LIST));
            processHoursJSON(dailyList, days, sdf, df);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (Bundle b : days) {
            table.addView(makeDayRow(b));
        }

    }

    private void processHoursJSON(JSONArray hoursData, ArrayList<Bundle> days, DateFormat sdf, DateFormat df) throws JSONException {

        //ArrayList<Bundle> days = new ArrayList<>();
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        int sumIcon = 0;
        int count = 0;

        for (int i = 1; i < hoursData.length(); i++) {

            JSONObject hourJson = hoursData.getJSONObject(i);
            JSONObject prevHourJson = hoursData.getJSONObject(i-1);

            JSONObject main = hourJson.getJSONObject("main");
            JSONObject weather = hourJson.getJSONArray("weather").getJSONObject(0);
            String currDay = sdf.format(new Date(hourJson.getLong("dt") * 1000));
            String prevDay = sdf.format(new Date(prevHourJson.getLong("dt") * 1000));

            if (Integer.valueOf(currDay) != Integer.valueOf(prevDay) ) {
                // store vals
                String dayOfWeek = df.format(new Date(prevHourJson.getLong("dt") * 1000));

                // if day is not the same as today dont make row
                // there might not be enough data to calculate min-max temp
                if( ! dayOfWeek.equals(data.getString(Weather.K_UPDATEDON))) {

                    int icon = 8;
                    if (count > 0) { // data for day not available
                        icon = sumIcon / count;
                        Bundle b = new Bundle();
                        b.putString(Weather.K_DAY_OF_WEEK, dayOfWeek);
                        b.putInt(Weather.K_ICON, icon);
                        b.putString(Weather.K_MAX_TEMP, String.valueOf(max));
                        b.putString(Weather.K_MIN_TEMP, String.valueOf(min));
                        days.add(b);
                    } else { // say data not available
                        Bundle b = new Bundle();
                        b.putString(Weather.K_DAY_OF_WEEK, dayOfWeek);
                        b.putInt(Weather.K_ICON, icon);
                        b.putString(Weather.K_MAX_TEMP, getString(R.string.data_not_available));
                        b.putString(Weather.K_MIN_TEMP, "");
                        days.add(b);
                    }
                }
                // reset for new day
                max = Integer.MIN_VALUE;
                min = Integer.MAX_VALUE;
                sumIcon = 0;
                count = 0;
            }
            max = (main.getInt("temp_max") > max) ? main.getInt("temp_max") : max;
            min = (main.getInt("temp_min") < min) ? main.getInt("temp_min") : min;

            sumIcon += weather.getInt("id");
            count++;
        }
    }

    private View makeDayRow(Bundle b) {//String day, int icon, String maxtemp, String minTemp) {
        //TableRow tr = new TableRow(getContext());
        View v = LayoutInflater.from(getContext())
                .inflate(R.layout.day_weather_row, null, false);

        Typeface weatherFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/weathericons-regular-webfont.ttf");

        // fill values
        TextView cell = v.findViewById(R.id.weatherTextViewRowDay);
        cell.setText(b.getString(Weather.K_DAY_OF_WEEK));
        cell = v.findViewById(R.id.weatherTextViewRowIcon);
        cell.setTypeface(weatherFont);
        cell.setText(Html.fromHtml(
                Weather.setWeatherIcon(
                        b.getInt(Weather.K_ICON),0,0)

        ));
        cell = v.findViewById(R.id.weatherTextViewRowMaxTemp);
        cell.setText(b.getString(Weather.K_MAX_TEMP));
        cell = v.findViewById(R.id.weatherTextViewRowMinTemp);
        cell.setText(b.getString(Weather.K_MIN_TEMP));
        return v;
    }


    private void makeBottomWeatherData(View v) {
        TableLayout table = v.findViewById(R.id.weatherDailyTable);
        // header
        TableRow aRow = getRowWithStyle();
        aRow.addView(makeTextView(getString(R.string.sunrise), true));
        aRow.addView(makeTextView(getString(R.string.sunset), true));
        table.addView(aRow);
        //data
        aRow = getRowWithStyle();
        aRow.addView(makeTextView(data.getString(Weather.K_SUNRISE), false));
        aRow.addView(makeTextView(data.getString(Weather.K_SUNSET), false));
        table.addView(aRow);

        // header
        aRow = getRowWithStyle();
        aRow.addView(makeTextView(getString(R.string.wind), true));
        aRow.addView(makeTextView(getString(R.string.humidity), true));
        table.addView(aRow);
        //data
        aRow = getRowWithStyle();
        String windInfo = data.getString(Weather.K_WIND_DIR)
                        + "  " + data.getString(Weather.K_WIND_SPEED);
        aRow.addView(makeTextView(windInfo,false));
        aRow.addView(makeTextView(data.getString(Weather.K_HUMIDITY), false));
        table.addView(aRow);

    }

    private TextView makeTextView(String text, boolean header) {
        TextView tv = new TextView(this.getContext());
        tv.setText(text);
        tv.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                TEXT_VIEW_WEIGHT
        ));

        if (header) {
            tv.setTextSize(getResources().getDimensionPixelSize(R.dimen.weather_header_text_size));
        } else {
            tv.setTextSize(getResources().getDimensionPixelSize(R.dimen.weather_data_text_size));
        }

        return tv;
    }

    private TableRow getRowWithStyle() {
        TableRow row = new TableRow(this.getContext());
        row.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
        ));

        int dp = getResources().getDimensionPixelSize(R.dimen.row_padding);
        row.setPadding(dp, dp, dp, dp);

        return row;
    }


    public void onSelectCClicked(View v) {
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
