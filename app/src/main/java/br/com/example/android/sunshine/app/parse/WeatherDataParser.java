package br.com.example.android.sunshine.app.parse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Desenvolvimento on 23/10/2016.
 */

public class WeatherDataParser {

    public static double getMaxTemperatureForDay(String weatherJsonStr,int dayIndex) throws JSONException{
        JSONObject object = new JSONObject(weatherJsonStr);
        JSONArray array = object.getJSONArray("list");
        JSONObject dayInfo = array.getJSONObject(dayIndex);
        double maxTemperature = dayInfo.getJSONObject("temp").getDouble("max");
        return maxTemperature;
    }
}
