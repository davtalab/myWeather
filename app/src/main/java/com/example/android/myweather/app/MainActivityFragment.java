package com.example.android.myweather.app;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ArrayList<String> weekForecast=new ArrayList<>();
        weekForecast.add("Today-Sunny-88/63");
        weekForecast.add("Tomorrow-Foggy-70/46");
        weekForecast.add("Weds-Cloudy-72/63");
        weekForecast.add("Thurs-Rainy-64/51");
        weekForecast.add("Fri-Foggy-70/46");
        weekForecast.add("Sat-Sunny-76/68");
        ArrayAdapter<String> weekForecastAdapter =
                new ArrayAdapter<String>(getActivity(),
                        R.layout.list_item_forecast,
                        R.id.list_item_forecast_textview,
                        weekForecast);
        ListView lv = (ListView) rootView.findViewById(R.id.listview_forecast);
        lv.setAdapter(weekForecastAdapter);
        return rootView;
    }
}
