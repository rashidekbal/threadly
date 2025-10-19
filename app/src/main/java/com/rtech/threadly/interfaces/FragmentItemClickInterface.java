package com.rtech.threadly.interfaces;

import android.view.View;

import androidx.annotation.Nullable;

public interface FragmentItemClickInterface {
    void onItemClick(@Nullable View v);
    void onFragmentDestroy();
}
