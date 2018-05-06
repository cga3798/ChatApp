package group1.tcss450.uw.edu.a450groupone;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendFragment extends Fragment {


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

        return v;
    }

    private void populateContacts(View v) {
        LinearLayout contactsListContainer = v.findViewById(R.id.friendsLinearLayoutContactsList);

        for (int i = 0; i < 20; i++) {
            contactsListContainer.addView(
                    getContactView("A nickname", " A full name"));
        }
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
