package com.nabinnembang.vonageandroidjava;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.nexmo.client.NexmoCall;
import com.nexmo.client.NexmoCallEventListener;
import com.nexmo.client.NexmoCallHandler;
import com.nexmo.client.NexmoMember;
import com.nexmo.client.NexmoCallMemberStatus;
import com.nexmo.client.NexmoClient;
import com.nexmo.client.NexmoMediaActionState;
import com.nexmo.client.request_listener.NexmoApiError;
import com.nexmo.client.request_listener.NexmoConnectionListener.ConnectionStatus;
import com.nexmo.client.request_listener.NexmoRequestListener;

public class MainActivity extends AppCompatActivity {

    private NexmoClient client;
    @Nullable
    private NexmoCall onGoingCall;

    private Button startCallButton;
    private Button endCallButton;
    private TextView connectionStatusTextView;
    private static final String jwtToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpYXQiOjE2NjczMTgwMjMsImV4cCI6MTY2NzM5MDAyMywianRpIjoiTXpnME5qTXhOekExIiwiYXBwbGljYXRpb25faWQiOiJiYThlYmRmMC05NGYwLTRlZmYtYWU2My05MTRmMDcwNjY0YzEiLCJzdWIiOiJJUURJQUxfTE9DQUxfVEVTVDpXRUI6NDptQG0uY29tIiwiYWNsIjp7InBhdGhzIjp7Ii8qL3VzZXJzLyoqIjp7fSwiLyovY29udmVyc2F0aW9ucy8qKiI6e30sIi8qL3Nlc3Npb25zLyoqIjp7fSwiLyovZGV2aWNlcy8qKiI6e30sIi8qL2ltYWdlLyoqIjp7fSwiLyovbWVkaWEvKioiOnt9LCIvKi9hcHBsaWNhdGlvbnMvKioiOnt9LCIvKi9wdXNoLyoqIjp7fSwiLyova25vY2tpbmcvKioiOnt9LCIvKi9sZWdzLyoqIjp7fX19fQ.japRl7vUErfrnQx3x4iq5YMrzETqBS1JHsDBbyWj8rRmC4agFmx6QN4Gnypjz_SmVaQEzgZuXNzxSaylpC1afAAfZ5aw3Y0CAkgn_J8r09dJbF6rLxpBzlfLsT5pTqONrAmbiYOObGuhqeWfqlkefMPvSha8pr6V6UpH0NVj23EAconSF8zfPo_rTZNYgwhE-YBCR9kj_tsvq3HBEwa5ln89D9EGye_YBBTNc__ZhrEt-UVKCFO_hFQ-4-fVvJJ_X_H76jgUSiiw0AsxteGYxF6m-wMHMeLWNjj3ULGszne819i_DrSIu7J0dbQAHXiP5AWzhAtJhWfYLaQr63CtZw";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // request permissions
        String[] callsPermissions = {Manifest.permission.RECORD_AUDIO};
        ActivityCompat.requestPermissions(this, callsPermissions, 123);

        // init views
        startCallButton = findViewById(R.id.startCallButton);
        endCallButton = findViewById(R.id.endCallButton);
        connectionStatusTextView = findViewById(R.id.connectionStatusTextView);

        startCallButton.setOnClickListener(v -> startCall());
        endCallButton.setOnClickListener(v -> hangup());

        // init client
        client = new NexmoClient.Builder().build(this);

        client.setConnectionListener((connectionStatus, connectionStatusReason) -> {
            runOnUiThread(() -> {
                connectionStatusTextView.setText(connectionStatus.toString());
            });

            if (connectionStatus == ConnectionStatus.CONNECTED) {
                runOnUiThread(() -> {
                    startCallButton.setVisibility(View.VISIBLE);
                });
            }
        });

        client.login(jwtToken);
    }

    @SuppressLint("MissingPermission")
    private void startCall() {
        client.serverCall("16202273311", null, new NexmoRequestListener<NexmoCall>() {
            @Override
            public void onError(@NonNull NexmoApiError nexmoApiError) {

            }

            @Override
            public void onSuccess(@Nullable NexmoCall call) {
                runOnUiThread(() -> {
                    endCallButton.setVisibility(View.VISIBLE);
                    startCallButton.setVisibility(View.INVISIBLE);
                });

                onGoingCall = call;
                onGoingCall.addCallEventListener(new NexmoCallEventListener() {
                    @Override
                    public void onMemberStatusUpdated(NexmoCallMemberStatus callStatus, NexmoMember NexmoMember) {
                        if (callStatus == NexmoCallMemberStatus.COMPLETED || callStatus == NexmoCallMemberStatus.CANCELLED) {
                            onGoingCall = null;

                            runOnUiThread(() -> {
                                endCallButton.setVisibility(View.INVISIBLE);
                                startCallButton.setVisibility(View.VISIBLE);
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
}