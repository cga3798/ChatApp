package group1.tcss450.uw.edu.a450groupone;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_friend, container, false);

        // TODO: maybe load contacts in an array and pass to this method
        populateContacts(v);

//        SearchView searchView = v.findViewById(R.id.friendSearchView);
//        searchView.setActivated(true);

//        v.findViewById(R.id.friendButtonAddNewFriend).setOnClickListener(view -> onAddNewFriend(v));

        return v;
    }

    private void populateContacts(View v) {
        LinearLayout contactsListContainer = v.findViewById(R.id.friendsLinearLayoutContactsList);

        for (int i = 0; i < 20; i++) {
            contactsListContainer.addView(
                    getContactView("A nickname", " A full name"));
        }
    }
//    private void onAddNewFriend(View v) {
//        mListener.onAddNewFriend();
//    }

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

    private View getContactView(String nickname, String fullName) {
        View v = LayoutInflater.from(getContext())
                .inflate(R.layout.contact_row, null, false);

        TextView tv = v.findViewById(R.id.friendsTextViewNickname);
        tv.setText(nickname);
        tv = v.findViewById(R.id.friendsTextViewFullName);
        tv.setText(fullName);

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO: change menu options
        super.onCreateOptionsMenu(menu, inflater);
    }
}
