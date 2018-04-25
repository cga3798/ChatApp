package group1.tcss450.uw.edu.a450groupone;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import group1.tcss450.uw.edu.a450groupone.model.Credentials;


public class MainActivity extends AppCompatActivity implements LoginFragment.OnLoginFragmentInteractionListener,
        RegisterFragment.OnRegistrationCompleteListener,
        SuccessRegistrationFragment.OnOkVerifyEmailListener, WeatherFragment.OnWeatherFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState == null) {
            if (findViewById(R.id.fragmentContainer) != null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragmentContainer, new LoginFragment())
                        .commit();
            }
        }
    }

    public void onRegistrationSubmitted() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, new SuccessRegistrationFragment())
                .commit();
    }

    @Override
    public void clickOkVerifyRegistration() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, new LoginFragment())
                .commit();
    }

    @Override
    public void onLogin(Credentials creds) {
        Intent i = new Intent(getBaseContext(), NavigationFragment.class);
        i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);
    }

    @Override
    public void onRegister() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, new RegisterFragment())
                // TODO: replace by string value
                .addToBackStack("Register Fragment")
                .commit();
    }

    @Override
    public void onSelectCityButtonClicked() {
        Log.d("MainActivity", "clicked me");
        SelectWeatherCityFragment rf;
        rf = new SelectWeatherCityFragment();
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, rf)
                .addToBackStack(null); // uncomment this if you want to go back
        // Commit the transaction
        transaction.commit();
    }
}