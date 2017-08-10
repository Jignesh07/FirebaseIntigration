package com.example.admin.firebaseintigration.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.firebaseintigration.R;
import com.example.admin.firebaseintigration.app.config;
import com.example.admin.firebaseintigration.util.NotificationUtils;
import com.google.firebase.messaging.FirebaseMessaging;

import static android.R.id.message;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private TextView txtRegId,txtMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtMessage = (TextView) findViewById(R.id.txt_push_message);
        txtRegId = (TextView) findViewById(R.id.txt_reg_id);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if(intent.getAction().equals(config.REGISTRATION_COMPLETE))
                {
                    FirebaseMessaging.getInstance().subscribeToTopic(config.TOPIC_GLOBAL);
                    displayFirebaseRegId();
                }
                else if(intent.getAction().equals(config.PUSH_NOTIFICATION))
                {
                String message = intent.getStringExtra("message");
                Toast.makeText(getApplicationContext(),"Push Notification : "+ message,Toast.LENGTH_SHORT).show();
                txtMessage.setText(message);


                }
            }
        };
        displayFirebaseRegId();
    }
    private void displayFirebaseRegId()
    {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(config.SHRED_PREF,0);
        String regId = pref.getString("regId",null);
        Log.e(TAG,"Firebase reg id: " + regId);

        if(!TextUtils.isEmpty(regId))
        {
            txtRegId.setText("Firebase Reg Id: " + regId);
        }
        else
        {
            txtRegId.setText("Firebase Reg Id is not received yet!");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(config.REGISTRATION_COMPLETE));

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(config.PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
}
