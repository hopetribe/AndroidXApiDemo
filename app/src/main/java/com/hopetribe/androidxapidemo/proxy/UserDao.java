package com.hopetribe.androidxapidemo.proxy;

import android.util.Log;

public class UserDao implements IUserDao {

    private final String TAG = "UserDao";

    @Override
    public void save() {
        Log.i(TAG, "UserDao::save");
    }
}
