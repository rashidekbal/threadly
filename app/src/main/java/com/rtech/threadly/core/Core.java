package com.rtech.threadly.core;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.androidnetworking.AndroidNetworking;
import com.rtech.threadly.constants.SharedPreferencesKeys;


public class Core {
    private static SharedPreferences preferences;
   public static void init(Context context){
       AndroidNetworking.initialize(context);
       preferences=context.getSharedPreferences(SharedPreferencesKeys.SHARED_PREF_NAME,MODE_PRIVATE);
   }

   public static SharedPreferences getPreference(){
       return preferences;
   }

}
