package group1.tcss450.uw.edu.a450groupone;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendFragment extends Fragment {

    private OnFriendFragmentInteractionListener mListener;


    public FriendFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_friend, container, false);

//        SearchView searchView = v.findViewById(R.id.friendSearchView);
//        searchView.setActivated(true);

        v.findViewById(R.id.friendButtonAddNewFriend).setOnClickListener(view -> onAddNewFriend(v));

        return v;
    }

    private void onAddNewFriend(View v) {
        mListener.onAddNewFriend();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FriendFragment.OnFriendFragmentInteractionListener) {
            mListener = (FriendFragment.OnFriendFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFriendFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFriendFragmentInteractionListener {
        void onAddNewFriend();
    }

}
