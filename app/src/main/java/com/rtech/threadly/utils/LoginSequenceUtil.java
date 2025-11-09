package com.rtech.threadly.utils;

import com.rtech.threadly.core.Core;
import com.rtech.threadly.services.FcmService;

public class LoginSequenceUtil {
    public static void onLoggedInSuccess(){

        Core.startSocketEvents();
        ReUsableFunctions.updateFcmTokenToServer();
        //TODO uncomment after fix
//        new MessengerUtils().LoadAllChatsForLoginAction();
    }
}
