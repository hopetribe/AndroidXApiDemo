package com.hopetribe.androidxapidemo.proxy.statics;

import android.util.Log;

import com.hopetribe.androidxapidemo.proxy.IUserDao;

public class UserDaoProxy implements IUserDao {
    private final String TAG = "UserDaoProxy";
    private IUserDao target;

    public UserDaoProxy(IUserDao target) {
        this.target = target;
    }

    @Override
    public void save() {
        Log.i(TAG, "UserDaoProxy:: begin");
        target.save();
        Log.i(TAG, "UserDaoProxy:: commit: ");
    }
}
