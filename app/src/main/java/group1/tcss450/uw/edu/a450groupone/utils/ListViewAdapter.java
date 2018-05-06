package group1.tcss450.uw.edu.a450groupone.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Locale;

import group1.tcss450.uw.edu.a450groupone.AddNewFriendFragment;
import group1.tcss450.uw.edu.a450groupone.R;

public class ListViewAdapter extends BaseAdapter {


    // Declare Variables

    Context mContext;
    LayoutInflater inflater;
    private ArrayList<String> arraylist;

    public ListViewAdapter(Context context) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<String>();
        this.arraylist.addAll(AddNewFriendFragment.connectionResultList);
    }

    public class ViewHolder {
        TextView name;
    }

    @Override
    public int getCount() {
        return AddNewFriendFragment.connectionResultList.size();
    }

    @Override
    public String getItem(int position) {
        return AddNewFriendFragment.connectionResultList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.listview_item, null);
            // Locate the TextViews in listview_item.xml
            holder.name = (TextView) view.findViewById(R.id.name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        //TODO: This was returning animal name
        holder.name.setText(AddNewFriendFragment.connectionResultList.get(position));
        return view;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        AddNewFriendFragment.connectionResultList.clear();
        if (charText.length() == 0) {
            AddNewFriendFragment.connectionResultList.addAll(arraylist);
        } else {
            for (String wp : arraylist) {
                if (wp.toLowerCase(Locale.getDefault()).contains(charText)) {
                    AddNewFriendFragment.connectionResultList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

}