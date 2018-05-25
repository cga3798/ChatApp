package group1.tcss450.uw.edu.a450groupone;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import group1.tcss450.uw.edu.a450groupone.model.Credentials;

/**
 * Fragment for user loggin view.
 *
 *
 * @author Casey Anderson
 * @version 1 April 2018
 */
public class LoginFragment extends Fragment implements View.OnClickListener{
    private OnLoginFragmentInteractionListener mListener;

    public LoginFragment() {
        // Required empty public constructor
    }



    /**
     * setting up fragment
     *
     * author: Casey Anderson
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        Button b = (Button) v.findViewById(R.id.LoginButtonLogin);
        b.setOnClickListener(this);
        b = (Button) v.findViewById(R.id.LoginButtonRegister);
        b.setOnClickListener(this);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs =
                this.getActivity().getSharedPreferences(getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        String.valueOf(prefs.getInt(getString(R.string.keys_prefs_id), -1));
        CheckBox c = (CheckBox) getActivity().findViewById(R.id.LoginCheckBoxStayLoggedIn);
        c.setChecked(prefs.getBoolean(getString(R.string.keys_prefs_stay_logged_in), false));
    }

    /**
     * Allows an external source to set an error message on this fragment. This may
     * be needed if an Activity includes processing that could cause login to fail.
     * @param err the error message to display.
     */
    public void setError(String err) {
        Toast.makeText(getActivity(),
                err, Toast.LENGTH_LONG).show();
    }

    /**
     * onAttach method to attach login fragment listener.
     *
     * author: Casey Anderson
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginFragmentInteractionListener) {
            mListener = (OnLoginFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     * default onDetach method
     *
     * author: Casey Anderson
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * onClick method to handle user login or register attempts.
     *
     * author: Casey Anderson
     */
    @Override
    public void onClick(View view) {
        if (mListener != null) {
            switch (view.getId()) {
                case R.id.LoginButtonLogin:
                    // TODO: add this in method
                    EditText editText = (EditText) getActivity().findViewById(R.id.LoginEditTextUserName);

                    // testing for username to be > 0 length
                    if (editText.getText().toString().trim().length() == 0) {
                        editText.setError("Username may not be empty");
                    }

                    String userName = editText.getText().toString();
                    editText = (EditText) getActivity().findViewById(R.id.LoginEditTextPassword);

                    // testing for password to be > 0 length
                    if (editText.getText().toString().trim().length() == 0) {
                        editText.setError("password may not be empty");
                    }
                    Editable password = editText.getText();
                    // TODO: redundant...

                    // testing for username and password to be > 0 length
                    if(!(userName.length() == 0 || password.length() == 0)) {
                        Credentials creds = new Credentials.Builder(userName, password).build();
                        mListener.onLogin(creds);
                    }
                    break;
                case R.id.LoginButtonRegister:
                    mListener.onRegister();

                default:
                    Log.wtf("", "Didn't expect to see me...");
            }
        }
    }

    /**
     * listener interfaces for buttons
     *
     * author: Casey Anderson
     */
    public interface OnLoginFragmentInteractionListener {
        // TODO: Update argument type and name
        void onLogin(Credentials creds);
        void onRegister();
    }

}
