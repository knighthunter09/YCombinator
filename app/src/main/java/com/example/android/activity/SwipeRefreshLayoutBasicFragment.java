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
import com.example.android.common.dummydata.Cheeses;
import com.example.android.common.http.HttpClient_Volley;
import com.example.android.swiperefreshlayoutbasic.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SwipeRefreshLayoutBasicFragment extends Fragment {

    private static final String LOG_TAG = SwipeRefreshLayoutBasicFragment.class.getSimpleName();

    private static final int LIST_ITEM_COUNT = 20;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ListView mListView;

    private RowNewsAdapter mListAdapter;

    private JSONArray topStoriesArray;

    private JSONArray maxStory;

    private List<String> storyTitle = new ArrayList<>();

    private Map<String,JSONObject> contentMap = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
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

        //queryForMaxItemId();
        //queryForTopStories();
        initiateRefresh();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListAdapter = new RowNewsAdapter(
                this.getActivity().getApplicationContext(),this.getLayoutInflater(null));

        // Set the adapter between the ListView and its backing data.
        mListView.setAdapter(mListAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initiateRefresh();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:

                // We make sure that the SwipeRefreshLayout is displaying it's refreshing indicator
                if (!mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(true);
                }

                initiateRefresh();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initiateRefresh() {
        new BackgroundTask().execute();
    }

    private void onRefreshComplete(List<String> result) {
        mListAdapter.updateData(result);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void queryForMaxItemId() {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, "https://hacker-news.firebaseio.com/v0/maxitem.json",
                        null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
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
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, "https://hacker-news.firebaseio.com/v0/item/" + id + ".json",
                        null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(LOG_TAG, response.toString());
                        try {
                            storyTitle.add(response.getString("title"));
                            contentMap.put(response.getString("id"), response);

                            Log.d(LOG_TAG,storyTitle.toString());

                            if (reqCall == 9) {
                                onRefreshComplete(storyTitle);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            mSwipeRefreshLayout.setRefreshing(false);
                            Log.e(LOG_TAG,e.getMessage());
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

    private class BackgroundTask extends AsyncTask<Void, Void, List<String>> {

        static final int TASK_DURATION = 3 * 1000; // 3 seconds

        @Override
        protected List<String> doInBackground(Void... params) {
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
        protected void onPostExecute(List<String> result) {
            super.onPostExecute(result);
        }

    }
}
