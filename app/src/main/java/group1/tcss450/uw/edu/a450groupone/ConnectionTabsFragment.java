package group1.tcss450.uw.edu.a450groupone;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment that will contain the view pager and will load all the tabs related to
 * connections.
 *
 */
public class ConnectionTabsFragment extends Fragment {
    View view;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_connection_tabs,container, false);
        // Setting ViewPager for each Tabs
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) view.findViewById(R.id.result_tabs);
        tabs.setupWithViewPager(viewPager);
        return view;
    }

    /**
     * Add fragments as tabs to the page viewer
     * @param viewPager viewer that will hold all the tabs
     */
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getChildFragmentManager());

        adapter.addFragment(new ReceivedRequestsFragment(), "Received Invites");
        adapter.addFragment(new FriendFragment(), "Friends");
        adapter.addFragment(new SearchNewFriendFragment(), "Search");
        adapter.addFragment(new SentRequestsFragment(), "Sent Invites");
        viewPager.setAdapter(adapter);
    }

    /**
     * Adapter class that will manage all the fragments in the view pager.
     */
    private static class Adapter extends FragmentStatePagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
