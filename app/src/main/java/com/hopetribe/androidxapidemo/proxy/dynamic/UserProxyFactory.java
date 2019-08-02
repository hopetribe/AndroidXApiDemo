package com.hopetribe.androidxapidemo.proxy.dynamic;

import android.util.Log;

import java.lang.reflect.Proxy;

public class UserProxyFactory {
    private final String TAG = "UserProxyFactory";
    private Object target;

    public UserProxyFactory(Object target) {
        this.target = target;
    }

    public Object getProxyInstance() {
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), (proxy, method, args) -> {
            Log.i(TAG, "invoke: " + method.getName());
            Object returnValue = method.invoke(target, args);
            return null;
        });
    }
}
