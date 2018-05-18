package group1.tcss450.uw.edu.a450groupone;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/*
 * Navigation fragment holds the currently displayed
 * screen overlaying it with a toolbar and clickable nav menu.
 */

public class NavigationActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        ChatFragment.OnChatFragmentInteractionListener,
        WeatherFragment.OnWeatherFragmentInteractionListener,
        HomeFragment.OnHomeFragmentInteractionListener,
        FriendFragment.OnFriendFragmentInteractionListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private GoogleApiClient mGoogleApiClient;
    private static final int MY_PERMISSIONS_LOCATIONS = 814;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;

    private HomeFragment homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        MainActivity.mainActivity.finish();

        // location services ________________________
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mLocationRequest = LocationRequest.create();
        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // end location services _______________________________

        homeFragment = new HomeFragment();
        if(savedInstanceState == null) {
            if (findViewById(R.id.navigationFragmentContainer) != null) {
                getSupportFragmentManager().beginTransaction().add(R.id.navigationFragmentContainer,
                       homeFragment
                    ).commit();
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        /**
//         * Floating action button
//         * Button floats bottom right and triggers an action.
//         * Current Action: load new chatFragment
//         */
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                loadFragment(new ChatFragment());
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View navHeader = navigationView.getHeaderView(0);
        // set current user info in nav header
        SharedPreferences prefs = getSharedPreferences(
                getString(R.string.keys_shared_prefs),
                Context.MODE_PRIVATE);
        TextView tv = navHeader.findViewById(R.id.navHeaderFullName);
        tv.setText(prefs.getString(getString(R.string.keys_prefs_first_name), "")
                .concat(" ")
                .concat(prefs.getString(getString(R.string.keys_prefs_last_name), "")));
        tv = navHeader.findViewById(R.id.navHeaderUsername);
        tv.setText(prefs.getString(getString(R.string.keys_prefs_username), ""));
        tv = navHeader.findViewById(R.id.navHeaderEmail);
        tv.setText(prefs.getString(getString(R.string.keys_prefs_email), ""));


        // ask for location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions( this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION
                            , Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_LOCATIONS);
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            loadFragment(new SettingsFragment(),
                     getString(R.string.keys_fragment_settings));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
     * onNavigationItemSelected
     * Handles the clicking of item buttons in the nav menu drawer.
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            loadFragment(new HomeFragment(),
                    getString(R.string.keys_fragment_home));
        } else if (id == R.id.nav_friends) {
            loadFragment(new ConnectionTabsFragment(),
                    getString(R.string.keys_fragment_connection_tab));
        } else if (id == R.id.nav_weather) {
            loadFragment(new WeatherFragment(),
                    getString(R.string.keys_fragment_weather));
        } else if (id == R.id.nav_settings) {
            loadFragment(new SettingsFragment(),
                    getString(R.string.keys_fragment_settings));
        } else if (id == R.id.nav_logout) {
            SharedPreferences prefs =
                    getSharedPreferences(
                            getString(R.string.keys_shared_prefs),
                            Context.MODE_PRIVATE);

            prefs.edit().remove(getString(R.string.keys_prefs_username));

            prefs.edit().putBoolean(
                    getString(R.string.keys_prefs_stay_logged_in),
                    false)
                    .apply();
            //the way to close an app programmaticaly
            finishAndRemoveTask();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*
     * loadFragment loads the passed fragment into the nav menu screen replacing the current fragment.
     * @param frag
     */
    private void loadFragment(Fragment frag, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.navigationFragmentContainer, frag, tag)
                .addToBackStack(tag);
        // Commit the transaction
        transaction.commit();
    }

    /*
     * onSelectCity should load a city selection menu.
     */
    @Override
    public void onSelectCityButtonClicked() {
        loadFragment(new SelectWeatherCityFragment(),
                getString(R.string.keys_fragment_select_weather));
    }

    /*
     * onNewChat is a handle for calling new chat calls. Calls the loadFrament method passing a new ChatFragment
     */
    @Override
    public void onNewChat() {
//        Intent intent = new Intent(this, ChatActivity.class);
//        startActivity(intent);
        // loads friends fragment so user can choose who to start the chat with
        loadFragment(new ConnectionTabsFragment(),
                getString(R.string.keys_fragment_connection_tab));
    }

     @Override
    public void onOpenChat() {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(getString(R.string.keys_open_chat_source), R.id.fragmentHome);
        startActivity(intent);
    }

    /*
     * NewWeather creates a new weather fragment then replaces the currently displayed fragment with
     * the newly created weather fragment.
     */
    @Override
    public void NewWeather() {
        loadFragment(new WeatherFragment(), getString(R.string.keys_fragment_weather));
    }

    @Override
    public void onAddNewFriend() { loadFragment(new SearchNewFriendFragment(),
            getString(R.string.keys_fragment_search));}

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (mCurrentLocation == null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mCurrentLocation != null) {
                    // first log we should see
                    Log.i("CURRENT_LOCATION", mCurrentLocation.toString());
                    homeFragment.setWeatherData();
                }
                //startLocationUpdates();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("LOCATION", "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("LOCATION", "Connection failed: ConnectionResult.getErrorCode() = " +
                connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        Log.d("CURRENT_LOCATION", mCurrentLocation.toString());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_LOCATIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // locations-related task you need to do.
                    //startLocationUpdates();
                    homeFragment.setWeatherData();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d("PERMISSION DENIED", "Nothing to see or do here.");
                    //Shut down the app. In production release, you would let the user
                    //know why the app is shutting down…maybe ask for permission again?
                    finishAndRemoveTask();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }

    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        //     (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }
    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        //  (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
    }

    protected void onStart() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        super.onStart();
    }

    protected void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    public GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }
}
