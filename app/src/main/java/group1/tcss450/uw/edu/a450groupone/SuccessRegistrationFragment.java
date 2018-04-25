package group1.tcss450.uw.edu.a450groupone;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class SuccessRegistrationFragment extends Fragment {

    private OnOkVerifyEmailListener mListener;

    public SuccessRegistrationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_success_registration, container, false);

//        v.findViewById(R.id.successRegisterButtonOK).setOnClickListener(view ->
//                mListener.clickOkVerifyRegistration());
        return v;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnOkVerifyEmailListener) {
            mListener = (OnOkVerifyEmailListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnOkVerifyEmailListener {
        void clickOkVerifyRegistration();
    }

}