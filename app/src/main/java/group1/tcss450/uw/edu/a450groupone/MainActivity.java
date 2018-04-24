package group1.tcss450.uw.edu.a450groupone;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.support.v7.widget.RecyclerView;

import group1.tcss450.uw.edu.a450groupone.model.Credentials;

public class MainActivity extends AppCompatActivity implements LoginFragment.OnLoginFragmentInteractionListener,
                                                    RegisterFragment.OnRegistrationCompleteListener,
                                                    SuccessRegistrationFragment.OnOkVerifyEmailListener {

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

    @Override
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
        // maybe make intent to new activity (App Activity)
    }

    @Override
    public void onRegister() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, new RegisterFragment())
                // TODO: replace by string value
                .addToBackStack("Register Fragment")
                .commit();
    }


}
