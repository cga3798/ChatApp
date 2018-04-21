package group1.tcss450.uw.edu.a450groupone;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {

    private static final int MIN_PASS_LENGTH = 6;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_register, container, false);

        //Bundle args = new Bundle();
        Button b = v.findViewById(R.id.registerButtonRegister);
        b.setOnClickListener(view -> {verifyFieldsInfoAndSend(v);});

        return v;
    }

    /**
     * It checks that the fields are not empty and checks for constraints.
     * TODO: Interaction listener is not implemented yet.
     *
     * @param v the view
     */
    public void verifyFieldsInfoAndSend(View v) {
        //args.putSerializable(getString(R.string.action_key), getString(R.string.login_from_register));
        //args.putSerializable(getString(R.string.user_key), userEt.getText().toString());
        //args.putSerializable(getString(R.string.pass_key), passEt.getText().toString());
        //args.putSerializable(getString(R.string.confirm_pass_key), cPassEt.getText().toString());

        EditText firstEt = v.findViewById(R.id.registerEditTextFirst);
        EditText lastEt = v.findViewById(R.id.registerEditTextLast);
        EditText emailEt = v.findViewById(R.id.registerEditTextEmail);
        EditText nicknameEt = v.findViewById(R.id.registerEditTextNickname);
        EditText passEt = v.findViewById(R.id.registerEditTextPassword);
        EditText confirmPassEt = v.findViewById(R.id.registerEditTextConfirmPass);


        // Check empty fields, matching, length reqs. TODO: verify email doesn't have account already
        if(firstEt.getText().toString().isEmpty() ) {
            firstEt.setError(getString(R.string.se_first_name));
        } else if( lastEt.getText().toString().isEmpty()) {
            lastEt.setError(getString(R.string.se_last_name));
        } else if( emailEt.getText().toString().isEmpty()) {
            emailEt.setError(getString(R.string.se_email));
        } else if( nicknameEt.getText().toString().isEmpty()) {
            nicknameEt.setError(getString(R.string.se_nickname));
        } else if(passEt.getText().toString().isEmpty()) {
            passEt.setError(getString(R.string.se_pass));
        } else if (confirmPassEt.getText().toString().isEmpty()) {
            confirmPassEt.setError(getString(R.string.se_confirm_pass));
        }

        if (passEt.getText().toString().length() < MIN_PASS_LENGTH) {
            passEt.setError(getString(R.string.se_pass_tooshort));
        }  // passwords dont match
        else if ( !passEt.getText().toString().equals(confirmPassEt.getText().toString()) ){
            passEt.setError(getString(R.string.se_pass_notmatch));
        }
        else { // all good
            //mListener.onFragmentInteraction(args);
        }
    }

}
