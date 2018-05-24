package group1.tcss450.uw.edu.a450groupone;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import group1.tcss450.uw.edu.a450groupone.utils.SendPostAsyncTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment{


    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        SharedPreferences prefs =
                this.getActivity().getSharedPreferences(getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        String.valueOf(prefs.getInt(getString(R.string.keys_prefs_id), -1));
        CheckBox c = (CheckBox) v.findViewById(R.id.settingsCheckBoxStayLog);
        c.setChecked(prefs.getBoolean(getString(R.string.keys_prefs_stay_logged_in), false));

        Button save = (Button) v.findViewById(R.id.settingsButtonSave);
        Button cancel = (Button) v.findViewById(R.id.settingsButtonCancel);
        save.setEnabled(false);
        cancel.setEnabled(false);

        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save.setEnabled(true);
                cancel.setEnabled(true);
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs.edit().putBoolean(
                        getString(R.string.keys_prefs_stay_logged_in),
                        c.isChecked())
                        .apply();
                save.setEnabled(false);
                cancel.setEnabled(false);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.setChecked(prefs.getBoolean(getString(R.string.keys_prefs_stay_logged_in), false));
                save.setEnabled(false);
                cancel.setEnabled(false);
            }
        });



        return v;
    }
}
