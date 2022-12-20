package com.nabinnembang.vonageandroidjava;

import android.app.Application;
import android.util.Log;

import com.nexmo.client.NexmoClient;
import static com.nexmo.utils.logger.ILogger.eLogLevel;

public class MainApplication extends Application{

    private static final String TAG = "MYDEBUG_MainApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        new NexmoClient.Builder()
                //.restEnvironmentHost("https://api-us-3.vonage.com")
                //.environmentHost("https://ws-us-3.vonage.com")
                //.logLevel(eLogLevel.DEBUG)
                .build(this);
                Log.d(TAG, "NexmoClient: "+String.valueOf(NexmoClient.get()));
    }
}
