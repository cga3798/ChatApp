package group1.tcss450.uw.edu.a450groupone.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import group1.tcss450.uw.edu.a450groupone.MainActivity;

public class MyLocation implements LocationListener{

    LocationManager mLocationManager;
    Context context;
    TextView view;

    String latitude = "";
    String longitude = "";

    public MyLocation(Context c, TextView v) {
        context = c;
        view = v;
        try {
            mLocationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public boolean isReady() {
        return !(latitude.isEmpty() && longitude.isEmpty());
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    @Override
    public void onLocationChanged(Location location) {
        //view.setText("Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude());
        if (location.getAccuracy() > .50f) {
            latitude = String.valueOf(location.getLatitude());
            longitude = String.valueOf(location.getLongitude());
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {
        Toast.makeText(context, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }
}
