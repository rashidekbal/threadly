package com.rtech.gpgram.utils;

import android.content.Context;
import android.content.Intent;

import com.rtech.gpgram.activities.UserProfileActivity;

public class ReUsableFunctions {
    public static void  openProfile(Context c, String userid){
        Intent intent =new Intent(c, UserProfileActivity.class);
        intent.putExtra("userid",userid);
        c.startActivity(intent);
    }
    public static boolean isEmail(String input){
        return  input.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    }
    public static boolean isPhone(String input) {
        return input.length() == 10 && input.matches("\\d+");
    }
    public static void ShowToast(Context c, String message) {
        android.widget.Toast.makeText(c, message, android.widget.Toast.LENGTH_SHORT).show();
    }
}
