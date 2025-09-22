package com.rtech.threadly.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.rtech.threadly.Threadly;

public class DownloadManagerUtil {
    private static final DownloadManager downloadManager=(DownloadManager) Threadly.getGlobalContext().getSystemService(Context.DOWNLOAD_SERVICE);
    public static DownloadManager getDownloadManager(){
        return downloadManager;
    }
    public static void downloadFromUri(Context context,Uri uri){

        DownloadManager.Request request=new DownloadManager.Request(uri);
        request.setTitle("Downloading");
        request.setDescription("post downloading...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,uri.getLastPathSegment());
        Toast.makeText(context,"downloading...",Toast.LENGTH_SHORT).show();
        downloadManager.enqueue(request);
    }
}
