package com.example.android.common.adapters;

import android.widget.BaseAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.android.swiperefreshlayoutbasic.R;

import java.util.ArrayList;
import java.util.List;

public class RowNewsAdapter extends BaseAdapter {

    Context mContext;
    LayoutInflater mInflater;
    List<String> list;

    public RowNewsAdapter(Context context, LayoutInflater inflater) {
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
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_news, null);
            holder = new ViewHolder();
            holder.titleTextView = (TextView) convertView.findViewById(R.id.text_title);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.titleTextView.setText(getItem(position));

        return convertView;
    }

    private static class ViewHolder {
        public TextView titleTextView;
    }

    public void updateData(List<String> list) {
        this.list = list;
        notifyDataSetChanged();
    }
}
