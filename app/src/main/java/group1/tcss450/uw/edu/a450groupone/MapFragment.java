package group1.tcss450.uw.edu.a450groupone;


import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;

import group1.tcss450.uw.edu.a450groupone.utils.Weather;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements GoogleMap.OnMapClickListener{

    MapView mMapView;
    private GoogleMap googleMap;
    private double mLat, mLng;
    Marker mapMarker;

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_map, container, false);

        MapFragment thisFrag = this;

//        SupportMapFragment mapFragment = (SupportMapFragment) v.f(R.id.map);
//        mapFragment.getMapAsync(this);
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);


        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                Location currentLocation = null;
                LatLng mapLatLng = null;
                SharedPreferences prefs = getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);

                String lat = prefs.getString(getString(R.string.keys_prefs_selected_city_lat), "_");
                String lon = prefs.getString(getString(R.string.keys_prefs_selected_city_lon), "_");

                googleMap = mMap;
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.getUiSettings().setAllGesturesEnabled(true);
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    googleMap.setMyLocationEnabled(true);

                    currentLocation = LocationServices.FusedLocationApi.getLastLocation(
                            ((NavigationActivity)getActivity()).getmGoogleApiClient());

                }

                if (currentLocation != null) {
                    mapLatLng = new LatLng(currentLocation.getLatitude(),
                            currentLocation.getLongitude());
                    Log.i("MAP_CURRENT_LOCATION", currentLocation.toString());
                } else {
                    mapLatLng = new LatLng(Double.valueOf(lat), Double.valueOf(lon));
                }

                // For dropping a marker at a point on the Map
                if (mapLatLng != null) { // location was obtained
                    mapMarker = googleMap.addMarker(new MarkerOptions().position(mapLatLng).title("Show weather of this location"));//.snippet("Marker Description"));
                    // For zooming automatically to the location of the marker
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(mapLatLng).zoom(12).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
                googleMap.setOnMapClickListener(thisFrag);
            }
        });

        // done button listener
        v.findViewById(R.id.doneButton).setOnClickListener(this::goToWeather);

        return v;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mapMarker.setPosition(latLng);
//                = googleMap.addMarker(new MarkerOptions()
//                .position(latLng)
//                .title(""));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));

        setCoordsInPrefs(latLng);
    }

    private void setCoordsInPrefs(LatLng latLng) {
        SharedPreferences prefs = getActivity().getSharedPreferences(
                getString(R.string.keys_shared_prefs),
                Context.MODE_PRIVATE);

        // load coordinates in prefs
        prefs.edit().putString(getString(R.string.keys_prefs_selected_city_lat),
               String.valueOf(latLng.latitude) ).apply();

        prefs.edit().putString(getString(R.string.keys_prefs_selected_city_lon),
               String.valueOf(latLng.longitude) ).apply();
        prefs.edit().putString(getString(R.string.keys_prefs_selected_zip),
                "_").apply();

        // let fragment know we just selkected the city
        prefs.edit().putBoolean(getString(R.string.keys_prefs_selected_city),
                true).apply();
    }

    private void goToWeather(View v) {
        getActivity().getSupportFragmentManager().popBackStack();
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


}
