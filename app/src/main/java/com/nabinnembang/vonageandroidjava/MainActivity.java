package com.nabinnembang.vonageandroidjava;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nexmo.client.NexmoCall;
import com.nexmo.client.NexmoCallEventListener;
import com.nexmo.client.NexmoCallHandler;
import com.nexmo.client.NexmoLegTransferEvent;
import com.nexmo.client.NexmoMember;
import com.nexmo.client.NexmoCallMemberStatus;
import com.nexmo.client.NexmoClient;
import com.nexmo.client.NexmoMediaActionState;
import com.nexmo.client.request_listener.NexmoApiError;
import com.nexmo.client.request_listener.NexmoConnectionListener.ConnectionStatus;
import com.nexmo.client.request_listener.NexmoRequestListener;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MYDEBUG_MainActivity";

    private NexmoClient client;
    @Nullable
    private NexmoCall onGoingCall;

    private Button logFemTokenBtn;
    private Button startCallButton;
    private Button endCallButton;
    private Button answerCallButton;
    private Button rejectCallButton;
    private TextView connectionStatusTextView;
    private static final String jwtToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpYXQiOjE2NzEzNDAzOTAsImV4cCI6MTY3MTQxMjM5MCwianRpIjoiTkRNNE5EQXhNakV4IiwiYXBwbGljYXRpb25faWQiOiJiYThlYmRmMC05NGYwLTRlZmYtYWU2My05MTRmMDcwNjY0YzEiLCJzdWIiOiJJUURJQUxfTE9DQUw6TU9CSUxFOjQ6bUBtLmNvbSIsImFjbCI6eyJwYXRocyI6eyIvKi91c2Vycy8qKiI6e30sIi8qL2NvbnZlcnNhdGlvbnMvKioiOnt9LCIvKi9zZXNzaW9ucy8qKiI6e30sIi8qL2RldmljZXMvKioiOnt9LCIvKi9pbWFnZS8qKiI6e30sIi8qL21lZGlhLyoqIjp7fSwiLyovYXBwbGljYXRpb25zLyoqIjp7fSwiLyovcHVzaC8qKiI6e30sIi8qL2tub2NraW5nLyoqIjp7fSwiLyovbGVncy8qKiI6e319fX0.LT_A2RXI8z3EO7PHx6hz6r3ksFYz-_ly-lbOlOMaRso2BR-D6aAXJkSxlhTbxSPUjARzHx7B8EuWfFewcFslMfuRU7GN2aIoA8ZBzv4LDrTfh2eyPyd2C-cSffK-BIRiTxvRJexoddMB768IZxBRJIeBBUEQ1PJmGobaXu6GiL2egdoTaFM7sH3BcPXBSQjr5uAyAxSIvkMWv2NZ4rLKYfsn61wkqr1W_s-m-njObjPscTSwGZzNENkSa3mS4x1Ub52OXXl5jMjjS-qiG2fMyPvrbOkG__pchkZTJ5Nm6BOiicJMzxnYNuMAxvF-e-DaHq7fI60xGYE4qVY09n1XGQ ◀eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpYXQiOjE2NzEzNDAzOTAsImV4cCI6MTY3MTQxMjM5MCwianRpIjoiTkRNNE5EQXhNakV4IiwiYXBwbGljYXRpb25faWQiOiJiYThlYmRmMC05NGYwLTRlZmY";
    public static final int  CALL_PERM_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create channel to show notifications.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }

        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        //
        // Handle possible data accompanying notification message.
        // [START handle_data_extras]
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "IntentExtraKey: " + key + " IntentExtraValue: " + value);
            }
        }

        // request permissions
        String[] callsPermissions = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.RECORD_AUDIO};
        ActivityCompat.requestPermissions(this, callsPermissions, CALL_PERM_CODE);

        // init views
        logFemTokenBtn = findViewById(R.id.logFemTokenBtn);
        startCallButton = findViewById(R.id.startCallButton);
        endCallButton = findViewById(R.id.endCallButton);
        connectionStatusTextView = findViewById(R.id.connectionStatusTextView);
        answerCallButton = findViewById(R.id.answerCallButton);
        rejectCallButton = findViewById(R.id.rejectCallButton);

        logFemTokenBtn.setOnClickListener(v -> logFcmToken());
        startCallButton.setOnClickListener(v -> startCall());
        endCallButton.setOnClickListener(v -> hangup());
        answerCallButton.setOnClickListener(view -> { answerCall();});
        rejectCallButton.setOnClickListener(view -> { rejectCall();});


        client = NexmoClient.get();
        if (client != null) {
            Log.d(TAG, "NexmoClientTest: " + String.valueOf(client));

            //Listen for client connection status changes
            client.setConnectionListener((connectionStatus, connectionStatusReason) -> {
                Log.d(TAG, "NexmoClient connectionStatus: " + connectionStatus.toString());
                runOnUiThread(() -> {
                    connectionStatusTextView.setText(connectionStatus.toString());
                });

                if (connectionStatus == ConnectionStatus.CONNECTED) {
                    runOnUiThread(() -> {
                        // startCallButton.setVisibility(View.VISIBLE);
                    });
                }
            });

            client.addIncomingCallListener(it -> {
                onGoingCall = it;

                answerCallButton.setVisibility(View.VISIBLE);
                rejectCallButton.setVisibility(View.VISIBLE);
            });
            //client.removeIncomingCallListeners();

            //NexmoClient.get().login("uyuyuiyiuyiuyui");
            client.login(jwtToken);
            //Log.d(TAG, "jwtToken: "+jwtToken);
        }

    }

    @SuppressLint("MissingPermission")
    private void startCall() {
        HashMap<String, Object> customData = new HashMap<String, Object>();
        customData.put("callType", "mobileAppOutBound");
        client.serverCall("16202273311", customData, new NexmoRequestListener<NexmoCall>() {
            @Override
            public void onError(@NonNull NexmoApiError nexmoApiError) {

            }

            @Override
            public void onSuccess(@Nullable NexmoCall call) {
                runOnUiThread(() -> {
                    endCallButton.setVisibility(View.VISIBLE);
                    //startCallButton.setVisibility(View.INVISIBLE);
                });

                onGoingCall = call;
                onGoingCall.addCallEventListener(new NexmoCallEventListener() {
                    @Override
                    public void onMemberStatusUpdated(NexmoCallMemberStatus callStatus, NexmoMember NexmoMember) {
                        if (callStatus == NexmoCallMemberStatus.COMPLETED || callStatus == NexmoCallMemberStatus.CANCELLED) {
                            onGoingCall = null;

                            runOnUiThread(() -> {
                                endCallButton.setVisibility(View.INVISIBLE);
                                //startCallButton.setVisibility(View.VISIBLE);
                            });
                        }
                    }

                    @Override
                    public void onMuteChanged(NexmoMediaActionState nexmoMediaActionState, NexmoMember NexmoMember) {

                    }

                    @Override
                    public void onEarmuffChanged(NexmoMediaActionState nexmoMediaActionState, NexmoMember NexmoMember) {

                    }

                    @Override
                    public void onDTMF(String s, NexmoMember NexmoMember) {

                    }

                    @Override
                    public void onLegTransfer(NexmoLegTransferEvent event, NexmoMember member) {

                    }
                });
            }
        });
    }

    private void hangup() {
        onGoingCall.hangup(new NexmoRequestListener<NexmoCall>() {
            @Override
            public void onError(@NonNull NexmoApiError nexmoApiError) {
                onGoingCall = null;
            }

            @Override
            public void onSuccess(@Nullable NexmoCall nexmoCall) {

            }
        });
    }

    @SuppressLint("MissingPermission")
    private void answerCall() {
        onGoingCall.answer(new NexmoRequestListener<NexmoCall>() {
            @Override
            public void onError(@NonNull NexmoApiError nexmoApiError) {

            }

            @Override
            public void onSuccess(@Nullable NexmoCall nexmoCall) {
                answerCallButton.setVisibility(View.GONE);
                rejectCallButton.setVisibility(View.GONE);
            }
        });
    }

    private void rejectCall() {
        onGoingCall.hangup(new NexmoRequestListener<NexmoCall>() {
            @Override
            public void onError(@NonNull NexmoApiError nexmoApiError) {

            }

            @Override
            public void onSuccess(@Nullable NexmoCall nexmoCall) {
                answerCallButton.setVisibility(View.GONE);
                rejectCallButton.setVisibility(View.GONE);
                endCallButton.setVisibility(View.GONE);
            }
        });
        onGoingCall = null;
    }

    private void logFcmToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

//    @Override
//    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(permsRequestCode, permissions, grantResults);
//        Log.d(TAG, "permsRequestCode: " + permsRequestCode);
//
//
//        switch (permsRequestCode) {
//            case CALL_PERM_CODE:
//                // init client
//                client = NexmoClient.get();
//                Log.d(TAG, "NexmoClientTest: "+String.valueOf(client));
//
//                //Listen for client connection status changes
//                client.setConnectionListener((connectionStatus, connectionStatusReason) -> {
//                    Log.d(TAG, "NexmoClient connectionStatus: " +connectionStatus.toString());
//                    runOnUiThread(() -> {
//                        connectionStatusTextView.setText(connectionStatus.toString());
//                    });
//
//                    if (connectionStatus == ConnectionStatus.CONNECTED) {
//                        runOnUiThread(() -> {
//                            // startCallButton.setVisibility(View.VISIBLE);
//                        });
//                    }
//                });
//
//                client.addIncomingCallListener(it -> {
//                    onGoingCall = it;
//
//                    answerCallButton.setVisibility(View.VISIBLE);
//                    rejectCallButton.setVisibility(View.VISIBLE);
//                });
//                //client.removeIncomingCallListeners();
//
//                //NexmoClient.get().login("uyuyuiyiuyiuyui");
//                client.login(jwtToken);
//                //Log.d(TAG, "jwtToken: "+jwtToken);
//        }
//
//    }



}