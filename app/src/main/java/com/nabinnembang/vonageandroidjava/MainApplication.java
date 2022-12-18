package com.nabinnembang.vonageandroidjava;

import android.app.Application;
import android.util.Log;

import com.nexmo.client.NexmoClient;

public class MainApplication extends Application{

    private static final String TAG = "MYDEBUG_MainApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        new NexmoClient.Builder()
                //.restEnvironmentHost("https://api-us-1.nexmo.com")
                //.environmentHost("https://ws-us-3.vonage.com")
                .build(this);
        Log.d(TAG, "NexmoClient: "+String.valueOf(NexmoClient.get()));
    }
}
