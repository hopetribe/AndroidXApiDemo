package com.hopetribe.androidxapidemo.proxy;

import android.util.Log;

import com.hopetribe.androidxapidemo.proxy.dynamic.UserProxyFactory;
import com.hopetribe.androidxapidemo.proxy.interceptor.InterceptorFactory;
import com.hopetribe.androidxapidemo.proxy.interceptor.Programmer;
import com.hopetribe.androidxapidemo.proxy.interceptor.Worker;
import com.hopetribe.androidxapidemo.proxy.interceptor.WorkerInterceptor;
import com.hopetribe.androidxapidemo.proxy.statics.UserDaoProxy;

public class ProxyTest {
    private final String TAG = "ProxyTest";

    public void testStaticProxy() {
        UserDao userDao = new UserDao();
        UserDaoProxy userDaoProxy = new UserDaoProxy(userDao);
        userDaoProxy.save();
    }

    public void testDynamicProxy() {
        IUserDao target = new UserDao();
        Log.i(TAG, "testDynamicProxy target : " + target.getClass());
        IUserDao proxy = (IUserDao) new UserProxyFactory(target).getProxyInstance();
        Log.i(TAG, "testDynamicProxy proxy: " + proxy.getClass());
        proxy.save();
    }

    public void testInterceptor() {
        Programmer target = new Programmer();
        target.login = false;
        Log.i(TAG, "testInterceptor target : " + target.getClass());
        Worker worker = (Worker) new InterceptorFactory().getProxyInstance(target, WorkerInterceptor.class);
        Log.i(TAG, "testInterceptor proxy: " + worker.getClass());
        worker.work();
        worker.rest();
    }
}
