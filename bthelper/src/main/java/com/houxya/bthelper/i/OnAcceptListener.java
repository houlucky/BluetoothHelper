package com.houxya.bthelper.i;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Houxy on 2016/11/5.
 */

public interface OnAcceptListener {

    void OnAcceptSuccess(BluetoothDevice device);
    void OnAcceptFailed(Exception e);

}
