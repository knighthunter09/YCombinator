package com.example.android.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.common.adapters.ExpandableListAdapter;
import com.example.android.common.http.HttpClient_Volley;
import com.example.android.swiperefreshlayoutbasic.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailsFragment extends Fragment {

    private static final String LOG_TAG = DetailsFragment.class.getSimpleName();

    private List<JSONObject> commentsTitle = new ArrayList<>();

    private Map<String,JSONObject> contentCommentsMap = new HashMap<>();

    private Map<String,JSONObject> contentCommentsReplyMap = new HashMap<>();

    private Map<String,Bundle> propMap = new HashMap<>();

    //Expandable Lists details
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader = new ArrayList<>();
    HashMap<String, List<String>> listDataChild = new HashMap<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        Log.d(LOG_TAG, "Details View Created1" + propMap);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG,"Details View Created2" + propMap);
        View view = inflater.inflate(R.layout.row_details, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(LOG_TAG,"Details View Created3" + propMap);

        //Prepare comments list
        // get the listview
        expListView = (ExpandableListView) view.findViewById(R.id.expandableList);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(this.getActivity().getApplicationContext(),
                listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        new BackgroundTask().execute();

    }

    private void prepareListData() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void queryForIndividualComments(final String id) {
        Log.d(LOG_TAG,"QUERYING FOR INDIVIDUAL COMMENTS " + id);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, "https://hacker-news.firebaseio.com/v0/item/" + id + ".json",
                        null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(LOG_TAG, response.toString());
                        commentsTitle.add(response);
                        contentCommentsMap.put(id, response);
                        queryForIndividualCommentsReply(id);
                        Log.d(LOG_TAG, commentsTitle.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(LOG_TAG, error.getMessage());
                    }
                });

        HttpClient_Volley.getInstance(DetailsFragment.this.getActivity().getApplicationContext()).
                addToRequestQueue(jsObjRequest);
    }


    private void queryForIndividualCommentsReply(final String id) {
        Log.d(LOG_TAG,"QUERYING FOR INDIVIDUAL COMMENTS REPLY " + id);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, "https://hacker-news.firebaseio.com/v0/item/" + id + ".json",
                        null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(LOG_TAG, response.toString());
                        contentCommentsReplyMap.put(id, response);

                        Log.d(LOG_TAG, commentsTitle.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(LOG_TAG, error.getMessage());
                    }
                });

        HttpClient_Volley.getInstance(DetailsFragment.this.getActivity().getApplicationContext()).
                addToRequestQueue(jsObjRequest);
    }

    public void setProp(String key,Bundle value) {
        this.propMap.clear();
        this.propMap.put(key,value);
    }

    private class BackgroundTask extends AsyncTask<Void, Void, List<JSONObject>> {

        static final int TASK_DURATION = 3 * 1000; // 3 seconds

        @Override
        protected List<JSONObject> doInBackground(Void... params) {
            try {
                Thread.sleep(TASK_DURATION);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (DetailsFragment.this.propMap != null
                    && DetailsFragment.this.propMap.isEmpty()
                        && DetailsFragment.this.propMap.size() == 1) {
                for(String key : propMap.keySet()) {
                    ArrayList<Integer> commentIds = propMap.get(key).getIntegerArrayList("request_obj_kids");
                    int i = 0;
                    for (int id : commentIds) {
                        queryForIndividualComments(String.valueOf(id));
                        i++;
                        if (i == 10) {
                            break;
                        }
                    }
                }
            }
            return new ArrayList<>();
        }

        @Override
        protected void onPostExecute(List<JSONObject> result) {
            super.onPostExecute(result);
        }

    }
}

