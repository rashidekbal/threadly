package com.rtech.threadly;
import android.app.Application;
import android.content.Context;

import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.utils.ReUsableFunctions;


public class Threadly extends Application {
    private static Threadly instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        Core.init(instance.getApplicationContext());
       if (Core.getPreference().getBoolean(SharedPreferencesKeys.IS_LOGGED_IN,false)&&!Core.getPreference().getBoolean(SharedPreferencesKeys.IS_FCM_TOKEN_UPLOADED,false)){
           ReUsableFunctions.updateFcmTokenToServer();

       }
       if(Core.getPreference().getBoolean(SharedPreferencesKeys.IS_LOGGED_IN,false)){

           ReUsableFunctions.resendPendingMessages();
           
       }


    }
    public static Context getGlobalContext(){
        return instance.getApplicationContext();
    }
    public static Threadly getInstance(){
        return instance;
    }
    
}
