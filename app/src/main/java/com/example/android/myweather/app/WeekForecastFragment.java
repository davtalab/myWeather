package com.example.android.myweather.app;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class WeekForecastFragment extends Fragment {
    public static int LOG_LEVEL = 0;

    public static boolean ERROR = LOG_LEVEL > 0;
    public static boolean WARN = LOG_LEVEL > 1;
    public static boolean INFO = LOG_LEVEL > 2;
    public static boolean DEBUG = LOG_LEVEL > 3;
    public static boolean VERBOSE = LOG_LEVEL > 4;

    public final String LOG_TAG = WeekForecastFragment.class.getSimpleName();

    public WeekForecastFragment() {
    }

    private ArrayAdapter<String> weekForecastAdapter;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.week_forecast_fragment, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            new RetrieveWeatherInfoTask().execute(new String[]{"Tehran"});
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ArrayList<String> fake_week_forecast=new ArrayList<>();
        fake_week_forecast.add("Today-Sunny-88/63");
        fake_week_forecast.add("Tomorrow-Foggy-70/46");
        fake_week_forecast.add("Weds-Cloudy-72/63");
        fake_week_forecast.add("Thurs-Rainy-64/51");
        fake_week_forecast.add("Fri-Foggy-70/46");
        fake_week_forecast.add("Sat-Sunny-76/68");
        weekForecastAdapter =
                new ArrayAdapter<String>(getActivity(),
                        R.layout.list_item_forecast,
                        R.id.list_item_forecast_textview,
                        fake_week_forecast);
        ListView lv = (ListView) rootView.findViewById(R.id.listview_forecast);
        lv.setAdapter(weekForecastAdapter);
        return rootView;
    }
    public class RetrieveWeatherInfoTask extends AsyncTask<String,Void,String[]> {

        protected String[] doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;
            String[] week_forecast = null;
            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast

                //https://www.myawesomesite.com/turtles/types?type=1&sort=relevance#section-name

                Uri.Builder uri_builder = new Uri.Builder();
                uri_builder.scheme("http")
                        .authority("api.openweathermap.org")
                        .appendPath("data")
                        .appendPath("2.5")
                        .appendPath("forecast")
                        .appendPath("daily")
                        .appendQueryParameter("q", params[0])
                        .appendQueryParameter("mode", "json")
                        .appendQueryParameter("units", "metric")
                        .appendQueryParameter("cnt", "7");

                URL url = new URL(uri_builder.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
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
                    return null;
                }
                forecastJsonStr = buffer.toString();

                try {
                    week_forecast = DataParser.getWeatherDataFromJson(forecastJsonStr, 7);
                } catch(Exception e) {
                    if(ERROR) Log.e(LOG_TAG, e.getMessage());
                }

            } catch (IOException e) {
                if(ERROR) Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        if(ERROR) Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return week_forecast;
        }
        protected void onPostExecute(String[] week_forecast) {
            if(week_forecast!=null) {
                weekForecastAdapter.clear();
                for(String day_forcast:week_forecast){
                    weekForecastAdapter.add(day_forcast);
                }
            }
        }
    }
}
