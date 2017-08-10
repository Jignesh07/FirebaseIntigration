package com.example.admin.firebaseintigration.service;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.SharedPreferencesCompat;
import android.util.Log;

import com.example.admin.firebaseintigration.app.config;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import static android.content.ContentValues.TAG;

/**
 * Created by ADMIN on 25-07-2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        storeRegIdInPreg(refreshedToken);

        Intent registraionComplete = new Intent(config.REGISTRATION_COMPLETE);
        registraionComplete.putExtra("token",refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registraionComplete);

    }

    private void sendRegistrationToServer(final String token)
    {
        Log.e(TAG,"sendRegistrationToServer: " + token);
    }

    private void storeRegIdInPreg(String token)
    {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(config.SHRED_PREF,0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId",token);
        editor.commit();
    }
}
