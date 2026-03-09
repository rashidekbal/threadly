package com.rtech.threadly.models;

import android.net.Uri;

public class MediaModel {
    public Uri uri;
    public boolean isVideo;
    public int duration;
    public boolean isCameraIntent;

    public MediaModel(Uri uri, boolean isVideo, int duration,boolean isCameraIntent){
        this.uri=uri;
        this.isVideo=isVideo;
        this.duration=duration;
        this.isCameraIntent=isCameraIntent;


    }

    public boolean isCameraIntent() {
        return isCameraIntent;
    }

    public void setCameraIntent(boolean cameraIntent) {
        isCameraIntent = cameraIntent;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
