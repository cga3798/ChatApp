package group1.tcss450.uw.edu.a450groupone.utils;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import group1.tcss450.uw.edu.a450groupone.NewGroupFragment;
import group1.tcss450.uw.edu.a450groupone.SearchNewFriendFragment;
import group1.tcss450.uw.edu.a450groupone.R;

public class ListViewAdapter extends BaseAdapter {


    Context mContext;
    LayoutInflater inflater;
    private ArrayList<String> arraylist;
    private String fragment;
    public ArrayList<Boolean> positionArray;


    public ListViewAdapter(Context context, String fragment) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        this.fragment = fragment;

        this.arraylist = new ArrayList<>();

        if (fragment.equals("search")) {
            this.arraylist.addAll(SearchNewFriendFragment.connectionResultList);

        } else if (fragment.equals("newGroup")) {

            this.arraylist.addAll(NewGroupFragment.contactsListView);
            positionArray = new ArrayList<Boolean>(arraylist.size());
            for ( int i =0; i<arraylist.size(); i++){
                positionArray.add(false);
            }

        }
    }

    @Override
    public int getCount() {
        int count = 0;
        if (fragment.equals("search")) {
            count = SearchNewFriendFragment.connectionResultList.size();

        } else if (fragment.equals("newGroup")) {
            count = NewGroupFragment.contactsListView.size();
        }

        return count;
    }

    @Override
    public String getItem(int position) {
        return SearchNewFriendFragment.connectionResultList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;

        if (view == null) {
            holder = new ViewHolder();

            if (fragment.equals("search")) {
                view = inflater.inflate(R.layout.listview_item, null);
                // Locate the TextViews in listview_item.xml
                holder.name = (TextView) view.findViewById(R.id.name);
                holder.invite = (TextView) view.findViewById(R.id.inviteTextView);
                view.setTag(holder);

            } else if (fragment.equals("newGroup")) {
                view = inflater.inflate(R.layout.new_group_item, null);
                // Locate the TextViews in listview_item.xml
                holder.name = (TextView) view.findViewById(R.id.newGroupItemName);
                holder.checkBox = (CheckBox) view.findViewById(R.id.newGroupCheckBox);
                view.setTag(holder);
            }

        } else {
            holder = (ViewHolder) view.getTag();
            holder.checkBox.setOnCheckedChangeListener(null);
        }

        // Set the results into TextViews
        if (fragment.equals("search")) {

            if (SearchNewFriendFragment.connectionResultList.get(position).contains("No results")) {
                holder.invite.setVisibility(View.GONE);
                holder.name.setText(SearchNewFriendFragment.connectionResultList.get(position));
            } else {
                holder.name.setText(SearchNewFriendFragment.connectionResultList.get(position));
            }


        } else if (fragment.equals("newGroup")) {
            holder.name.setText(NewGroupFragment.contactsListView.get(position));

            holder.checkBox.setFocusable(false);
            holder.checkBox.setChecked(positionArray.get(position));
            holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(isChecked ) {
                    positionArray.set(position, true);
                } else
                    positionArray.set(position, false);
            });

        }
        return view;
    }

    public boolean isChecked(int position) {
        return positionArray.get(position);
    }

    public class ViewHolder {
        TextView name;
        CheckBox checkBox;
        TextView invite;
    }

}