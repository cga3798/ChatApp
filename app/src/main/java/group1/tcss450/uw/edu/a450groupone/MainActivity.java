package group1.tcss450.uw.edu.a450groupone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity implements WeatherFragment.OnWeatherFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null) {
            if (findViewById(R.id.fragmentContainer) != null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragmentContainer, new WeatherFragment())
                        .commit();
            }
        }

        Log.d("MARS", "inside oncreate.");
    }

    @Override
    public void onFragmentInteraction() {

    }
}
