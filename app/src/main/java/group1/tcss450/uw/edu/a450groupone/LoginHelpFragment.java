package group1.tcss450.uw.edu.a450groupone;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import group1.tcss450.uw.edu.a450groupone.model.Credentials;

public class LoginHelpFragment extends Fragment implements View.OnClickListener{
    private OnHelpFragmentInteractionListener mListener;
    public LoginHelpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login_help, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnHelpFragmentInteractionListener) {
            mListener = (OnHelpFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnHelpFragmentInteractionListener");
        }

    }

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
            EditText editText = (EditText) getActivity().findViewById(R.id.help_user_input);
            switch (view.getId()) {
                case R.id.help_password_button:
                    // testing for username to be > 0 length
                    if (editText.getText().toString().trim().length() == 0) {
                        editText.setError("Email may not be empty");
                    }
                    else {
                        mListener.onPassRecover(editText.getText().toString());
                    }
                    break;
                case R.id.help_username_button:
                    // testing for username to be > 0 length
                    if (editText.getText().toString().trim().length() == 0) {
                        editText.setError("Email may not be empty");
                    }
                    else {
                        mListener.onUserRecover(editText.getText().toString());
                    }
                default:
                    Log.wtf("", "Didn't expect to see me...");
            }
        }

    }
        /**
         * listener interfaces for temp buttons
         *
         * author: Casey Anderson
         */
        public interface OnHelpFragmentInteractionListener {
            void onPassRecover(String email);

            void onUserRecover(String email);
        }
    }