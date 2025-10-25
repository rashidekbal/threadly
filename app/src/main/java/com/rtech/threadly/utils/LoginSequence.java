package com.rtech.threadly.utils;

import com.rtech.threadly.core.Core;
public class LoginSequence {
    public static void onLoggedInSuccess(){
        Core.startSocketEvents();
        ReUsableFunctions.updateFcmTokenToServer();
        new MessengerUtils().LoadAllChatsForLoginAction();
    }
}
