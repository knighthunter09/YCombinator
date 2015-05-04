package com.example.android.common.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class ActivityBase extends FragmentActivity {

    public static final String TAG = "ActivityBase";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected  void onStart() {
        super.onStart();
    }
}
