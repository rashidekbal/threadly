package com.rtech.threadly;

import static com.rtech.threadly.network_managers.FcmManager.UpdateFcmToken;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.interfaces.NetworkCallbackInterface;
import com.rtech.threadly.network_managers.FcmManager;

public class Threadly extends Application {
    String token;
    private static Threadly instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        Core.init(instance.getApplicationContext());

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {token=task.getResult() ;
            UpdateFcmToken(token, new NetworkCallbackInterface() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError(String err) {
                    Log.d("sentdata", "onResponse: "+err);

                }
            });


            }
        });

    }
    public static Context getGlobalContext(){
        return instance.getApplicationContext();
    }
    public static Threadly getInstance(){
        return instance;
    }
    
}
