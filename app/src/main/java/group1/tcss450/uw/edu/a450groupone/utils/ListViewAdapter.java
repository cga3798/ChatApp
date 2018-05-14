package group1.tcss450.uw.edu.a450groupone.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

import group1.tcss450.uw.edu.a450groupone.SearchNewFriendFragment;
import group1.tcss450.uw.edu.a450groupone.R;

public class ListViewAdapter extends BaseAdapter {


    Context mContext;
    LayoutInflater inflater;
    private ArrayList<String> arraylist;

    public ListViewAdapter(Context context) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<String>();
        this.arraylist.addAll(SearchNewFriendFragment.connectionResultList);
    }

    public class ViewHolder {
        TextView name;
    }

    @Override
    public int getCount() {
        return SearchNewFriendFragment.connectionResultList.size();
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
            view = inflater.inflate(R.layout.listview_item, null);
            // Locate the TextViews in listview_item.xml
            holder.name = (TextView) view.findViewById(R.id.name);
            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.name.setText(SearchNewFriendFragment.connectionResultList.get(position));
        return view;
    }

}