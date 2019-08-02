package com.hopetribe.androidxapidemo.proxy.interceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class InterceptorFactory {

    public Object getProxyInstance(Object target, Class interceptor) {
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(), new InterceptorHandler(target, interceptor));
    }

    public class InterceptorHandler implements InvocationHandler {

        private Object target;
        private Class interceptClass;

        public InterceptorHandler(Object target, Class interceptClass) {
            this.target = target;
            this.interceptClass = interceptClass;
        }


        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (interceptClass == null) {
                return method.invoke(target, args);
            }

            Interceptor interceptor = (Interceptor) interceptClass.newInstance();
            if (interceptor.shouldIntercept(target, method)) {
                interceptor.intercepted(method, "no login");
            } else {
                return method.invoke(target, args);
            }
            return null;
        }
    }
}
