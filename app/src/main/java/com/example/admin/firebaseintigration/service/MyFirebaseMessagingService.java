package com.example.admin.firebaseintigration.service;


import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.admin.firebaseintigration.activity.MainActivity;
import com.example.admin.firebaseintigration.app.config;
import com.example.admin.firebaseintigration.util.NotificationUtils;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import static android.R.attr.codes;
import static android.R.attr.configChanges;
import static android.R.attr.handle;
import static android.os.Build.VERSION_CODES.N;

/**
 * Created by ADMIN on 25-07-2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private NotificationUtils notificationUtils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG,"From: " + remoteMessage.getFrom());

        if(remoteMessage == null)
        {
            return;
        }

        if(remoteMessage.getNotification() != null)
        {
            Log.e(TAG,"Notification Body : " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }
        if(remoteMessage.getData().size() > 0)
        {
            Log.e(TAG,"Data Payload : " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);

            } catch (JSONException e) {
                Log.e(TAG,"Exception : " + e.getMessage());
                //e.printStackTrace();
            }

        }
    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            Intent pushNotificaion = new Intent(config.PUSH_NOTIFICATION);
            pushNotificaion.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotificaion);

            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        }
        else {

        }
    }

    private void handleDataMessage(JSONObject json)
    {
        Log.e(TAG,"push json: " + json.toString());
        try {
            JSONObject data = json.getJSONObject("data");
            String title = data.getString("title");
            String message = data.getString("message");
            boolean isBackground = data.getBoolean("is_background");
            String imageUrl = data.getString("image");
            String timestamp = data.getString("timestamp");
            JSONObject payload = data.getJSONObject("payload");


            Log.e(TAG,"title : " + title);
            Log.e(TAG,"message : " + message);
            Log.e(TAG,"isBackground : " + isBackground);
            Log.e(TAG,"imageUrl : " + imageUrl);
            Log.e(TAG,"timestamp : " + timestamp);


            if(!NotificationUtils.isAppIsInBackground(getApplicationContext()))
            {
                Intent pushNotifiation = new Intent(config.PUSH_NOTIFICATION);
                pushNotifiation.putExtra("message",message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotifiation);

                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            }
            else {
                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                resultIntent.putExtra("message",message);

                if(TextUtils.isEmpty(imageUrl))
                {
                    showNotificationMessageWithBigImage(getApplicationContext(),title,message,timestamp,resultIntent,imageUrl);
                }

            }

        } catch (JSONException e) {
          //  e.printStackTrace();
            Log.e(TAG, "Json Exception: " + e.getMessage());
        }

    }
    private void showNotificationMessage(Context context,String title, String message, String timestamp, Intent intent)
    {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title,message,timestamp,intent);
    }
    private void showNotificationMessageWithBigImage(Context context,String title, String message,String timeStamp,Intent intent,String imageUrl)
    {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title,message,timeStamp,intent,imageUrl);
    }


}
