package group1.tcss450.uw.edu.a450groupone.model;

import org.json.JSONObject;

public class WeatherBundle {
    private JSONObject currentWeather;
    private JSONObject hourlyDailyWeather;


    public JSONObject getHourlyDailyWeather() {
        return hourlyDailyWeather;
    }

    public JSONObject getCurrentWeather() {
        return currentWeather;
    }

    public void setHourlyDailyWeather(JSONObject hourlyDailyWeather) {
        this.hourlyDailyWeather = hourlyDailyWeather;
    }

    public void setCurrentWeather(JSONObject currentWeather) {
        this.currentWeather = currentWeather;
    }
}
