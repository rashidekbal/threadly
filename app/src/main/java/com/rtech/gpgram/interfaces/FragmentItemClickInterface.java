package com.rtech.gpgram.interfaces;

import android.view.View;

import androidx.annotation.Nullable;

public interface FragmentItemClickInterface {
    void onItemClick(@Nullable View v);
    void onfragmentDestroy();
}
