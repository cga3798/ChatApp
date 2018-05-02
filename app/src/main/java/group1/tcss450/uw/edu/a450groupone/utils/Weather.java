package group1.tcss450.uw.edu.a450groupone.utils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import group1.tcss450.uw.edu.a450groupone.R;
import group1.tcss450.uw.edu.a450groupone.model.WeatherBundle;

public class Weather {

    public static final String K_CITY = "city";
    public static final String K_WEATHER_DESC = "weatherDesc";
    public static final String K_CURRENT_TEMP = "currentTemp";
    public static final String K_ICON = "icon";
    public static final String K_HUMIDITY = "humidity";
    public static final String K_PRESSURE = "pressure";
    public static final String K_UPDATEDON = "updatedOn";
    public static final String K_MAX_TEMP = "maxTemp";
    public static final String K_MIN_TEMP = "minTemp";
    public static final String K_SUNRISE = "sunrise";
    public static final String K_SUNSET = "sunset";


    private static final int DAY_COUNT = 10;

    private static final String OPEN_WEATHER_CURRENT_URL =
            "http://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&units=metric"; // by lat lon
    //"api.openweathermap.org/data/2.5/weather?q=%s";  // by city (to use city id-> "...?id=%d" )
    private static final String OPEN_WEATHER_HOURLY_URL =
            "http://api.openweathermap.org/data/2.5/forecast?lat=%s&lon=%s&units=metric"; // forecast ( 5 days every 3 hrs)
    private static final String OPEN_WEATHER_DAILY_URL =
            "http://api.openweathermap.org/data/2.5/forecast/daily?lat=%s&lon=%s&units=metric&cnt=%d"; // daily (up to 16 days)

    // units can be (metric, imperial) -> (Celsius, Fahrenheit)


    private static final String OPEN_WEATHER_MAP_API = "ffd45d2019792da36a10c13555d5c15c";


    public static class RetrieveData extends AsyncTask<String, Void, WeatherBundle> {

        private AsyncResponse delegate = null;//Call back interface
        /* The request for data is coming from HomeFragment or weatherFragment*/
        private int source;

        public RetrieveData(int frag, AsyncResponse asyncResponse) {
            delegate = asyncResponse;//Assigning call back interfacethrough constructor
            source = frag;
        }

        // TODO : can modify to use two params if we get by lat/lon OR one param by city id
        @Override
        protected WeatherBundle doInBackground(String... params) {

            WeatherBundle weatherBundle = new WeatherBundle();
//            JSONObject jsonWeather = null;
//            JSONObject jsonDaily = null;
            try {
                weatherBundle.setCurrentWeather(getCurrentWeatherJSON(params[0], params[1]));
                //jsonDaily = getWeatherJSON(params[0], params[1]);
            } catch (Exception e) {
                Log.d("Error", "Cannot process JSON results", e);
            }

            return weatherBundle;
        }

        @Override
        protected void onPostExecute(WeatherBundle weatherBundle) {
            Bundle b = new Bundle();

            if (source == R.id.fragmentWeather) {
                // get detailed data
                loadWeatherFragmentData(weatherBundle, b);
            } else if (source == R.id.fragmentHome) {
                // get simple data
                loadHomeFragmentData(weatherBundle.getCurrentWeather(), b);
            } else {
                Log.e("WEATHER_ASYNC", "This shouldn't happen!");
            }

            //delegate.processFinish(city, description, temperature, humidity, pressure, updatedOn, iconText, ""+ (json.getJSONObject("sys").getLong("sunrise") * 1000));
            delegate.processFinish(b);

        }

        private static void loadWeatherFragmentData(WeatherBundle weatherBundle, Bundle b) {
            JSONObject simpleWeatherJSON = weatherBundle.getCurrentWeather();
            JSONObject dailyWeatherJSON = weatherBundle.getDailyWeather();
            JSONObject hourlyWeatherJSON = weatherBundle.getHourlyWeather();



            parseCurrentWeatherJSON(simpleWeatherJSON, b);

        }
    }

    private static void parseCurrentWeatherJSON(JSONObject simpleWeatherJSON, Bundle b) {
        try {
            if (simpleWeatherJSON != null) {

                JSONObject details = simpleWeatherJSON.getJSONArray("weather").getJSONObject(0);
                JSONObject main = simpleWeatherJSON.getJSONObject("main");
                JSONObject sys = simpleWeatherJSON.getJSONObject("sys");
                DateFormat df = DateFormat.getDateTimeInstance();


                String city = simpleWeatherJSON.getString("name").toUpperCase(Locale.US) + ", " + simpleWeatherJSON.getJSONObject("sys").getString("country");
                String description = details.getString("description").toUpperCase(Locale.US);
                String temperature = String.valueOf(main.getInt("temp")) + "°";
                String humidity = main.getString("humidity") + "%";
                String pressure = main.getString("pressure") + " hPa";
                String updatedOn = df.format(new Date(simpleWeatherJSON.getLong("dt") * 1000));
                String iconText = setWeatherIcon(details.getInt("id"),
                        sys.getLong("sunrise") * 1000,
                        sys.getLong("sunset") * 1000);
                String maxTemp = String.valueOf(main.getInt("temp_max"));
                String minTemp = String.valueOf(main.getInt("temp_min"));
                String sunrise = df.format(new Date(sys.getLong("sunrise") * 1000));
                String sunset = df.format(new Date(sys.getLong("sunset") * 1000));

                b.putString(K_CITY, city);
                b.putString(K_WEATHER_DESC, description);
                b.putString(K_CURRENT_TEMP, temperature);
                b.putString(K_ICON, iconText);
                b.putString(K_HUMIDITY, humidity);
                b.putString(K_PRESSURE, pressure);
                b.putString(K_UPDATEDON, updatedOn);
                b.putString(K_MAX_TEMP, maxTemp);
                b.putString(K_MIN_TEMP, minTemp);
                b.putString(K_SUNRISE, sunrise);
                b.putString(K_SUNSET, sunset);
            }
        } catch (JSONException e) {
            Log.e("WEATHER_FRAG", "Cannot process JSON results", e);
        }
    }




    private static void loadHomeFragmentData(JSONObject json, Bundle b) {
        try {
            if (json != null) {

                JSONObject details = json.getJSONArray("weather").getJSONObject(0);
                JSONObject main = json.getJSONObject("main");

                String city = json.getString("name").toUpperCase(Locale.US) + ", " + json.getJSONObject("sys").getString("country");
                String description = details.getString("description").toUpperCase(Locale.US);
                String temperature = String.valueOf(main.getInt("temp")) + "°";
                String iconText = setWeatherIcon(details.getInt("id"),
                        json.getJSONObject("sys").getLong("sunrise") * 1000,
                        json.getJSONObject("sys").getLong("sunset") * 1000);

                b.putString(K_CITY, city);
                b.putString(K_WEATHER_DESC, description);
                b.putString(K_CURRENT_TEMP, temperature);
                b.putString(K_ICON, iconText);
            }
        } catch (JSONException e) {
            Log.e("WEATHER_FRAG", "Cannot process JSON results", e);
        }
    }

    public static JSONObject getCurrentWeatherJSON(String lat, String lon) {
        try {
            URL url = new URL(String.format(OPEN_WEATHER_CURRENT_URL, lat, lon));
            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();

            connection.addRequestProperty("x-api-key", OPEN_WEATHER_MAP_API);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp = "";
            while ((tmp = reader.readLine()) != null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            // This value will be 404 if the request was not
            // successful
            if (data.getInt("cod") != 200) {
                return null;
            }

            return data;
        } catch (Exception e) {
            return null;
        }
    }


    public static JSONObject getDailyWeatherJSON(String lat, String lon) {
        try {
            URL url = new URL(String.format(OPEN_WEATHER_DAILY_URL, lat, lon, DAY_COUNT));
            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();

            connection.addRequestProperty("x-api-key", OPEN_WEATHER_MAP_API);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp = "";
            while ((tmp = reader.readLine()) != null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            // This value will be 404 if the request was not
            // successful
            if (data.getInt("cod") != 200) {
                return null;
            }

            return data;
        } catch (Exception e) {
            return null;
        }
    }

    public static JSONObject getHourlyWeatherJSON(String lat, String lon) {
        try {
            URL url = new URL(String.format(OPEN_WEATHER_HOURLY_URL, lat, lon));
            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();

            connection.addRequestProperty("x-api-key", OPEN_WEATHER_MAP_API);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp = "";
            while ((tmp = reader.readLine()) != null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            // This value will be 404 if the request was not
            // successful
            if (data.getInt("cod") != 200) {
                return null;
            }

            return data;
        } catch (Exception e) {
            return null;
        }
    }

    public int celsiusToFarenheit(int celsius) {
        return (int) Math.floor(celsius * 1.8 + 32);
    }

    public static String setWeatherIcon(int actualId, long sunrise, long sunset) {
        int id = actualId / 100;
        String icon = "";
        if (actualId == 800) {
            long currentTime = new Date().getTime();
            if (currentTime >= sunrise && currentTime < sunset) {
                icon = "&#xf00d;";
            } else {
                icon = "&#xf02e;";
            }
        } else {
            switch (id) {
                case 2:
                    icon = "&#xf01e;";
                    break;
                case 3:
                    icon = "&#xf01c;";
                    break;
                case 7:
                    icon = "&#xf014;";
                    break;
                case 8:
                    icon = "&#xf013;";
                    break;
                case 6:
                    icon = "&#xf01b;";
                    break;
                case 5:
                    icon = "&#xf019;";
                    break;
            }
        }
        return icon;
    }


    public interface AsyncResponse {
        // processFinish(String output1, String output2, String output3, String output4, String output5, String output6, String output7, String output8);
        void processFinish(Bundle b);
    }

}
