package com.example.android.common.adapters;

import android.widget.BaseAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.android.swiperefreshlayoutbasic.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RowNewsAdapter extends BaseAdapter {

    Context mContext;
    LayoutInflater mInflater;
    List<JSONObject> list;

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
        try {
            return list.get(position).getString("title");
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
            convertView = mInflater.inflate(R.layout.row_news, null);
            holder = new ViewHolder();
            holder.titleTextView = (TextView) convertView.findViewById(R.id.text_title);
            holder.author = (TextView) convertView.findViewById(R.id.author);
            holder.score = (TextView) convertView.findViewById(R.id.score);
            holder.time = (TextView) convertView.findViewById(R.id.time);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        try {
            if (list.get(position) != null) {
                holder.titleTextView.setText(list.get(position).getString("title"));
                holder.author.setText(list.get(position).getString("by"));
                holder.score.setText(list.get(position).getString("score"));
                Date time = new Date();
                if (list.get(position).getString("time") != null
                        && !list.get(position).getString("time").isEmpty()) {
                    time.setTime(Long.valueOf(list.get(position).getString("time")));
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(time);
                    holder.time.setText(cal.getTime().toString());
                } else {
                    holder.time.setText("");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        };

        return convertView;
    }

    private static class ViewHolder {
        public TextView titleTextView;
        public TextView author;
        public TextView score;
        public TextView time;
    }

    public void updateData(List<JSONObject> list) {
        this.list.clear();
        this.list.addAll(list);
        this.notifyDataSetChanged();
    }
}
