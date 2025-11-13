package com.rtech.threadly.utils;

import com.rtech.threadly.core.Core;
public class LoginSequenceUtil {
    public static void onLoggedInSuccess(){

        Core.startSocketEvents();
        ReUsableFunctions.updateFcmTokenToServer();
        //TODO optimization is left
      new MessengerUtils().LoadAllChatsForLoginAction();
    }
}
