package com.rtech.threadly.utils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.rtech.threadly.RoomDb.DataBase;
import com.rtech.threadly.SocketIo.SocketManager;
import com.rtech.threadly.activities.authActivities.loginActivities.LoginActivity;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.interfaces.NetworkCallbackInterface;
import com.rtech.threadly.network_managers.AuthManager;

import java.util.concurrent.Executors;

public class LogoutSequenceUtil {
    public static void Logout(AppCompatActivity activity) {
        new AuthManager().logout(new NetworkCallbackInterface() {
            @Override
            public void onSuccess() {
                SharedPreferences.Editor editor = Core.getPreference().edit();
                editor.clear();
                editor.apply();
                Executors.newSingleThreadExecutor().execute(() -> DataBase.getInstance().clearAllTables());
                SocketManager.getInstance().disconnect();
                ReUsableFunctions.ShowToast("logout Success..");
                Intent intent = new Intent(activity, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(intent);
                activity.finish();
            }

            @Override
            public void onError(String err) {
                ReUsableFunctions.ShowToast("Something went Wrong" + err);
                Log.d("LogoutError", "onError: " + err);

            }
        });
    }
}
