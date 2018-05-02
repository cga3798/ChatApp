package group1.tcss450.uw.edu.a450groupone.model;

import org.json.JSONObject;

public class WeatherBundle {
    private JSONObject currentWeather;
    private JSONObject dailyWeather;
    private JSONObject hourlyWeather;

    public JSONObject getDailyWeather() {
        return dailyWeather;
    }

    public JSONObject getHourlyWeather() {
        return hourlyWeather;
    }

    public JSONObject getCurrentWeather() {
        return currentWeather;
    }

    public void setDailyWeather(JSONObject dailyWeather) {
        this.dailyWeather = dailyWeather;
    }

    public void setHourlyWeather(JSONObject hourlyWeather) {
        this.hourlyWeather = hourlyWeather;
    }

    public void setCurrentWeather(JSONObject currentWeather) {
        this.currentWeather = currentWeather;
    }
}
