package group1.tcss450.uw.edu.a450groupone;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

/*
 * Navigation fragment holds the currently displayed
 * screen overlaying it with a toolbar and clickable nav menu.
 */

public class NavigationActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        ChatFragment.OnChatFragmentInteractionListener,
        WeatherFragment.OnWeatherFragmentInteractionListener,
        HomeFragment.OnHomeFragmentInteractionListener,
        FriendFragment.OnFriendFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        MainActivity.mainActivity.finish();

//        if (ContextCompat.checkSelfPermission(getApplicationContext(),
//                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(getApplicationContext(),
//                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//            ActivityCompat.requestPermissions(this,
//                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
//                            android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
//
//        }

        if(savedInstanceState == null) {
            if (findViewById(R.id.navigationFragmentContainer) != null) {
                getSupportFragmentManager().beginTransaction().add(R.id.navigationFragmentContainer,
                        new HomeFragment()//replace with weather fragment
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
         Intent i = new Intent(this, TempActivity.class);
         startActivity(i);
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

}
