package group1.tcss450.uw.edu.a450groupone;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import group1.tcss450.uw.edu.a450groupone.utils.Weather;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class SelectWeatherCityFragment extends Fragment implements RecyclerViewAdapter.ItemClickListener,
                                                                    SearchView.OnQueryTextListener {

    WeatherFragment.OnWeatherFragmentInteractionListener weatheListener;

    RecyclerViewAdapter adapter;
    SearchView searchView;
    JSONArray preferedCities;


    public SelectWeatherCityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_select_weather_city, container, false);

        SharedPreferences prefs = getActivity().getSharedPreferences(
                getString(R.string.keys_shared_prefs),
                Context.MODE_PRIVATE);

        try {
            preferedCities = new JSONArray(
                            prefs.getString(
                            getString(R.string.keys_prefs_fave_cities), "[]"));
            ArrayList<String> cityNames = new ArrayList<>();

            for (int i = 0; i < preferedCities.length(); i++) {
                JSONObject city = preferedCities.getJSONObject(i);
                cityNames.add(city.getString(Weather.K_CITY));
            }

            // set up the RecyclerView
            RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.rvCities);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            adapter = new RecyclerViewAdapter(getContext(), cityNames);
            adapter.setClickListener(this::onItemClick);
            recyclerView.setAdapter(adapter);

            searchView = (SearchView) v.findViewById(R.id.searchBox);
            searchView.setActivated(true);
            searchView.setOnQueryTextListener(this);

            ImageButton b = (ImageButton) v.findViewById(R.id.mapButton);
            b.setOnClickListener(this::openMap);

            // clear history button set listener
            v.findViewById(R.id.clearHistoryButton)
                .setOnClickListener(this::clearCityHistory);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return v;
    }

    private void clearCityHistory(View v) {
        SharedPreferences prefs = getActivity().getSharedPreferences(
                getString(R.string.keys_shared_prefs),
                Context.MODE_PRIVATE);

        prefs.edit().putString(
                getString(R.string.keys_prefs_fave_cities), "[]").apply();
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(getActivity(), "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        getWeatherBySelection(position);
    }


    @Override
    public boolean onQueryTextSubmit(String zip) {
        // check it's valid input
        zip = zip.trim();
        if (zip.length() == 5) {
            getWeatherByZip(zip);
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private void getWeatherBySelection(int position) {
        SharedPreferences prefs = getActivity().getSharedPreferences(
                getString(R.string.keys_shared_prefs),
                Context.MODE_PRIVATE);

        try {
            // load coordinates in prefs
            prefs.edit().putString(getString(R.string.keys_prefs_selected_city_lat),
                    preferedCities.getJSONObject(position).getString(Weather.K_LAT)).apply();

            prefs.edit().putString(getString(R.string.keys_prefs_selected_city_lon),
                    preferedCities.getJSONObject(position).getString(Weather.K_LON)).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // let fragment know we just selkected the city
        prefs.edit().putBoolean(getString(R.string.keys_prefs_selected_city),
                true).apply();

        getFragmentManager().popBackStack();
    }

    private void getWeatherByZip(String zipStr) {
        // provide zip
        getActivity().getSharedPreferences(
                getString(R.string.keys_shared_prefs),
                Context.MODE_PRIVATE).edit()
                .putString(getString(R.string.keys_prefs_selected_zip), zipStr)
                .apply();

        getActivity().getSharedPreferences(
                getString(R.string.keys_shared_prefs),
                Context.MODE_PRIVATE).edit()
                .putBoolean(getString(R.string.keys_prefs_selected_city), true)
                .apply();

        getFragmentManager().popBackStack();

    }

    private void openMap(View v) {
        weatheListener.onMapButtonClicked();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof WeatherFragment.OnWeatherFragmentInteractionListener) {
            weatheListener = (WeatherFragment.OnWeatherFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnWeatherFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        weatheListener = null;
    }


}
