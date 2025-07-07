package com.rtech.gpgram.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionManagementUtil {
    public static boolean isAllPermissionGranted(Context context,String[] permission){
        if(context!=null&&permission.length!=0){
            for(String permissionName:permission){
                if (ContextCompat.checkSelfPermission(context, permissionName) != PackageManager.PERMISSION_GRANTED) return false;

            }
            return true;

        }else{
            return false;
        }
    }
    public static void requestPermission(Activity activity, String[] permission, int permissionCode){
        ActivityCompat.requestPermissions(activity,permission,permissionCode);
    }
}
