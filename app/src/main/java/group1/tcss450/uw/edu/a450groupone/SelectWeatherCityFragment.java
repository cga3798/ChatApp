package group1.tcss450.uw.edu.a450groupone;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class SelectWeatherCityFragment extends Fragment implements RecyclerViewAdapter.ItemClickListener {

    RecyclerViewAdapter adapter;

    public SelectWeatherCityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_select_weather_city, container, false);

        // this is how to programmatically add cities
        ArrayList<String> animalNames = new ArrayList<>();
        animalNames.add("Seattle");
        animalNames.add("Los Angles");
        animalNames.add("Tacoma");

        // set up the RecyclerView
        RecyclerView recyclerView = v.findViewById(R.id.rvCities);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new RecyclerViewAdapter(getContext(), animalNames);
        adapter.setClickListener(this::onItemClick);
        recyclerView.setAdapter(adapter);
        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onItemClick(View view, int position) {
       Toast.makeText(getActivity(), "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
