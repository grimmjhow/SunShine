package br.com.example.android.sunshine.model.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.example.android.sunshine.app.R;
import br.com.example.android.sunshine.app.parse.WeatherDataParser;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForecastFragment extends Fragment {

    private ArrayAdapter<String> mForecastAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    //List just for Mock! =]
    private List<String> weekForecast = new ArrayList<String>(Arrays.asList(
            "Today - Sunny - 88/63"));

    public ForecastFragment() {
        // Required empty public constructor (If you say so!)
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId(); //get the ID!

        if(id == R.id.action_refresh) {//Is the same ID?
            FetchWeatherTask weatherTask = new FetchWeatherTask();

            weatherTask.execute("94043");


            try {
                //List<String> result = weatherTask.get(); //getting the result
                ListView list = (ListView) this.getActivity().findViewById(R.id.listview_forecast);
                mForecastAdapter = new ArrayAdapter(
                        this.getActivity(),
                        R.layout.list_item_forecast,
                        R.id.list_item_forecast_textview,
                        this.weekForecast
                );
                ListView listView = (ListView) this.getActivity().findViewById(R.id.listview_forecast);
                listView.setAdapter(mForecastAdapter);

            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment,menu); //inflate menu_item
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_blank, container, false);
        FetchWeatherTask fetchWeatherTask = new FetchWeatherTask();
        fetchWeatherTask.execute("94043");

        try{
            mForecastAdapter = new ArrayAdapter<String>(
                    //this context --> Means this all the information about this fragment in particular
                    getActivity(),
                    //list Item layout that will be used!
                    R.layout.list_item_forecast,
                    //ID of textView to populate
                    R.id.list_item_forecast_textview,
                    //Array with all the information to populate the list view
                    this.weekForecast);

            //casting to get ListView
            ListView listItem = (ListView) rootView.findViewById(R.id.listview_forecast);

            listItem.setAdapter(mForecastAdapter); //set the array adapter
            //Show a tosted messagem when a listItem was clicked =]
            listItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(view.getContext(),DetailActivity.class);
                    intent.putExtra(Intent.EXTRA_TEXT,mForecastAdapter.getItem(position));
                    startActivity(intent);
                }
            });

        }
        catch(Exception ex){
            ex.printStackTrace();
            Log.e("real forecast",ex.getMessage());
        }
        return rootView;
    }

    /**
     * Inner class!
     */
    public class FetchWeatherTask extends AsyncTask<String, Integer, List<String>> {
        public FetchWeatherTask() {
            super();
        }

        /**
         * Teste to get JSON weather data!
         */
        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
        private static final String WEATHER_KEY = "b435054bac5bc463a041e3d593521ee5";

        @Override
        protected List<String> doInBackground(String... params){
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                String defaultPostalcode = "94043";
                String localPostalcode = params[0];

                String mode = "json";
                String units = "metric";
                int numDays = 7;


                final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String FORMAT_PARAM = "mode";
                final String QUERY_PARAM = "q";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String API_KEY = "appid";

                Uri uri = Uri
                        .parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM,localPostalcode)
                        .appendQueryParameter(UNITS_PARAM,units)
                        .appendQueryParameter(DAYS_PARAM,Integer.toString(numDays))
                        .appendQueryParameter(API_KEY,FetchWeatherTask.WEATHER_KEY).build();

                StringBuilder openWeatherPath = new StringBuilder("http://api.openweathermap.org/data/2.5/forecast/daily?zip=");
                openWeatherPath
                        .append(localPostalcode)
                        .append("&mode=json&units=metric&cnt=7")
                        .append("&appid=")
                        .append(FetchWeatherTask.WEATHER_KEY);
                // just to check if URL was created right
                Log.i("URI_BUILDER",uri.toString());

                URL url = new URL(openWeatherPath.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    forecastJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    forecastJsonStr = null;
                }
                forecastJsonStr = buffer.toString();

                try{
                    Double maxTemperature = WeatherDataParser.getMaxTemperatureForDay(forecastJsonStr,0);
                    Log.i("JSON result",maxTemperature.toString());
                }catch(JSONException ex){
                    Log.e("JSON Error",ex.getMessage());
                    ex.printStackTrace();
                }

                Log.d("Json Information",forecastJsonStr); //shows the result!

                return getWeatherDataFromJson(forecastJsonStr,7);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                forecastJsonStr = null;
            } catch(JSONException ex){
                Log.e("JSON Exception",ex.getMessage());
                ex.printStackTrace();
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return new ArrayList<>();
        }

        @Override
        protected void onPostExecute(List<String> result) {
            if(result != null){
                mForecastAdapter.clear(); //need to clear the list to get new values!
                for(String r : result)
                    mForecastAdapter.add(r);
            }
        }

        /* The date/time conversion code is going to be moved outside the asynctask later,
               * so for convenience we're breaking it out into its own method now.
               */
        private String getReadableDateString(long time){
            // Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid date.
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(time);
        }

        /**
         * Prepare the weather high/lows for presentation.
         */
        private String formatHighLows(double high, double low) {
            // For presentation, assume the user doesn't care about tenths of a degree.
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);

            String highLowStr = roundedHigh + "/" + roundedLow;
            return highLowStr;
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
        }

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private List<String> getWeatherDataFromJson(String forecastJsonStr, int numDays)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DESCRIPTION = "main";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            // OWM returns daily forecasts based upon the local time of the city that is being
            // asked for, which means that we need to know the GMT offset to translate this data
            // properly.

            // Since this data is also sent in-order and the first day is always the
            // current day, we're going to take advantage of that to get a nice
            // normalized UTC date for all of our weather.

            Time dayTime = new Time();
            dayTime.setToNow();

            // we start at the day returned by local time. Otherwise this is a mess.
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            // now we work exclusively in UTC
            dayTime = new Time();

            String[] resultStrs = new String[numDays];
            for(int i = 0; i < weatherArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String day;
                String description;
                String highAndLow;

                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                // The date/time is returned as a long.  We need to convert that
                // into something human-readable, since most people won't read "1400356800" as
                // "this saturday".
                long dateTime;
                // Cheating to convert this to UTC time, which is what we want anyhow
                dateTime = dayTime.setJulianDay(julianStartDay+i);
                day = getReadableDateString(dateTime);

                // description is in a child array called "weather", which is 1 element long.
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

                highAndLow = formatHighLows(high, low);
                resultStrs[i] = day + " - " + description + " - " + highAndLow;
            }

            List<String> array = new ArrayList<>(Arrays.asList(resultStrs));
            return array;
        }
    }
}

