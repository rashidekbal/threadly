package com.rtech.gpgram.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.rtech.gpgram.activities.UserProfileActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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

    public File getFileFromUri(Context context, Uri contentUri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(contentUri);

        // Create a file in cache dir (you can use filesDir too)
        File file = new File(context.getCacheDir(), "temp_file");
        FileOutputStream outputStream = new FileOutputStream(file);

        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }

        outputStream.close();
        inputStream.close();

        return file;
    }

}
