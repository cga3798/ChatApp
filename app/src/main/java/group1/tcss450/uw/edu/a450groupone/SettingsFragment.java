package group1.tcss450.uw.edu.a450groupone;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import es.dmoral.toasty.Toasty;
import group1.tcss450.uw.edu.a450groupone.utils.SendPostAsyncTask;

import static android.content.Context.MODE_PRIVATE;


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

        RadioGroup radioGroup = (RadioGroup) v.findViewById(R.id.settingsRadioGroup);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        SharedPreferences prefs =
                this.getActivity().getSharedPreferences(getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        String.valueOf(prefs.getInt(getString(R.string.keys_prefs_id), -1));

        CheckBox checkbox = (CheckBox) v.findViewById(R.id.settingsCheckBoxStayLog);
        checkbox.setChecked(prefs.getBoolean(getString(R.string.keys_prefs_stay_logged_in), false));

        Button save = (Button) v.findViewById(R.id.settingsButtonSave);
        Button cancel = (Button) v.findViewById(R.id.settingsButtonCancel);


        save.setOnClickListener(v1 -> {
            prefs.edit().putBoolean(
                    getString(R.string.keys_prefs_stay_logged_in),
                    checkbox.isChecked())
                    .apply();

            int radioButtonID = radioGroup.getCheckedRadioButtonId();
            View radioButton = radioGroup.findViewById(radioButtonID);
            int index = radioGroup.indexOfChild(radioButton);

            SharedPreferences.Editor themePrefs = getActivity().getSharedPreferences("themePrefs", MODE_PRIVATE).edit();
            if (index == 0) {
                themePrefs.putInt("themePrefs", 1).apply();
            } else if (index == 1) {
                themePrefs.putInt("themePrefs", 2).apply();
            } else if (index == 2) {
                themePrefs.putInt("themePrefs", 3).apply();
            } else if (index == -1 || index == 3) {
                themePrefs.putInt("themePrefs", -1).apply();
            }

            getActivity().recreate();

        });

        cancel.setOnClickListener(veiw -> getFragmentManager().popBackStack());

        return v;
    }
}
