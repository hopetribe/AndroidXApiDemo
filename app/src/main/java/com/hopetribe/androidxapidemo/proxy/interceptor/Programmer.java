package com.hopetribe.androidxapidemo.proxy.interceptor;

import android.util.Log;

public class Programmer implements Worker {
    private final String TAG = "Programmer";

    public boolean login = false;

    @Override
    public void work() {
        Log.i(TAG, "work: programing");
    }

    @Override
    public void rest() {
        Log.i(TAG, "have a rest");
    }
}
