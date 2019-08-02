package com.hopetribe.androidxapidemo.proxy.interceptor;

import android.util.Log;

import java.lang.reflect.Method;

/**
 * @author eric
 */
public class WorkerInterceptor implements Interceptor {

    private static final String TAG = "WorkerInterceptor";

    @Override
    public boolean shouldIntercept(Object target, Method method) {
        Log.i(TAG, "shouldIntercept: ");
        Log.i(TAG, "shouldIntercept: " + target.getClass().getName());
        String methodName = method.getName();
        Programmer programmer = (Programmer) target;

        boolean shouldIntercept;
        switch (methodName) {
            case "work":
                shouldIntercept = !programmer.login;
                break;
            case "rest":
                shouldIntercept = false;
                break;
            default:
                shouldIntercept = true;
                break;
        }
        return shouldIntercept;
    }

    @Override
    public void intercepted(Method method, String reason) {
        Log.e(TAG, "intercepted: not allow to " + method.getName() + " because " + reason);
    }
}
