package com.rtech.threadly.constants;

import android.Manifest;

public class Permissions {
    public static final String[]AddPostPermissionsApi33AndAbove={
            Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_MEDIA_IMAGES,
            android.Manifest.permission.READ_MEDIA_VIDEO
    };
    public static final String[]AddPostPermissionsApiBelow33={
            Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
}
