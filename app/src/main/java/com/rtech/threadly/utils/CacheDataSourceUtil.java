package com.rtech.threadly.utils;

import android.content.Context;

import androidx.annotation.OptIn;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.datasource.cache.CacheDataSource;

public class CacheDataSourceUtil {
    @OptIn(markerClass = UnstableApi.class)
    public static DataSource.Factory getCacheDataSourceFactory(Context context) {
        DefaultDataSource.Factory upstreamFactory = new DefaultDataSource.Factory(context);

        return new CacheDataSource.Factory()
                .setCache(ExoPlayerCache.getInstance(context))
                .setUpstreamDataSourceFactory(upstreamFactory)
                .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
    }
}
