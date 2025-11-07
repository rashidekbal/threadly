package com.rtech.threadly.utils;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.OptIn;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;
import androidx.media3.ui.PlayerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.rtech.threadly.Threadly;

import java.io.File;

public class ExoplayerUtil {
    private static Context cont;
    private static ExoPlayer exoplayer;
    private static PlayerView currentPlayerView;
    public static void init(Context context){
        if(exoplayer==null){
            exoplayer=new ExoPlayer.Builder(context).build();
            exoplayer.setRepeatMode(ExoPlayer.REPEAT_MODE_ONE);
        }

cont=context;
    }
    public static ExoPlayer getExoplayer(){
        return exoplayer;
    }

    @UnstableApi
    public static void play(Uri uri, PlayerView playerView){
        if(exoplayer!=null){

            // Detach old surface
            if (currentPlayerView != null) {
                currentPlayerView.setPlayer(null);
            }
            currentPlayerView = playerView;
            MediaItem mediaItem=MediaItem.fromUri(uri);

            MediaSource mediaSource = new ProgressiveMediaSource.Factory(
                    CacheDataSourceUtil.getCacheDataSourceFactory(cont)
            ).createMediaSource(mediaItem);

            exoplayer.setMediaSource(mediaSource);
            playerView.setPlayer(exoplayer);
            exoplayer.prepare();
            exoplayer.play();
        }


    }
    @UnstableApi
    public static void play(Uri uri, PlayerView playerView, ImageView previewImageView){
        if(exoplayer!=null){
             ImageView previewView;
            // Detach old surface
            if (currentPlayerView != null) {
                currentPlayerView.setPlayer(null);
            }
            currentPlayerView = playerView;
//            previewView=previewImageView;
//            previewView.setVisibility(View.VISIBLE);
//            Glide.with(Threadly.getGlobalContext()).load(uri).thumbnail(0.1f).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(previewView);
            MediaItem mediaItem=MediaItem.fromUri(uri);

            MediaSource mediaSource = new ProgressiveMediaSource.Factory(
                    CacheDataSourceUtil.getCacheDataSourceFactory(cont)
            ).createMediaSource(mediaItem);

            exoplayer.setMediaSource(mediaSource);
            playerView.setPlayer(exoplayer);
            exoplayer.prepare();
            exoplayer.play();
//            previewView.setVisibility(View.GONE);
        }


    }
    @UnstableApi
    public static void playNoLoop(Uri uri, PlayerView playerView){
        if(exoplayer!=null){
            exoplayer.setRepeatMode(ExoPlayer.REPEAT_MODE_OFF);
            // Detach old surface
            if (currentPlayerView != null) {
                currentPlayerView.setPlayer(null);
            }
            currentPlayerView = playerView;
            MediaItem mediaItem=MediaItem.fromUri(uri);

            MediaSource mediaSource = new ProgressiveMediaSource.Factory(
                    CacheDataSourceUtil.getCacheDataSourceFactory(cont)
            ).createMediaSource(mediaItem);

            exoplayer.setMediaSource(mediaSource);
            playerView.setPlayer(exoplayer);
            exoplayer.prepare();
            exoplayer.play();
        }


    }
    @UnstableApi
    public static void play(File file, PlayerView playerView){
        if(exoplayer!=null){

            // Detach old surface
            if (currentPlayerView != null) {
                currentPlayerView.setPlayer(null);
            }
            currentPlayerView = playerView;
            MediaItem mediaItem=MediaItem.fromUri(Uri.fromFile(file));
            exoplayer.setMediaItem(mediaItem);
            playerView.setPlayer(exoplayer);
            exoplayer.prepare();
            exoplayer.play();
        }


    }
    @UnstableApi
    public static void playFromLocalUri(Uri uri, PlayerView playerView){
        if(exoplayer!=null){

            // Detach old surface
            if (currentPlayerView != null) {
                currentPlayerView.setPlayer(null);
            }
            currentPlayerView = playerView;
            MediaItem mediaItem=MediaItem.fromUri(uri);
            exoplayer.setMediaItem(mediaItem);
            playerView.setPlayer(exoplayer);
            exoplayer.prepare();
            exoplayer.play();
        }


    }
    public static void pause(){
        if(exoplayer!=null){
            exoplayer.setPlayWhenReady(false);
            exoplayer.pause();
        }
    }

    public static void resume(){
        if(exoplayer!=null){
            exoplayer.setPlayWhenReady(true);
            exoplayer.play();
        }
    }
    public static void stop() {
        if (exoplayer != null) {
            exoplayer.setPlayWhenReady(false);
            exoplayer.stop();
        }
    }
    public static void mute(){
        if(exoplayer!=null){
            exoplayer.setVolume(0f);

        }
    }
    public static void unMute(){
        if(exoplayer!=null){
            exoplayer.setVolume(1f);

        }
    }
    public static long[] getPlayingInfo(){
        return new long[]{
                exoplayer.getDuration(),exoplayer.getCurrentPosition()
        };

    }
    public static void seekTo(long position){
        if (exoplayer!=null){
            exoplayer.seekTo(position);

        }
    }

    @OptIn(markerClass = UnstableApi.class)
    public static void release() {
        if (exoplayer != null) {
            exoplayer.release();
            exoplayer = null;
        }
    }
}
