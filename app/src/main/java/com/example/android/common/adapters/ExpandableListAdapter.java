package com.example.android.common.adapters;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.android.swiperefreshlayoutbasic.R;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private static final String LOG_TAG = ExpandableListAdapter.class.getSimpleName();
    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<String>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return _listDataChild.get(_listDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return _listDataChild.get(_listDataHeader.get(groupPosition)).get(childPosition).hashCode();
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater)_context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.reply_comments, parent, false);
        }

        TextView itemName = (TextView) v.findViewById(R.id.itemName);

        String det = _listDataChild.get(_listDataHeader.get(groupPosition)).get(childPosition);

        itemName.setText(det);

        return v;

    }

    @Override
    public int getChildrenCount(int groupPosition) {
        int size = _listDataChild.get(_listDataHeader.get(groupPosition)).size();
        Log.d(LOG_TAG, "Child for group ["+groupPosition+"] is ["+size+"]");
        return size;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return _listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return _listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return _listDataHeader.get(groupPosition).hashCode();
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater)_context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.comments_header, parent, false);
        }

        TextView groupName = (TextView) v.findViewById(R.id.groupName);

        String cat = _listDataHeader.get(groupPosition);

        groupName.setText(cat);

        return v;

    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

