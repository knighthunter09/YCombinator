package com.example.android.common.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.android.swiperefreshlayoutbasic.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentsAdapter  extends BaseAdapter {

    Context mContext;
    LayoutInflater mInflater;
    List<JSONObject> list;
    HashMap<String,JSONObject> map;

    public CommentsAdapter(Context context, LayoutInflater inflater) {
        this.mContext = context;
        this.mInflater = inflater;
        this.list = new ArrayList<>();
        this.map = new HashMap<>();
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
            holder.time = (TextView) convertView.findViewById(R.id.timeofcomment);
            holder.reply = (TextView) convertView.findViewById(R.id.reply);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        try {
            if (list.get(position) != null) {
                if (list.get(position).has("text")) {
                    holder.commentTextView.setText(Html.fromHtml(list.get(position)
                            .getString("text")).toString());
                }
                if (list.get(position).has("by")){
                    holder.commentAuthorView.setText( (position +1) + ". " + list.get(position)
                            .getString("by"));
                }
                if (list.get(position).has("time")) {
                    Date time = new Date();
                    if (list.get(position).getString("time") != null
                            && !list.get(position).getString("time").isEmpty()) {
                        time.setTime(Long.valueOf(list.get(position).getString("time")));
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(time);
                        holder.time.setText(cal.getTime().toString());
                    } else {
                        holder.time.setText(String.valueOf(list.get(position).has("time")));
                    }
                }
                if (list.get(position).has("id")) {
                    JSONObject obj = this.map.get(String.valueOf(list.get(position).getInt("id")));
                    holder.reply.setText((obj.has("by") ? obj.getString("by"): " Anonymous ")
                            + " replied " + (Html.fromHtml(obj.has("text")
                                ? obj.getString("text") : "" )));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        };

        return convertView;
    }

    private static class ViewHolder {
        public TextView commentTextView;
        public TextView commentAuthorView;
        public TextView time;
        public TextView reply;
    }

    public void updateData(List<JSONObject> list,
                           Map<String, JSONObject> contentCommentsReplyMap) {
        this.list.clear();
        this.list.addAll(list);
        this.map.clear();
        this.map.putAll(contentCommentsReplyMap);
        this.notifyDataSetChanged();
    }
}
