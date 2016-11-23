package com.houxya.bthelper.i;

/**
 * Created by Houxy on 2016/11/1.
 */

public interface IConnectionListener {

    void OnConnectionStart();
    void OnConnectionSuccess();
    void OnConnectionFailed(Exception e);
}
