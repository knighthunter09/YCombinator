package com.example.android.common.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.android.swiperefreshlayoutbasic.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CommentsAdapter  extends BaseAdapter {

    Context mContext;
    LayoutInflater mInflater;
    List<JSONObject> list;

    public CommentsAdapter(Context context, LayoutInflater inflater) {
        mContext = context;
        mInflater = inflater;
        list = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String getItem(int position) {
        try {
            return list.get(position).getString("text");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.comments_header, null);
            holder = new ViewHolder();
            holder.commentTextView = (TextView) convertView.findViewById(R.id.groupName);
            holder.commentAuthorView = (TextView) convertView.findViewById(R.id.commentAuthor);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        try {
            holder.commentTextView.setText(list.get(position).getString("text"));
            holder.commentAuthorView.setText(list.get(position).getString("by"));
        } catch (JSONException e) {
            e.printStackTrace();
        };

        return convertView;
    }

    private static class ViewHolder {
        public TextView commentTextView;
        public TextView commentAuthorView;
    }

    public void updateData(List<JSONObject> list) {
        this.list.clear();
        this.list.addAll(list);
        this.notifyDataSetChanged();
    }
}
