package com.rtech.threadly.interfaces;

public interface NetworkCallbackInterface {
    void onSuccess();

    void onError(String err);
}
