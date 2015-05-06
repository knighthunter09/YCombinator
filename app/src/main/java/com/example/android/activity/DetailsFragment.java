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
import android.widget.ListView;
//import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.android.common.adapters.CommentsAdapter;
import com.example.android.common.http.HttpClient_Volley;
import com.example.android.swiperefreshlayoutbasic.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailsFragment extends Fragment {

    private static final String LOG_TAG = DetailsFragment.class.getSimpleName();

    private List<JSONObject> commentsTitle = new ArrayList<>();

    private Map<String,JSONObject> contentCommentsReplyMap = new HashMap<>();

    private Map<String,Bundle> propMap = new HashMap<>();

    private CommentsAdapter mListAdapter;

//    private ProgressBar progressBar;

    private ListView mListView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.row_details, container, false);
        ((TextView)view.findViewById(R.id.details)).setText("Comments on post by '"
                + (propMap.get(propMap.keySet().iterator().next()).getString("author")) + "'");
        mListView = (ListView) view.findViewById(android.R.id.list);

        mListAdapter = new CommentsAdapter(
                this.getActivity().getApplicationContext(),this.getLayoutInflater(null));
        mListView.setAdapter(mListAdapter);

//        progressBar = (ProgressBar)view.findViewById(R.id.progressBar1);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        progressBar.setVisibility(View.VISIBLE);
        new BackgroundTask().execute();

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

    private void queryForIndividualComments(final String id,final int count,final int totalSize) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, "https://hacker-news.firebaseio.com/v0/item/" + id + ".json",
                        null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        DetailsFragment.this.commentsTitle.add(response);
                        // Perform below operation in the next method
                        //                        if (count == totalSize-1) {
                        //                            mListAdapter.updateData(DetailsFragment.this.commentsTitle);
                        //                            progressBar.setVisibility(View.GONE);
                        //                        }
                        try {
                            if (response.has("kids") && response.getJSONArray("kids") != null
                                    && response.getJSONArray("kids").length() > 0) {
                                queryForIndividualCommentsReply(
                                        String.valueOf(response.getJSONArray("kids").getInt(0)),
                                            (count == totalSize-1));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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


    private void queryForIndividualCommentsReply(final String id, final boolean updateAdapter) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, "https://hacker-news.firebaseio.com/v0/item/" + id + ".json",
                        null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                                DetailsFragment.this.contentCommentsReplyMap.put(
                                        String.valueOf(response.getInt("parent")), response);

                                if (updateAdapter) {
                                        mListAdapter.updateData(DetailsFragment.this.commentsTitle,
                                                DetailsFragment.this.contentCommentsReplyMap);
 //                                       progressBar.setVisibility(View.GONE);
                                    }
                            } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                    && !DetailsFragment.this.propMap.isEmpty()
                        && DetailsFragment.this.propMap.size() == 1) {
                for(String key : DetailsFragment.this.propMap.keySet()) {
                    ArrayList<Integer> commentIds = DetailsFragment.this.propMap.get(key)
                            .getIntegerArrayList("request_obj_kids");
                    int i = 0;
                    for (int id : commentIds) {
                        queryForIndividualComments(String.valueOf(id),i,commentIds.size());
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

