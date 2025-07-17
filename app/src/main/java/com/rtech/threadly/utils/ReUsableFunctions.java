package com.rtech.threadly.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.rtech.threadly.Threadly;
import com.rtech.threadly.activities.UserProfileActivity;

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
    public static void ShowToast( String message) {
        android.widget.Toast.makeText(Threadly.getGlobalContext(), message, android.widget.Toast.LENGTH_SHORT).show();
    }

    public static File getFileFromUri(Context context, Uri uri) throws IOException {
        ContentResolver cr = context.getContentResolver();
        String mimeType = cr.getType(uri); // e.g., "image/jpeg" or "video/mp4"

        String extension = "";
        if (mimeType != null) {
            extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
        }

        if (extension == null || extension.isEmpty()) {
            extension = "tmp"; // fallback
        }

        String fileName = "gallery_" + System.currentTimeMillis() + "." + extension;
        File file = new File(context.getCacheDir(), fileName);

        InputStream inputStream = cr.openInputStream(uri);
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
