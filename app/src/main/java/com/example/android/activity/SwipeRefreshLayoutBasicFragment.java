/*
 * Copyright 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.activity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.common.adapters.RowNewsAdapter;
import com.example.android.common.http.HttpClient_Volley;
import com.example.android.swiperefreshlayoutbasic.R;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SwipeRefreshLayoutBasicFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String LOG_TAG = SwipeRefreshLayoutBasicFragment.class.getSimpleName();

    private static final int LIST_ITEM_COUNT = 20;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ListView mListView;

    private RowNewsAdapter mListAdapter;

    private JSONArray topStoriesArray;

    private JSONArray maxStory;

    private List<JSONObject> storyTitle = new ArrayList<>();

    private Map<String,JSONObject> contentMap = new HashMap<>();

    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        progressDialog = new ProgressDialog(this.getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sample, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);

        mSwipeRefreshLayout.setColorScheme(
                R.color.swipe_color_1, R.color.swipe_color_2,
                R.color.swipe_color_3, R.color.swipe_color_4);

        mListView = (ListView) view.findViewById(android.R.id.list);
        mListView.setOnItemClickListener(this);

        // Set the adapter between the ListView and its backing data.
        mListAdapter = new RowNewsAdapter(
                this.getActivity().getApplicationContext(),this.getLayoutInflater(null));
        mListView.setAdapter(mListAdapter);
        //queryForMaxItemId();
        //queryForTopStories();
        progressDialog.setMessage("Loading data...");
        progressDialog.setCancelable(false);
        return view;
    }
    // END_INCLUDE (inflate_view)

    // BEGIN_INCLUDE (setup_views)
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        mListAdapter = new RowNewsAdapter(
//                this.getActivity().getApplicationContext(),this.getLayoutInflater(null));

        // Set the adapter between the ListView and its backing data.
        //mListView.setAdapter(mListAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(LOG_TAG, "onRefresh called from SwipeRefreshLayout");

                initiateRefresh();
            }
        });
        initiateRefresh();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                Log.i(LOG_TAG, "Refresh menu item selected");

                // We make sure that the SwipeRefreshLayout is displaying it's refreshing indicator
                if (!mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(true);
                }

                // Start our refresh background task
                initiateRefresh();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initiateRefresh() {
        Log.i(LOG_TAG, "initiateRefresh");
        if (this.isVisible()) {
            progressDialog.show();
        }
        queryForTopStories();
        //new BackgroundTask().execute();
    }

    private void onRefreshComplete(List<JSONObject> result) {
        Log.i(LOG_TAG, "onRefreshComplete" + " :: " + result.toString());

        mListAdapter.updateData(result);
        mListAdapter.notifyDataSetChanged();

        // Stop the refreshing indicator
        mSwipeRefreshLayout.setRefreshing(false);
        progressDialog.dismiss();
    }

    private void queryForMaxItemId() {
        Log.d(LOG_TAG,"QUERYING FOR MAX ID ");
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, "https://hacker-news.firebaseio.com/v0/maxitem.json",
                        null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(LOG_TAG, "MAX ID *** " + response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(LOG_TAG, error.getMessage());
                    }
                });

        HttpClient_Volley.getInstance(SwipeRefreshLayoutBasicFragment.this.getActivity().getApplicationContext()).
                addToRequestQueue(jsObjRequest);
    }

    private void queryForIndividualStories(String id,final int reqCall) {
        Log.d(LOG_TAG,"QUERYING FOR INDIVIDUAL STORIES " + id + " :: " + reqCall);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, "https://hacker-news.firebaseio.com/v0/item/" + id + ".json",
                        null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(LOG_TAG, response.toString());
                            storyTitle.add(response);
                            contentMap.put(String.valueOf(reqCall), response);

                            Log.d(LOG_TAG,storyTitle.toString());

                            if (reqCall == 9) {
                                onRefreshComplete(storyTitle);
                            }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(LOG_TAG, error.getMessage());
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });

        HttpClient_Volley.getInstance(SwipeRefreshLayoutBasicFragment.this.getActivity().getApplicationContext()).
                addToRequestQueue(jsObjRequest);
    }

    private void queryForTopStories() {
        //Log.d(LOG_TAG,"QUERYING FOR TOP STORIES ");
        if (!mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
        JsonArrayRequest jsObjRequest = new JsonArrayRequest
                ("https://hacker-news.firebaseio.com/v0/topstories.json",new Response.Listener<JSONArray>(){

                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        topStoriesArray = jsonArray;
                        storyTitle.clear();
                        Log.d(LOG_TAG, jsonArray.toString());
                        for (int i = 0; i < 10; i++) {
                            queryForIndividualStories(jsonArray.optString(i), i);
                        }
                    }
                },new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(LOG_TAG, error.getMessage());
                    }
                });

        HttpClient_Volley.getInstance(SwipeRefreshLayoutBasicFragment.this.getActivity().getApplicationContext()).
                addToRequestQueue(jsObjRequest);
    }

    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * <p/>
     * Implementers can call getItemAtPosition(position) if they need
     * to access the data associated with the selected item.
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(LOG_TAG,"Initiating");
        FragmentTransaction transaction = this.getActivity().getSupportFragmentManager().beginTransaction();
        DetailsFragment fragment = new DetailsFragment();
        transaction.replace(R.id.sample_content_fragment, fragment);
        Bundle args = new Bundle();
        try {
            Log.d(LOG_TAG, position + " P>>>M " + contentMap + " >> ID : " + contentMap.get(String.valueOf(position)).getInt("id"));
            args.putInt("request_obj", contentMap.get(String.valueOf(position)).getInt("id"));
            args.putIntegerArrayList("request_obj_kids",
                    getArrayOfKids(contentMap.get(String.valueOf(position)).getJSONArray("kids")));

            fragment.setArguments(args);
            fragment.setProp(String.valueOf(contentMap.get(String.valueOf(position)).getInt("id")),args);
        } catch (JSONException e) {
            e.printStackTrace();
        };
        transaction.addToBackStack(String.valueOf(R.id.swiperefresh));
        transaction.commit();
    }

    private ArrayList<Integer> getArrayOfKids(JSONArray kids) {
        List<Integer> intList = new ArrayList<>();
        if (kids != null && kids.length() > 0) {
            for (int i = 0; i < kids.length(); i++) {
                try {
                    intList.add(kids.getInt(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d(LOG_TAG, "Returning kids " + intList);
        return (ArrayList<Integer>) intList;
    }

    private class BackgroundTask extends AsyncTask<Void, Void, List<JSONObject>> {

        static final int TASK_DURATION = 3 * 1000; // 3 seconds

        @Override
        protected List<JSONObject> doInBackground(Void... params) {
            // Sleep for a small amount of time to simulate a background-task
            try {
                Thread.sleep(TASK_DURATION);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //queryForMaxItemId();
            queryForTopStories();
            return storyTitle;
        }

        @Override
        protected void onPostExecute(List<JSONObject> result) {
            super.onPostExecute(result);

            // Tell the Fragment that the refresh has completed
            //onRefreshComplete(result);
        }

    }
}
