package com.rtech.threadly.utils;

import static android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET;
import static android.net.NetworkCapabilities.NET_CAPABILITY_VALIDATED;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

public class ConnectivityUtil {
    public static boolean IsInternetConnected(Context context){
        ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network=connectivityManager.getActiveNetwork();
        if(network==null)return false;
        NetworkCapabilities capabilities=connectivityManager.getNetworkCapabilities(network);
        return capabilities !=null && capabilities.hasCapability(NET_CAPABILITY_INTERNET)&& capabilities.hasCapability( NET_CAPABILITY_VALIDATED);
    }
}
