package com.rtech.threadly.utils;

import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.core.Core;

public class PreferenceUtil {
    public static String getUUID(){
        return Core.getPreference().getString(SharedPreferencesKeys.UUID,"null");
    }
    public static String getJWT(){
        return Core.getPreference().getString(SharedPreferencesKeys.JWT_TOKEN,"null");
    }
}
