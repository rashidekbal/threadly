package com.rtech.threadly.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.OptIn;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.database.StandaloneDatabaseProvider;
import androidx.media3.datasource.cache.CacheEvictor;
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor;
import androidx.media3.datasource.cache.SimpleCache;

import com.rtech.threadly.Threadly;

import java.io.File;

public class ExoPlayerCache {
    @SuppressLint("UnsafeOptInUsageError")
    private static SimpleCache simpleCache;


    @OptIn(markerClass = UnstableApi.class)
    public static SimpleCache getInstance(Context context) {
        if (simpleCache == null) {
            File cacheDir = new File(context.getCacheDir(), "exo_media_cache");
            long cacheSize = 1000 * 1024 * 1024; // 1000 MB

            simpleCache = new SimpleCache(
                    cacheDir,
                    new LeastRecentlyUsedCacheEvictor(cacheSize),
                    new StandaloneDatabaseProvider(context)
            );
        }
        return simpleCache;
    }
}
