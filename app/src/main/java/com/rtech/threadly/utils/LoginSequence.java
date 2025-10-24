package com.rtech.threadly.utils;

import com.rtech.threadly.core.Core;
import com.rtech.threadly.network_managers.MessageManager;

public class LoginSequence {
    public static void onLoggedInSuccess(){
        MessageManager.checkAndGetPendingMessages();
        Core.startSocketEvents();
        ReUsableFunctions.updateFcmTokenToServer();
    }
}
