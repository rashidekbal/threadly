package com.rtech.threadly.utils;

import static com.rtech.threadly.RoomDb.DataBase.getInstance;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import android.util.Log;
import android.util.TypedValue;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.messaging.FirebaseMessaging;
import com.rtech.threadly.RoomDb.DataBase;
import com.rtech.threadly.RoomDb.schemas.MessageSchema;
import com.rtech.threadly.RoomDb.schemas.NotificationSchema;
import com.rtech.threadly.SocketIo.SocketManager;
import com.rtech.threadly.Threadly;
import com.rtech.threadly.activities.authActivities.loginActivities.LoginActivity;
import com.rtech.threadly.activities.UserProfileActivity;
import com.rtech.threadly.constants.SharedPreferencesKeys;
import com.rtech.threadly.constants.TypeConstants;
import com.rtech.threadly.core.Core;
import com.rtech.threadly.interfaces.NetworkCallbackInterface;
import com.rtech.threadly.network_managers.FcmManager;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.Executors;

public class ReUsableFunctions {
    public static void openProfile(Context c, String userid) {
        Intent intent = new Intent(c, UserProfileActivity.class);
        intent.putExtra("userid", userid);
        c.startActivity(intent);
    }

    public static boolean isEmail(String input) {
        return input.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    }

    public static boolean isPhone(String input) {
        return input.length() == 10 && input.matches("\\d+");
    }

    public static void ShowToast(Context c, String message) {
        android.widget.Toast.makeText(c, message, android.widget.Toast.LENGTH_SHORT).show();
    }

    public static void ShowToast(String message) {
        android.widget.Toast.makeText(Threadly.getGlobalContext(), message, android.widget.Toast.LENGTH_SHORT).show();
    }

    public static void logoutWithoutActivity() {
        SharedPreferences loginInfo = Core.getPreference();
        SharedPreferences.Editor editor = loginInfo.edit();
        editor.clear();
        editor.apply();
        SocketManager.getInstance().disconnect();
        Executors.newSingleThreadExecutor().execute(() -> DataBase.getInstance().clearAllTables());
        Intent intent = new Intent(Threadly.getGlobalContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Threadly.getGlobalContext().startActivity(intent);

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
        while (true) {
            assert inputStream != null;
            if ((len = inputStream.read(buffer)) == -1)
                break;
            outputStream.write(buffer, 0, len);
        }

        outputStream.close();
        inputStream.close();

        return file;
    }

    public static void updateFcmTokenToServer() {

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            String token = task.getResult();
            if (token != null) {
                FcmManager.UpdateFcmToken(token, new NetworkCallbackInterface() {
                    @Override
                    public void onSuccess() {

                        Core.getPreference().edit().putBoolean(SharedPreferencesKeys.IS_FCM_TOKEN_UPLOADED, true)
                                .apply();
                    }

                    @Override
                    public void onError(String err) {

                    }
                });

            }
        });

    }

    public static String GenerateUUid() {
        return (UUID.randomUUID().toString());

    }

    public static String getTimestamp() {

        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(now);

    }

    public static void updateMessageStatus(String MsgUid, int status) {
        Executors.newSingleThreadExecutor()
                .execute(() -> getInstance().MessageDao().updateDeliveryStatus(MsgUid, status));
        Executors.newSingleThreadExecutor().execute(() -> {
            MessageSchema messageSchema = DataBase.getInstance().MessageDao().getMessageWithUid(MsgUid);
            Log.d("check", "updateMessageStatus: " + messageSchema.getMediaUploadState());
        });
    }

    public static void DeleteMessage(String messageUid) {
        Executors.newSingleThreadExecutor()
                .execute(() -> DataBase.getInstance().MessageDao().deleteMessage(messageUid));
    }

    // TODO consider media type message with grace and all factors
    public static void resendPendingMessages() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<MessageSchema> pendingToSendMessagesList = DataBase.getInstance().MessageDao()
                    .getPendingToSendMessages();
            if (pendingToSendMessagesList.isEmpty()) {
                return;
            }

            for (MessageSchema msg : pendingToSendMessagesList) {
                if (msg.getType().equals(TypeConstants.IMAGE) || msg.getType().equals(TypeConstants.VIDEO)) {
                    if (msg.getPostLink() == null) {
                        continue;
                    }
                }
                try {
                    Core.sendCtoS(msg);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }

        });
    }

    // Helper method to convert dp to pixels
    public static int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources().getDisplayMetrics());
    }

    public static String toIso8601Utc(String mysqlTimestamp) {
        try {
            // Step 1: Parse MySQL timestamp (local time)
            SimpleDateFormat mysqlFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            mysqlFormat.setTimeZone(TimeZone.getDefault()); // interpret as local

            Date date = mysqlFormat.parse(mysqlTimestamp);

            // Step 2: Convert to ISO 8601 (UTC)
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            assert date != null;
            return isoFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void addNotification(NotificationSchema schema) {
        Log.d("addingNotification", "addNotification: going to add ");
        Executors.newSingleThreadExecutor()
                .execute(() -> DataBase.getInstance().notificationDao().addNotification(schema));
    }

    public static void MarkAllNotificationRead() {
        Executors.newSingleThreadExecutor()
                .execute(() -> DataBase.getInstance().notificationDao().markAllNotificationsAsViewed());
    }

    public static void hideKeyboard(AppCompatActivity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
    }

    public static boolean isLoggedIn() {
        return Core.getPreference().getBoolean(SharedPreferencesKeys.IS_LOGGED_IN, false);
    }

}
