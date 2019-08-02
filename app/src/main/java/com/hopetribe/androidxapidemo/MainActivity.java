package com.hopetribe.androidxapidemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;

import com.hopetribe.androidxapidemo.edof.EdofExtractor;
import com.hopetribe.androidxapidemo.proxy.ProxyTest;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Handler workerHandler;
    EdofExtractor extractor;
    ProxyTest proxyTest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HandlerThread handlerThread = new HandlerThread("WorkerThreader");
        handlerThread.start();
        workerHandler = new Handler(handlerThread.getLooper());

        findViewById(R.id.btn_test).setOnClickListener(this::testEdof);

        extractor = new EdofExtractor();
        proxyTest = new ProxyTest();
    }

    private void testEdof(View v) {
        workerHandler.post(() -> {
            extractor.extractEdof("/sdcard/example.jpg");
        });
    }


    public void testStaticProxy(View v){
        proxyTest.testStaticProxy();
    }

    public void testDynamicProxy(View v){
        proxyTest.testDynamicProxy();
    }

    public void testInterceptor(View v){
        proxyTest.testInterceptor();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (workerHandler.getLooper() != null) {
            workerHandler.getLooper().quitSafely();
        }
    }
}
