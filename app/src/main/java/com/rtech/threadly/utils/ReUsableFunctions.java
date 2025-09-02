package com.rtech.threadly.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import androidx.appcompat.app.AppCompatActivity;

import com.rtech.threadly.RoomDb.DataBase;
import com.rtech.threadly.RoomDb.schemas.MessageSchema;
import com.rtech.threadly.Threadly;
import com.rtech.threadly.activities.LoginActivity;
import com.rtech.threadly.activities.UserProfileActivity;
import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.core.Core;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executors;

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
    public static void logout(AppCompatActivity activity){
        SharedPreferences loginInfo= Core.getPreference();
        SharedPreferences.Editor editor=loginInfo.edit();
        editor.clear();
        editor.apply();
        Intent intent=new Intent(activity,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.finish();
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
    public static void addMessageToDb(JSONObject object){
        String ConversationId=object.optString("senderUuid")+Core.getPreference().getString(SharedPreferencesKeys.UUID, "null");
        String senderUuid=object.optString("senderUuid");
        String message =object.optString("message");
        String MessageUid=object.optString("MsgUid");
        String ReplyTOMessageUid=object.optString("ReplyTOMsgUid");
        String type=object.optString("type");
        String timestamp=object.optString("timestamp");
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                DataBase.getInstance().dao().insertMessage(new MessageSchema(
                        MessageUid,
                        ConversationId,
                        ReplyTOMessageUid,
                        senderUuid,
                        Core.getPreference().getString(SharedPreferencesKeys.UUID,null),
                        message,
                        type,
                        timestamp,
                        -1,
                        false
                ));
            }
        });

    }


}
