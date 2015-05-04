package com.example.android.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import com.example.android.common.activities.ActivityBase;
import com.example.android.swiperefreshlayoutbasic.R;


public class MainActivity extends ActivityBase {

    public static final String TAG = "MainActivity";

    // Whether the Log Fragment is currently shown
    private boolean mLogShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            SwipeRefreshLayoutBasicFragment fragment = new SwipeRefreshLayoutBasicFragment();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        switch(item.getItemId()) {
//            case R.id.menu_toggle_log:
//                mLogShown = !mLogShown;
////                ViewAnimator output = (ViewAnimator) findViewById(R.id.sample_output);
////                if (mLogShown) {
////                    output.setDisplayedChild(1);
////                } else {
////                    output.setDisplayedChild(0);
////                }
//                supportInvalidateOptionsMenu();
//                return true;
//        }
        return super.onOptionsItemSelected(item);
    }
}
