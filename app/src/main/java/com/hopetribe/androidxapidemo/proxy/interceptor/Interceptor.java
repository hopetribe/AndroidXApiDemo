package com.hopetribe.androidxapidemo.proxy.interceptor;

import java.lang.reflect.Method;

/**
 * @author eric
 */
public interface Interceptor {

    boolean shouldIntercept(Object target, Method method);

    void intercepted(Method method, String reason);
}
