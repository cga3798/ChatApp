package group1.tcss450.uw.edu.a450groupone;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import org.json.JSONException;
import org.json.JSONObject;
import group1.tcss450.uw.edu.a450groupone.model.Credentials;
import group1.tcss450.uw.edu.a450groupone.utils.SendPostAsyncTask;


public class MainActivity extends AppCompatActivity implements
        LoginFragment.OnLoginFragmentInteractionListener,
        RegisterFragment.OnRegistrationCompleteListener,
        SuccessRegistrationFragment.OnOkVerifyEmailListener,
        WeatherFragment.OnWeatherFragmentInteractionListener,
        HomeFragment.OnHomeFragmentInteractionListener,
        ChatFragment.OnChatFragmentInteractionListener {

    private Credentials mCredentials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState == null) {
            if (findViewById(R.id.fragmentContainer) != null) {
                loadFragment(new LoginFragment(),
                        getString(R.string.keys_fragment_login));
            }
        }
    }

    /**
     * Sends post request to web service to register user.
     * TODO: waiting on endpoint link
     * TODO: verify all credentials attributes have a value in server side
     * TODO: set onPreExecute function (disable buttons)
     * @param creds
     */
    public void onRegistrationSubmitted(Credentials creds) {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_register))
                .build();
        //build the JSONObject
        JSONObject msg = creds.asJSONObject();

        mCredentials = creds;

        //instantiate and execute the AsyncTask.
        //Feel free to add a handler for onPreExecution so that a progress bar
        //is displayed or maybe disable buttons. You would need a method in
        //LoginFragment to perform this.
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleRegisterOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    /**
     *  Verifies registration was succesful. Redirects to success fragment if successful or
     *  set error in register fragment otherwise.
     * @param result
     */
    private void handleRegisterOnPost(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");
            if (success) {
                //Registration was successful. Switch to the SuccessRegistrationFragment.
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new SuccessRegistrationFragment())
                        .commit();
            } else {
                Log.d("result: ", result);
                String err = resultsJSON.getJSONObject("error").getString("detail");
                if (err.contains("email")) {
                    RegisterFragment frag =
                            (RegisterFragment) getSupportFragmentManager()
                                    .findFragmentByTag(getString(R.string.keys_fragment_register));
                    frag.setError("Email already exists.", "email");

                } else if (err.contains("username")) {
                    RegisterFragment frag =
                            (RegisterFragment) getSupportFragmentManager()
                                    .findFragmentByTag(getString(R.string.keys_fragment_register));
                    frag.setError("Username already exists.", "username");

                } else {
                    showDialog("Please try again.", "Registration Unsuccessful!");
                }
            }
        } catch (JSONException e) {
            //It appears that the web service didn’t return a JSON formatted String
            //or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
        }
    }

    /**
     * Redirects to login fragment after clicking OK in
     * verify email fragment after registration.
     */
    @Override
    public void clickOkVerifyRegistration() {
        loadFragment(new LoginFragment(),
                getString(R.string.keys_fragment_login));
    }

    /**
     * TODO: waiting on end_point links AND add onPreExecute method (disable buttons...)
     * Makes request to web service to authenticate given credentials.
     * @param creds
     */
    @Override
    public void onLogin(Credentials creds) {
        //TODO: WARNING - uncommnet line below if you want to see the other screens after clicking "Log in"
        //loadHomeFragment();

        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.ep_base_url))
                .appendPath(getString(R.string.ep_login))
                .build();
        //build the JSONObject
        JSONObject msg = creds.asJSONObject();

        mCredentials = creds;

        //instantiate and execute the AsyncTask.
        //Feel free to add a handler for onPreExecution so that a progress bar
        //is displayed or maybe disable buttons. You would need a method in
        //LoginFragment to perform this.
        new SendPostAsyncTask.Builder(uri.toString(), msg)
                .onPostExecute(this::handleLoginOnPost)
                .onCancelled(this::handleErrorsInTask)
                .build().execute();
    }

    /**
     * Redirects to Register Fragment after clicking Register button in
     * Log in screen.
     */
    @Override
    public void onRegister() {
        loadFragment(new RegisterFragment(),
                getString(R.string.keys_fragment_register));
    }

    /**
     * Send post request to web service to login user.
     * @param result the JSON formatted String response from the web service
     */
    private void handleLoginOnPost(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            boolean success = resultsJSON.getBoolean("success");

            if (success) {
                //Login was successful. Switch to the loadSuccessFragment.
                checkStayLoggedIn();
                loadHomeFragment();
            } else {
                showDialog("Please check your username and password",
                        "Login Unsuccessful!");
            }
        } catch (JSONException e) {
            //It appears that the web service didn’t return a JSON formatted String
            //or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
        }
    }

    
    private void checkStayLoggedIn() {
        if (((CheckBox) findViewById(R.id.LoginCheckBoxStayLoggedIn)).isChecked()) {
            SharedPreferences prefs =
                    getSharedPreferences(
                            getString(R.string.keys_shared_prefs),
                            Context.MODE_PRIVATE);
            //save the username for later usage
            prefs.edit().putString(
                    getString(R.string.keys_prefs_username),
                    mCredentials.getUsername())
                    .apply();
            //save the users “want” to stay logged in
            prefs.edit().putBoolean(
                    getString(R.string.keys_prefs_stay_logged_in),
                    true)
                    .apply();
        }
    }

    private void loadHomeFragment() {
        Intent intent = new Intent(getBaseContext(), NavigationFragment.class);
        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    /**
     * Handle errors that may occur during the AsyncTask.
     * @param result the error message provide from the AsyncTask
     */
    private void handleErrorsInTask(String result) {
        Log.e("ASYNCT_TASK_ERROR", result);
    }

    // TODO: probably methods below will be in activity with navigation bar (after logging in)
    @Override
    public void onNewChat() {
        loadFragment(new ChatFragment(),
                getString(R.string.keys_fragment_chat));
    }
    @Override
    public void NewWeather() {
        loadFragment(new WeatherFragment(),
                getString(R.string.keys_fragment_weather));
    }

    @Override
    public void onSelectCityButtonClicked() {
        Log.d("ADD CITY: ", "CLICKED");
        loadFragment(new SelectWeatherCityFragment(),
                getString(R.string.keys_fragment_select_weather));
    }

    private void loadFragment(Fragment frag, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, frag, tag)
                .addToBackStack(null); // uncomment this if you want to go back
        // Commit the transaction
        transaction.commit();
    }

    private void showDialog(String err, String title) {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(err);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
